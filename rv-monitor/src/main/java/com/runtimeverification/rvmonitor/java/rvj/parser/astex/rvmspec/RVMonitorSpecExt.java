package com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.runtimeverification.rvmonitor.java.rvj.RVMNameSpace;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.PackageDeclaration;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.SpecModifierSet;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.ExtNode;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.visitor.GenericVisitor;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.visitor.VoidVisitor;
import com.runtimeverification.rvmonitor.util.RVMException;

public class RVMonitorSpecExt extends ExtNode {
    private final int modifiers;
    private final boolean isPublic;
    private final String name;
    private final RVMParameters parameters;
    private final String inMethod;
    private final PackageDeclaration packageDeclaration;
    private final String declarations;
    private final List<EventDefinitionExt> events;
    private final List<PropertyAndHandlersExt> properties;
    private final List<String> eventNames;
    private final List<ExtendedSpec> extendedSpecs;

    public RVMonitorSpecExt(PackageDeclaration packageDeclaration, int line,
            int column, boolean isPublic, int modifiers, String name,
            List<RVMParameter> parameters, String inMethod,
            List<ExtendedSpec> extendedSpecs, String declarations,
            List<EventDefinitionExt> events,
            List<PropertyAndHandlersExt> properties)
                    throws com.runtimeverification.rvmonitor.java.rvj.parser.main_parser.ParseException {
        super(line, column);
        this.packageDeclaration = packageDeclaration;
        this.modifiers = modifiers;
        this.name = name;
        this.parameters = new RVMParameters(parameters);
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
        } catch (RVMException e) {
            throw new com.runtimeverification.rvmonitor.java.rvj.parser.main_parser.ParseException(
                    e.getMessage());
        }
    }

    public void setVarsInEvents() throws RVMException {
        int numStartEvent = 0;
        HashSet<String> duplicatedEventNames = new HashSet<String>();
        for (EventDefinitionExt event : this.events) {
            if (event.isStartEvent())
                numStartEvent++;

            event.rvmParametersOnSpec = RVMParameters.intersectionSet(
                    event.rvmParameters, this.parameters);
            event.rvmParametersOnSpec = this.parameters
                    .sortParam(event.rvmParametersOnSpec);

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
                    while (RVMNameSpace.checkUserVariable(event.getId() + "_"
                            + idnum))
                        idnum++;

                    RVMNameSpace.addUserVariable(event.getId() + "_" + idnum);
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

    public RVMParameters getParameters() {
        return parameters;
    }

    public String getInMethod() {
        return inMethod;
    }

    public String getDeclarationsStr() {
        return declarations;
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
                if (!event.getRVMParametersOnSpec().contains(parameters)) {
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

    private Boolean cachedHas__LOC = null;
    private Boolean cachedHas__DEFAULT_MESSAGE = null;

    public boolean has__LOC() {
        if (cachedHas__LOC != null)
            return cachedHas__LOC.booleanValue();

        for (EventDefinitionExt event : this.events) {
            String eventAction = event.getAction().toString();
            if (eventAction.indexOf("__LOC") != -1
                    || eventAction.indexOf("__DEFAULT_MESSAGE") != -1) {
                cachedHas__LOC = new Boolean(true);
                return true;
            }
        }
        for (PropertyAndHandlersExt prop : this.properties) {
            for (String handler : prop.getHandlers().values()) {
                if (handler.indexOf("__LOC") != -1
                        || handler.indexOf("__DEFAULT_MESSAGE") != -1) {
                    cachedHas__LOC = new Boolean(true);
                    return true;
                }
            }
        }
        cachedHas__LOC = new Boolean(false);
        return false;
    }

    private Boolean cachedHas__SKIP = null;

    public boolean has__SKIP() {
        if (cachedHas__SKIP != null)
            return cachedHas__SKIP.booleanValue();

        for (EventDefinitionExt event : this.events) {
            if (event.getAction() == null)
                continue;
            String eventAction = event.getAction().toString();
            if (eventAction.indexOf("__SKIP") != -1) {
                cachedHas__SKIP = new Boolean(true);
                return true;
            }
        }
        for (PropertyAndHandlersExt prop : this.properties) {
            for (String handler : prop.getHandlers().values()) {
                if (handler.indexOf("__SKIP") != -1) {
                    cachedHas__SKIP = new Boolean(true);
                    return true;
                }
            }
        }
        cachedHas__SKIP = new Boolean(false);
        return false;
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

    public Boolean isCachedHas__LOC() {
        return this.cachedHas__LOC;
    }

    public Boolean isCashedHas__SKIP() {
        return this.cachedHas__SKIP;
    }

    public boolean isPublic() {
        return this.isPublic;
    }

}
