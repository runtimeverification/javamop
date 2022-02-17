package com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.runtimeverification.rvmonitor.java.rvj.RVMNameSpace;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.Node;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.PackageDeclaration;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.visitor.GenericVisitor;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.visitor.VoidVisitor;
import com.runtimeverification.rvmonitor.util.RVMException;

///TODO:  All this has__ methods are carbon copies with the names changed.
// This should really be refactored.
// -P
public class RVMonitorSpec extends Node implements Comparable<RVMonitorSpec> {
    private final int modifiers;
    private final String name;
    private final PackageDeclaration packageDeclaration;
    private final RVMParameters parameters;
    private final String inMethod;
    private final String declarations;
    private final List<EventDefinition> events;
    private final List<PropertyAndHandlers> properties;
    private final List<String> eventNames;

    private final RVMParameters commonParamInEvents;
    private final RVMParameters varsToSave;

    public RVMonitorSpec(PackageDeclaration packageDeclaration, int line,
            int column, int modifiers, String name,
            List<RVMParameter> parameters, String inMethod,
            String declarations, List<EventDefinition> events,
            List<PropertyAndHandlers> properties)
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
        RVMParameters commonParamInEvents = new RVMParameters(this.parameters);

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
        } catch (RVMException e) {
            throw new com.runtimeverification.rvmonitor.java.rvj.parser.main_parser.ParseException(
                    e.getMessage());
        }

        for (EventDefinition event : this.events) {
            RVMParameters param = event.getRVMParametersOnSpec();

            // commonParamInEvents = RVMParameters.intersectionSet(param,
            // commonParamInEvents);
        }
        this.commonParamInEvents = commonParamInEvents;

        this.varsToSave = new RVMParameters();

        for (PropertyAndHandlers prop : properties) {
            for (String category : prop.getHandlers().keySet()) {
                RVMParameters param = prop.getUsedParametersIn(category,
                        this.parameters);

                for (RVMParameter p : param) {
                    if (!this.commonParamInEvents.contains(p)) {
                        varsToSave.add(p);
                    }
                }
            }
        }

        for (EventDefinition event : events) {
            RVMParameters eventParam = event.getRVMParametersOnSpec();
            RVMParameters param = event.getUsedParametersIn(this.parameters);

            for (RVMParameter p : param) {
                if (!eventParam.contains(p)) {
                    varsToSave.add(p);
                }
            }
        }
    }

    public void setVarsInEvents() throws RVMException {
        int numStartEvent = 0;
        HashSet<String> duplicatedEventNames = new HashSet<String>();
        for (EventDefinition event : this.events) {
            if (event.isStartEvent())
                numStartEvent++;

            event.rvmParametersOnSpec = RVMParameters.intersectionSet(
                    event.rvmParameters, this.parameters);
            event.rvmParametersOnSpec = this.parameters
                    .sortParam(event.rvmParametersOnSpec);

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
                    while (RVMNameSpace.checkUserVariable(event.getId() + "_"
                            + idnum))
                        idnum++;

                    RVMNameSpace.addUserVariable(event.getId() + "_" + idnum);
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

    public RVMParameters getParameters() {
        return parameters;
    }

    public RVMParameters getCommonParamInEvents() {
        return commonParamInEvents;
    }

    public RVMParameters getVarsToSave() {
        return varsToSave;
    }

    public String getInMethod() {
        return inMethod;
    }

    public String getDeclarationsStr() {
        return declarations;
    }

    public List<EventDefinition> getEvents() {
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

    public List<PropertyAndHandlers> getPropertiesAndHandlers() {
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

        for (EventDefinition event : this.events) {
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

    private Boolean cachedHas__LOC = null;

    public boolean has__LOC() {
        if (cachedHas__LOC != null)
            return cachedHas__LOC.booleanValue();

        for (EventDefinition event : this.events) {
            String eventAction = event.getAction().toString();
            if (eventAction.indexOf("__LOC") != -1
                    || eventAction.indexOf("__DEFAULT_MESSAGE") != -1) {
                cachedHas__LOC = new Boolean(true);
                return true;
            }
        }
        for (PropertyAndHandlers prop : this.properties) {
            for (String handler : prop.getHandlers().values()) {
                if (handler.indexOf("__LOC") != -1
                        || handler.toString().indexOf("__DEFAULT_MESSAGE") != -1) {
                    cachedHas__LOC = new Boolean(true);
                    return true;
                }
            }
        }
        cachedHas__LOC = new Boolean(false);
        return false;
    }

    private Boolean cachedHas__ACTIVITY = null;

    public boolean has__ACTIVITY() {
        if (cachedHas__ACTIVITY != null)
            return cachedHas__ACTIVITY.booleanValue();

        for (EventDefinition event : this.events) {
            String eventAction = event.getAction().toString();
            if (eventAction.indexOf("__ACTIVITY") != -1) {
                cachedHas__ACTIVITY = new Boolean(true);
                return true;
            }
        }
        for (PropertyAndHandlers prop : this.properties) {
            for (String handler : prop.getHandlers().values()) {
                if (handler.indexOf("__ACTIVITY") != -1) {
                    cachedHas__ACTIVITY = new Boolean(true);
                    return true;
                }
            }
        }
        cachedHas__ACTIVITY = new Boolean(false);
        return false;
    }

    private Boolean cachedHas__SKIP = null;

    public boolean has__SKIP() {
        if (cachedHas__SKIP != null)
            return cachedHas__SKIP.booleanValue();

        for (EventDefinition event : this.events) {
            if (event.getAction() == null)
                continue;
            String eventAction = event.getAction().toString();
            if (eventAction.indexOf("__SKIP") != -1) {
                cachedHas__SKIP = new Boolean(true);
                return true;
            }
        }
        for (PropertyAndHandlers prop : this.properties) {
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

    private Boolean cachedHasNoParamEvent = null;

    public boolean hasNoParamEvent() {
        if (cachedHasNoParamEvent != null)
            return cachedHasNoParamEvent;

        for (EventDefinition event : getEvents()) {
            if (event.getRVMParametersOnSpec().size() == 0) {
                cachedHasNoParamEvent = true;
                return true;
            }
        }
        cachedHasNoParamEvent = false;
        return false;
    }

    @Override
    public int compareTo(RVMonitorSpec o) {
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

}
