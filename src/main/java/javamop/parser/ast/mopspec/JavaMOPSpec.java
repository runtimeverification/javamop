// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.mopspec;

import javamop.parser.ast.Node;
import javamop.parser.ast.PackageDeclaration;
import javamop.parser.ast.body.BodyDeclaration;
import javamop.parser.ast.stmt.BlockStmt;
import javamop.parser.ast.visitor.CheckThisJoinPointVisitor;
import javamop.parser.ast.visitor.GenericVisitor;
import javamop.parser.ast.visitor.VoidVisitor;
import javamop.util.MOPException;
import javamop.util.MOPNameSpace;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

///TODO:  All this has__ methods are carbon copies with the names changed.
// This should really be refactored.
// -P
public class JavaMOPSpec extends Node implements Comparable<JavaMOPSpec>{
    private final int modifiers;
    private final String name;
    private final PackageDeclaration packageDeclaration;
    private final MOPParameters parameters;
    private final String inMethod;
    private final List<BodyDeclaration> declarations;
    private final List<EventDefinition> events;
    private final List<PropertyAndHandlers> properties;
    private final List<String> eventNames;
    
    private MOPParameters commonParamInEvents;
    private final MOPParameters varsToSave;
    private String rawLogic;
    
    public JavaMOPSpec(PackageDeclaration packageDeclaration, int line, int column, int modifiers, String name, List<MOPParameter> parameters, String inMethod, List<BodyDeclaration> declarations,
                       List<EventDefinition> events, List<PropertyAndHandlers> properties) throws javamop.parser.main_parser.ParseException {
        super(line, column);
        this.packageDeclaration = packageDeclaration;
        this.modifiers = modifiers;
        this.name = name;
        this.parameters = new MOPParameters(parameters);
        this.inMethod = inMethod;
        this.declarations = declarations;
        this.events = events;
        this.properties = properties;
        this.eventNames = new ArrayList<String>();
        this.commonParamInEvents = new MOPParameters(this.parameters);
        
        for (EventDefinition event : this.events) {
            if (!this.eventNames.contains(event.getId()))
                this.eventNames.add(event.getId());
        }
        
        int idnum = 1;
        for (PropertyAndHandlers prop : this.properties)
            prop.propertyId = idnum++;
        
        // set variables in each event
        try {
            setVarsInEvents();
        } catch (MOPException e) {
            throw new javamop.parser.main_parser.ParseException(e.getMessage());
        }
        
        for (EventDefinition event : this.events) {
            MOPParameters param = event.getMOPParametersOnSpec();
            
            this.commonParamInEvents = MOPParameters.intersectionSet(param, this.commonParamInEvents);
        }
        
        this.varsToSave = new MOPParameters();
        
        for (PropertyAndHandlers prop : properties){
            for(String category: prop.getHandlers().keySet()){
                MOPParameters param = prop.getUsedParametersIn(category, this.parameters);
                
                for(MOPParameter p : param){
                    if(!this.commonParamInEvents.contains(p)){
                        varsToSave.add(p);
                    }
                }
            }
        }
        
        for (EventDefinition event : events){
            MOPParameters eventParam = event.getMOPParametersOnSpec();
            MOPParameters param = event.getUsedParametersIn(this.parameters);
            
            for(MOPParameter p : param){
                if(!eventParam.contains(p)){
                    varsToSave.add(p);
                }
            }
        }
    }
    
