// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex.mopspec;

import javamop.parser.ast.PackageDeclaration;
import javamop.parser.ast.body.BodyDeclaration;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;
import javamop.parser.ast.mopspec.SpecModifierSet;
import javamop.parser.astex.ExtNode;
import javamop.parser.astex.visitor.GenericVisitor;
import javamop.parser.astex.visitor.VoidVisitor;
import javamop.util.MOPException;
import javamop.util.MOPNameSpace;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class JavaMOPSpecExt extends ExtNode {
    private final int modifiers;
    private final boolean isPublic;
    private final String name;
    private final MOPParameters parameters;
    private final String inMethod;
    private final PackageDeclaration packageDeclaration;
    private final List<BodyDeclaration> declarations;
    private final List<EventDefinitionExt> events;
    private final List<PropertyAndHandlersExt> properties;
    private final List<String> eventNames;
    private final List<ExtendedSpec> extendedSpecs;
    private String rawLogic;

    public JavaMOPSpecExt(PackageDeclaration packagedeclaration, int line, int column, boolean isPublic, int modifiers, String name, List<MOPParameter> parameters, String inMethod, List<ExtendedSpec> extendedSpecs,
                          List<BodyDeclaration> declarations, List<EventDefinitionExt> events, List<PropertyAndHandlersExt> properties) throws javamop.parser.main_parser.ParseException {
        super(line, column);
        this.packageDeclaration = packagedeclaration;   
        this.modifiers = modifiers;
        this.name = name;
        this.parameters = new MOPParameters(parameters);
        this.inMethod = inMethod;
        this.declarations = declarations;
        this.events = events;
        this.properties = properties;
        this.eventNames = new ArrayList<String>();
        this.extendedSpecs = extendedSpecs;
        this.isPublic = isPublic;
        
        for (EventDefinitionExt event : this.events) {
            if (!this.eventNames.contains(event.getId()))
                this.eventNames.add(event.getId());
        }
        
        int idnum = 1;
        for (PropertyAndHandlersExt prop : this.properties)
            prop.propertyId = idnum++;
        
        // set variables in each event
        try {
            setVarsInEvents();
        } catch (MOPException e) {
            throw new javamop.parser.main_parser.ParseException(e.getMessage());
        }
    }
    
    public void setVarsInEvents() throws MOPException {
        int numStartEvent = 0;
        HashSet<String> duplicatedEventNames = new HashSet<String>();
        for (EventDefinitionExt event : this.events) {
            if (event.isStartEvent())
                numStartEvent++;
            
            event.mopParametersOnSpec = MOPParameters.intersectionSet(event.mopParameters, this.parameters);
            event.mopParametersOnSpec = this.parameters.sortParam(event.mopParametersOnSpec);
            
            for (EventDefinitionExt event2 : this.events) {
                if (event == event2)
                    continue;
                if (event.getId().equals(event2.getId())) {
                    event.duplicated = true;
                    duplicatedEventNames.add(event.getId());
                }
            }
        }
        
        if (numStartEvent == 0) {
            for (EventDefinitionExt event : this.events) {
                event.startEvent = true;
            }
        }
        
        for (String eventName : duplicatedEventNames) {
            int idnum = 1;
            for (EventDefinitionExt event : this.events) {
                if (event.getId().equals(eventName)) {
                    while (MOPNameSpace.checkUserVariable(event.getId() + "_" + idnum))
                        idnum++;
                    
                    MOPNameSpace.addUserVariable(event.getId() + "_" + idnum);
                    event.uniqueId = event.getId() + "_" + idnum;
                }
            }
        }
        
        for (int i = 0; i < this.events.size(); i++) {
            EventDefinitionExt event = this.events.get(i);
            if (event.uniqueId == null)
                event.uniqueId = event.getId();
            event.idnum = i;
        }
    }
    
    public int getModifiers() {
        return modifiers;
    }
    
    public String getName() {
        return name;
    }
    
    public PackageDeclaration getPackage() {
        return packageDeclaration;
    }
    
    public MOPParameters getParameters() {
        return parameters;
    }
    
    public String getInMethod() {
        return inMethod;
    }
    
    public List<BodyDeclaration> getDeclarations() {
        return declarations;
    }
    
    public String getDeclarationsStr() {
        String ret = "";
        
        if (declarations == null)
            return ret;
        
        for (BodyDeclaration decl : declarations)
            ret += decl.toString() + "\n";
        
        return ret;
    }
    
    public List<EventDefinitionExt> getEvents() {
        return events;
    }
    
    private String cachedEventStr = null;
    
    public String getEventStr() {
        if (cachedEventStr != null)
            return cachedEventStr;
        cachedEventStr = "";
        for (String eventName : eventNames) {
            cachedEventStr += " " + eventName;
        }
        cachedEventStr = cachedEventStr.trim();
        
        return cachedEventStr;
    }
    
    public List<ExtendedSpec> getExtendedSpec() {
        return this.extendedSpecs;
    }
    
    public List<PropertyAndHandlersExt> getPropertiesAndHandlers() {
        return properties;
    }
    
    public boolean isPerThread() {
        return SpecModifierSet.isPerThread(modifiers);
    }
    
    public boolean isSync() {
        if (SpecModifierSet.isPerThread(modifiers))
            return false;
        
        return !SpecModifierSet.isUnSync(modifiers);
    }
    
    public boolean isCentralized() {
        if (SpecModifierSet.isPerThread(modifiers))
            return true; // if perthread, it always uses centralized indexing
            
            return !SpecModifierSet.isDecentralized(modifiers);
    }
    
    private Boolean cachedIsGeneral = null;
    
    public boolean isGeneral() {
        if (cachedIsGeneral != null)
            return cachedIsGeneral.booleanValue();
        
        for (EventDefinitionExt event : this.events) {
            if (event.isStartEvent()) {
                if (!event.getMOPParametersOnSpec().contains(parameters)) {
                    cachedIsGeneral = new Boolean(true);
                    return true;
                }
            }
        }
        cachedIsGeneral = new Boolean(false);
        return false;
    }
    
    public boolean isSuffixMatching() {
        return SpecModifierSet.isSuffix(this.getModifiers());
    }
    
    public boolean isFullBinding() {
        return SpecModifierSet.isFullBinding(this.getModifiers());
    }
    
    public boolean isConnected() {
        return SpecModifierSet.isConnected(this.getModifiers());
    }
    
    public boolean isMultiFormula() {
        return this.properties != null && this.properties.size() > 1;
    }
    
    public boolean isRaw() {
        return this.properties == null || this.properties.size() == 0;
    }
    
    /**
    * returns if the specification is extending other specifications.
    * 
    */
    public boolean hasExtend() {
        if (this.extendedSpecs == null)
            return false;
        else
            return (this.extendedSpecs.isEmpty() == false);
    }
    
    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
    
    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }
    
    public Boolean isCachedGeneral() {
        return this.cachedIsGeneral;
    }
    
    public String isCachedEventStr() {
        return this.cachedEventStr;
    }
    
    public boolean isPublic() {
        return this.isPublic;
    }

    public String getRawLogic() {
        return rawLogic;
    }

    public JavaMOPSpecExt setRawLogic(String rawLogic) {
        this.rawLogic = rawLogic;
        return this;
    }
}