    public void setVarsInEvents() throws MOPException {
        int numStartEvent = 0;
        HashSet<String> duplicatedEventNames = new HashSet<String>();
        for (EventDefinition event : this.events) {
            if (event.isStartEvent())
                numStartEvent++;
            
            event.mopParametersOnSpec = MOPParameters.intersectionSet(event.mopParameters, this.parameters);
            event.mopParametersOnSpec = this.parameters.sortParam(event.mopParametersOnSpec);
            
            for (EventDefinition event2 : this.events) {
                if (event == event2)
                    continue;
                if (event.getId().equals(event2.getId())) {
                    event.duplicated = true;
                    duplicatedEventNames.add(event.getId());
                }
            }
        }
        
        if (numStartEvent == 0) {
            for (EventDefinition event : this.events) {
                event.startEvent = true;
            }
        }
        
        for (String eventName : duplicatedEventNames) {
            int idnum = 1;
            for (EventDefinition event : this.events) {
                if (event.getId().equals(eventName)) {
                    while (MOPNameSpace.checkUserVariable(event.getId() + "_" + idnum))
                        idnum++;
                    
                    MOPNameSpace.addUserVariable(event.getId() + "_" + idnum);
                    event.uniqueId = event.getId() + "_" + idnum;
                }
            }
        }
        
        for (int i = 0; i < this.events.size(); i++) {
            EventDefinition event = this.events.get(i);
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
    
    public MOPParameters getCommonParamInEvents(){
        return commonParamInEvents;
    }
    
    public MOPParameters getVarsToSave(){
        return varsToSave;
    }
    
    public String getInMethod() {
        return inMethod;
    }
    
    public List<BodyDeclaration> getDeclarations() {
        return declarations;
    }
    
    public String getDeclarationsStr(){
        String ret = "";
        
        if (declarations == null)
            return ret;
        
        for(BodyDeclaration decl : declarations)
            ret += decl.toString() + "\n";
        
        return ret;
    }
    
    public List<EventDefinition> getEvents() {
        return events;
    }
    
    private String cachedEventStr = null;
    
    public String getEventStr(){
        if(cachedEventStr != null)
            return cachedEventStr;
        cachedEventStr = "";
        for (String eventName : eventNames) {
            cachedEventStr += " " + eventName;
        }
        cachedEventStr = cachedEventStr.trim();
        
        return cachedEventStr;
    }
    
    public List<PropertyAndHandlers> getPropertiesAndHandlers() {
        return properties;
    }
    
    public boolean isPerThread(){
        return SpecModifierSet.isPerThread(modifiers);
    }
    
    public boolean isSync(){
        if(SpecModifierSet.isPerThread(modifiers))
            return false;
        
        return !SpecModifierSet.isUnSync(modifiers);
    }
    
    public boolean isCentralized(){
        if(SpecModifierSet.isPerThread(modifiers))
            return true; //if perthread, it always uses centralized indexing
            
            return !SpecModifierSet.isDecentralized(modifiers);
    }
    
    private Boolean cachedIsGeneral = null;
    
    public boolean isGeneral() {
        if (cachedIsGeneral != null)
            return cachedIsGeneral.booleanValue();
        
        for (EventDefinition event : this.events) {
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
    
    /***
    * 
    * Whether it is a enforce property or not.
    * 
    * @return True if this property is supposed to be enforced.
    */
    public boolean isEnforce() {
        return SpecModifierSet.isEnforce(this.getModifiers());
    }
    
    /***
    * 
    * Whether it is a avoid property or not.
    * 
    * @return True if this property is supposed to be avoided.
    */
    public boolean isAvoid() {
        return SpecModifierSet.isAvoid(this.getModifiers());
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
    
    public boolean hasSpecialModifier(String modifier) {
        for(EventDefinition event : events) {
            if(event.getAction().toString().contains(modifier)) {
                return true;
            }
        }
        for(PropertyAndHandlers property : properties) {
            for(BlockStmt handler : property.getHandlers().values()) {
                if(handler.toString().contains(modifier)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean has__LOC() {
        return hasSpecialModifier("__LOC") || hasSpecialModifier("__DEFAULT_MESSAGE");
    }
    
    public boolean has__STATICSIG() {
        return hasSpecialModifier("__STATICSIG");
    }
    
    public boolean has__SKIP() {
        return hasSpecialModifier("__SKIP");
    }
    
    private Boolean cachedHasThisJoinPoint = null;
    
    public boolean hasThisJoinPoint() {
        if(cachedHasThisJoinPoint != null)
            return cachedHasThisJoinPoint;
        
        for (EventDefinition event : this.events) {
            if(event.getAction() == null)
                continue;
            BlockStmt block = event.getAction();
            if (block.accept(new CheckThisJoinPointVisitor(), null)){
                cachedHasThisJoinPoint = new Boolean(true);
                return true;
            }
        }
        
        for (PropertyAndHandlers prop : this.properties) {
            for (BlockStmt handler : prop.getHandlers().values()) {
                if (handler.accept(new CheckThisJoinPointVisitor(), null)){
                    cachedHasThisJoinPoint = new Boolean(true);
                    return true;
                }
            }
        }
        cachedHasThisJoinPoint = new Boolean(false);
        return false;
    }
    
    private Boolean cachedHasNoParamEvent = null;
    
    public boolean hasNoParamEvent(){
        if (cachedHasNoParamEvent != null)
            return cachedHasNoParamEvent;
        
        for(EventDefinition event : getEvents()){
            if (event.getMOPParametersOnSpec().size() == 0){
                cachedHasNoParamEvent = true;
                return true;
            }
        }
        cachedHasNoParamEvent = false;
        return false;
    }
    
    
    public int compareTo(JavaMOPSpec o){
        return getName().compareTo(o.getName());
    }
    
    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
    
    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }

    public String getRawLogic() {
        return rawLogic;
    }

    public JavaMOPSpec setRawLogic(String rawLogic) {
        this.rawLogic = rawLogic;
        return this;
    }
}
