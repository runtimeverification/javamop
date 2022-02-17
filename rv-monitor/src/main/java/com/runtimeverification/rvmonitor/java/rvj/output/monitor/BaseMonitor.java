package com.runtimeverification.rvmonitor.java.rvj.output.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.java.rvj.output.OptimizedCoenableSet;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMJavaCode;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMJavaCodeNoNewLine;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.UserJavaCode;
import com.runtimeverification.rvmonitor.java.rvj.output.Util;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeArrayLookup;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeAssignStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeBinOpExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeBreakStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeConditionStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeExprStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeFieldRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeLiteralExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMemberField;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMemberMethod;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMethodInvokeExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeNewExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeReturnStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmtCollection;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeThisRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeVarDeclStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeVarRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeWhileStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeFormatters;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeRVType;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.GlobalLock;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.reftree.RefTree;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.EventDefinition;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.PropertyAndHandlers;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;
import com.runtimeverification.rvmonitor.util.RVMException;

class PropMonitor {
    RVMJavaCode cloneCode;
    RVMJavaCode localDeclaration;
    RVMJavaCode stateDeclaration;
    RVMJavaCode resetCode;
    RVMJavaCode hashcodeCode;
    RVMJavaCode equalsCode;
    RVMJavaCode initilization;

    RVMVariable hashcodeMethod = null;

    HashMap<String, RVMVariable> categoryVars = new HashMap<String, RVMVariable>();
    HashMap<String, HandlerMethod> handlerMethods = new HashMap<String, HandlerMethod>();
    HashMap<String, RVMVariable> eventMethods = new HashMap<String, RVMVariable>();

    public String getStateDeclarationCode(boolean omitState) {
        if (omitState)
            return this.stateDeclaration.getWithoutStateDeclaration();
        return this.stateDeclaration.toString();
    }

    public String getInitializationCode(boolean omitState) {
        if (omitState)
            return this.initilization.getWithoutState();
        return this.initilization.toString();
    }

    public String getResetCode(boolean omitState) {
        if (omitState)
            return this.resetCode.getWithoutState();
        return this.resetCode.toString();
    }

    public int getInitialState() {
        return this.initilization.getStateRHS();
    }

    public int getResetState() {
        return this.initilization.getStateRHS();
    }

    public boolean isSimpleFSM() {
        try {
            this.getInitialState();
            this.getResetState();
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

public class BaseMonitor extends Monitor {
    // fields
    final RVMVariable lastevent = new RVMVariable("RVM_lastevent");
    public static RVMVariable skipEvent = new RVMVariable("skipEvent");
    private final RVMVariable conditionFail = new RVMVariable(
            "RVM_conditionFail");

    private boolean atomicMonitorTried = false;
    private CodeMemberField pairValueField;

    public final boolean isAtomicMoniorUsed() {
        if (!this.atomicMonitorTried)
            throw new IllegalStateException();
        return this.pairValueField != null;
    }

    // info about spec
    List<PropertyAndHandlers> props;
    List<EventDefinition> events;
    private RVMParameters specParam;
    private UserJavaCode monitorDeclaration;
    private boolean existCondition = false;
    private boolean existSkip = false;
    private HashMap<RVMParameter, RVMVariable> varsToSave = new HashMap<RVMParameter, RVMVariable>();

    final HashMap<PropertyAndHandlers, PropMonitor> propMonitors = new HashMap<PropertyAndHandlers, PropMonitor>();

    public BaseMonitor(String name, RVMonitorSpec rvmSpec,
            OptimizedCoenableSet coenableSet, boolean isOutermost)
                    throws RVMException {
        this(name, rvmSpec, coenableSet, isOutermost, "");
    }

    public BaseMonitor(String name, RVMonitorSpec rvmSpec,
            OptimizedCoenableSet coenableSet, boolean isOutermost,
            String monitorNameSuffix) throws RVMException {
        super(name, rvmSpec, coenableSet, isOutermost);
        this.initialize(name, rvmSpec, coenableSet, isOutermost,
                monitorNameSuffix);
    }

    private void checkIfAtomicMonitorCanBeEnabled() {
        MonitorFeatures feature = this.getFeatures();

        CodeMemberField pair = null;
        if (Main.useAtomicMonitor) {
            boolean simple = true;
            for (PropertyAndHandlers prop : props) {
                PropMonitor propMonitor = propMonitors.get(prop);
                if (!propMonitor.isSimpleFSM()) {
                    simple = false;
                    break;
                }
            }

            // It seems lastEvent is changed here only if isOutermost is true.
            if (simple && isOutermost && monitorInfo == null
                    && !feature.isTimeTrackingNeeded())
                pair = new CodeMemberField("pairValue", false, false, false,
                        CodeType.AtomicInteger());
        }
        this.pairValueField = pair;

        this.atomicMonitorTried = true;

        feature.setSelfSynchroniztionNeeded(Main.useFineGrainedLock
                && !this.isAtomicMoniorUsed());
    }

    public void initialize(String name, RVMonitorSpec rvmSpec,
            OptimizedCoenableSet coenableSet, boolean isOutermost,
            String monitorNameSuffix) {
        this.isDefined = true;
        this.monitorName = new RVMVariable(getOutputName() + monitorNameSuffix
                + "Monitor");
        this.events = rvmSpec.getEvents();
        this.props = rvmSpec.getPropertiesAndHandlers();
        this.monitorDeclaration = new UserJavaCode(rvmSpec.getDeclarationsStr());
        this.specParam = rvmSpec.getParameters();

        if (isOutermost) {
            varInOutermostMonitor = new VarInOutermostMonitor(name, rvmSpec,
                    rvmSpec.getEvents());
            monitorTermination = new MonitorTermination(name, rvmSpec,
                    rvmSpec.getEvents(), coenableSet);
        }

        String prefix = Main.merge ? this.monitorName + "_" : "";

        for (PropertyAndHandlers prop : props) {
            PropMonitor propMonitor = new PropMonitor();

            HashSet<String> cloneLocal = new HashSet<String>();
            cloneLocal.add("ret");

            propMonitor.cloneCode = new RVMJavaCode(prop,
                    prop.getLogicProperty("clone"), monitorName, cloneLocal);
            propMonitor.localDeclaration = new RVMJavaCode(prop,
                    prop.getLogicProperty("local declaration"), monitorName);
            propMonitor.stateDeclaration = new RVMJavaCode(prop,
                    prop.getLogicProperty("state declaration"), monitorName);
            propMonitor.resetCode = new RVMJavaCode(prop,
                    prop.getLogicProperty("reset"), monitorName);
            propMonitor.hashcodeCode = new RVMJavaCode(prop,
                    prop.getLogicProperty("hashcode"), monitorName);
            propMonitor.equalsCode = new RVMJavaCode(prop,
                    prop.getLogicProperty("equals"), monitorName);
            propMonitor.initilization = new RVMJavaCode(prop,
                    prop.getLogicProperty("initialization"), monitorName);

            HashMap<String, String> handlerBodies = prop.getHandlers();
            for (String category : prop.getHandlers().keySet()) {
                if (category.equals("deadlock"))
                    continue;
                RVMVariable categoryVar = new RVMVariable(prefix + "Prop_"
                        + prop.getPropertyId() + "_Category_" + category);
                propMonitor.categoryVars.put(category, categoryVar);

                String handlerBody = handlerBodies.get(category);

                if (handlerBody.toString().length() != 0) {
                    propMonitor.handlerMethods
                    .put(category, new HandlerMethod(prop, category,
                            specParam,
                            rvmSpec.getCommonParamInEvents(),
                            varsToSave, handlerBody, categoryVar, this));

                }
            }
            for (EventDefinition event : events) {
                RVMVariable eventMethod = new RVMVariable("Prop_"
                        + prop.getPropertyId() + "_event_" + event.getId());

                propMonitor.eventMethods.put(event.getId(), eventMethod);
            }

            propMonitors.put(prop, propMonitor);
        }

        varsToSave = new HashMap<RVMParameter, RVMVariable>();
        for (RVMParameter p : rvmSpec.getVarsToSave()) {
            varsToSave.put(p, new RVMVariable("Ref_" + p.getName()));
        }

        if (this.isDefined && rvmSpec.isGeneral()) {
            if (rvmSpec.isFullBinding() || rvmSpec.isConnected())
                monitorInfo = new MonitorInfo(rvmSpec);
        }

        for (PropertyAndHandlers prop : rvmSpec.getPropertiesAndHandlers()) {
            if (!existSkip) {
                for (String handler : prop.getHandlers().values()) {
                    if (handler.indexOf("__SKIP") != -1) {
                        existSkip = true;
                        break;
                    }
                }
            }
        }

        for (EventDefinition event : events) {
            if (event.getCondition() != null
                    && event.getCondition().length() != 0) {
                existCondition = true;
                break;
            }
            if (event.has__SKIP()) {
                existSkip = true;
                break;
            }
        }
    }

    @Override
    public void setRefTrees(TreeMap<String, RefTree> refTrees) {
        this.refTrees = refTrees;

        if (monitorTermination != null)
            monitorTermination.setRefTrees(refTrees);
    }

    @Override
    public RVMVariable getOutermostName() {
        return monitorName;
    }

    @Override
    public Set<String> getNames() {
        Set<String> ret = new HashSet<String>();

        ret.add(monitorName.toString());
        return ret;
    }

    @Override
    public Set<RVMVariable> getCategoryVars() {
        HashSet<RVMVariable> ret = new HashSet<RVMVariable>();

        for (PropertyAndHandlers prop : props) {
            ret.addAll(propMonitors.get(prop).categoryVars.values());
        }

        return ret;
    }

    public String printEventMethod(PropertyAndHandlers prop,
            EventDefinition event, String methodNamePrefix) {
        String synch = this.getFeatures().isSelfSynchronizationNeeded() ? " synchronized "
                : " ";
        String ret = "";

        PropMonitor propMonitor = propMonitors.get(prop);

        int idnum = event.getIdNum();
        RVMJavaCode eventMonitoringCode = new RVMJavaCode(prop,
                prop.getEventMonitoringCode(event.getId()), monitorName);
        RVMJavaCode aftereventMonitoringCode = new RVMJavaCode(prop,
                prop.getAfterEventMonitoringCode(event.getId()), monitorName);
        RVMJavaCode monitoringBody = new RVMJavaCode(prop,
                prop.getLogicProperty("monitoring body"), monitorName);
        RVMJavaCode stackManage = new RVMJavaCode(prop,
                prop.getLogicProperty("stack manage"), monitorName);
        HashMap<String, RVMJavaCode> categoryConditions = new HashMap<String, RVMJavaCode>();
        RVMJavaCode eventAction = null;

        for (String handlerName : prop.getHandlers().keySet()) {
            if (handlerName.equals("deadlock"))
                continue;
            String conditionStr = prop.getLogicProperty(handlerName
                    + " condition");
            if (conditionStr.contains(":{")) {
                HashMap<String, String> conditions = new HashMap<String, String>();
                prop.parseMonitoredEvent(conditions, conditionStr);
                conditionStr = conditions.get(event.getId());
            }

            if (conditionStr != null) {
                categoryConditions.put(handlerName, new RVMJavaCodeNoNewLine(
                        prop, conditionStr, monitorName));
            }
        }

        if (prop == props.get(props.size() - 1)) {
            if (event.getAction() != null) {
                String eventActionStr = event.getAction();

                if (!Main.generateVoidMethods) {
                    eventActionStr = eventActionStr.replaceAll("return;",
                            "return true;");
                }
                eventActionStr = eventActionStr.replaceAll("__RESET",
                        "this.reset()");
                eventActionStr = eventActionStr.replaceAll("__DEFAULT_MESSAGE",
                        defaultMessage);
                // __DEFAULT_MESSAGE may contain __LOC, make sure to sub in
                // __DEFAULT_MESSAGE first
                // -P
                eventActionStr = eventActionStr.replaceAll("__LOC",
                        Util.defaultLocation);
                // "this." + loc);
                eventActionStr = eventActionStr.replaceAll("__ACTIVITY",
                        "this." + activity);
                eventActionStr = eventActionStr.replaceAll("__SKIP", "this."
                        + skipEvent + " = true");

                eventAction = new RVMJavaCode(eventActionStr);
            }
        }

        // Add return value to events, so we know whether it's because of the
        // condition failure or not.
        // However, it seems the return value is always 'true' for monitoring.
        // To eliminate unnecessary
        // hassle, I let it generate 'void' methods for monitoring.
        boolean retbool = !Main.generateVoidMethods;
        ret += "final" + synch + (retbool ? "boolean " : "void ")
                + methodNamePrefix
                + propMonitor.eventMethods.get(event.getId()) + "(";
        // CL: Let's not pass parameters that are never referred to by the
        // user's action code.
        {
            RVMParameters params;
            if (Main.stripUnusedParameterInMonitor)
                params = event.getReferredParameters(event.getRVMParameters());
            else
                params = event.getRVMParameters();
            ret += params.parameterDeclString();
        }
        ret += ") {\n";

        if (prop == props.get(props.size() - 1) && eventAction != null) {
            for (RVMParameter p : event.getUsedParametersIn(specParam)) {
                if (!event.getRVMParametersOnSpec().contains(p)) {
                    RVMVariable v = this.varsToSave.get(p);

                    ret += p.getType() + " " + p.getName() + " = null;\n";
                    ret += "if(" + v + " != null){\n";
                    ret += p.getName() + " = (" + p.getType() + ")" + v
                            + ".get();\n";
                    ret += "}\n";
                }
            }

            ret += eventAction;
        }

        if (Main.internalBehaviorObserving) {
            ret += "this.trace.add(\"";
            ret += event.getId();
            ret += "\");\n";
        }

        for (RVMParameter p : varsToSave.keySet()) {
            if (event.getRVMParametersOnSpec().contains(p)) {
                RVMVariable v = varsToSave.get(p);

                ret += "if(" + v + " == null){\n";
                ret += v + " = new WeakReference(" + p.getName() + ");\n";
                ret += "}\n";
            }
        }

        if (isOutermost) {
            if (!this.isAtomicMoniorUsed())
                ret += lastevent + " = " + idnum + ";\n";
        }

        if (monitorInfo != null)
            ret += monitorInfo.union(event.getRVMParametersOnSpec());

        ret += propMonitors.get(prop).localDeclaration;

        ret += stackManage + "\n";

        if (!this.isAtomicMoniorUsed())
            ret += eventMonitoringCode;

        ret += monitoringBody;

        if (!this.isAtomicMoniorUsed()) {
            String categoryCode = "";
            for (Entry<String, RVMJavaCode> entry : categoryConditions
                    .entrySet()) {
                categoryCode += propMonitors.get(prop).categoryVars.get(entry
                        .getKey()) + " = " + entry.getValue() + ";\n";
            }

            if (monitorInfo != null)
                ret += monitorInfo.computeCategory(categoryCode);
            else
                ret += categoryCode;
        }

        if (this.isAtomicMoniorUsed()) {
            String tablevar = eventMonitoringCode.extractTableVariable();
            ret += this.getInternalEventHandlerCallCode(idnum, tablevar, prop,
                    categoryConditions);
        }

        ret += aftereventMonitoringCode;

        if (retbool)
            ret += "return true;\n";
        ret += "}\n";

        return ret;
    }

    public String printEventMethod(PropertyAndHandlers prop,
            EventDefinition event) {
        return this.printEventMethod(prop, event, "");
    }

    @Override
    public String Monitoring(RVMVariable monitorVar, EventDefinition event,
            RVMVariable loc, GlobalLock lock, String outputName,
            boolean inMonitorSet) {
        String ret = "";

        // if (has__LOC) {
        // if(loc != null)
        // ret += monitorVar + "." + this.loc + " = " + loc + ";\n";
        // else
        // ret += monitorVar + "." + this.loc + " = " +
        // "Thread.currentThread().getStackTrace()[2].toString()"
        // + ";\n";
        // }

        if (event.isBlockingEvent())
            ret += "boolean cloned_monitor_condition_satisfied = true;\n";

        for (PropertyAndHandlers prop : props) {
            PropMonitor propMonitor = propMonitors.get(prop);

            ret += this.beforeEventMethod(monitorVar, prop, event, lock,
                    outputName, inMonitorSet);

            RVMVariable finalMonitor = new RVMVariable(monitorVar
                    + "finalMonitor");
            ret += "final " + this.monitorName + " " + finalMonitor + " = "
                    + monitorVar + ";\n";
            String handlerCode = getHandlerCallingCode(finalMonitor, event,
                    propMonitor);
            if (!event.isBlockingEvent()) {
                ret += monitorVar + "."
                        + propMonitor.eventMethods.get(event.getId()) + "(";
                {
                    RVMParameters passing;
                    if (Main.stripUnusedParameterInMonitor)
                        passing = event.getReferredParameters(event
                                .getRVMParameters());
                    else
                        passing = event.getRVMParameters();
                    ret += passing.parameterString();
                }
                ret += ");\n";
            } else {
                // Copy parameters to final variables
                List<String> finalParameters = new ArrayList<String>();
                for (RVMParameter p : event.getRVMParameters()) {
                    ret += "final " + p.getType() + " " + p.getName()
                            + "_final = " + p.getName() + ";\n";
                    finalParameters.add(p.getName() + "_final");
                }

                String methodName = propMonitor.eventMethods.get(event.getId())
                        .toString();
                String threadName = finalMonitor + "_thread";
                // String eventName = this.pointcutName.toString().substring(0,
                // this.pointcutName.toString().length() - 5);
                ret += "com.runtimeverification.rvmonitor.java.rt.concurrent.BlockingEventThread "
                        + threadName
                        + " = new com.runtimeverification.rvmonitor.java.rt.concurrent.BlockingEventThread(\""
                        + event.getId() + "\") {\n";

                ret += "public void execEvent() {\n";

                // Acquire lock
                if (lock != null) {
                    ret += lock.getAcquireCode();
                }

                // Call the real event method
                ret += finalMonitor + "." + methodName + "(";

                String finalParameter = "";
                for (String p : finalParameters) {
                    finalParameter += ", " + p;
                }
                if (finalParameter.length() != 0) {
                    finalParameter = finalParameter.substring(2);
                }
                ret += finalParameter;
                ret += ");\n";

                // SignalAll
                if (lock != null) {
                    ret += lock.getName() + "_cond.signalAll();\n";
                }
                ret += handlerCode;

                // Release lock
                if (lock != null) {
                    ret += lock.getReleaseCode();
                }

                ret += " }\n";
                ret += " };\n";

                // Set name of the blocking event method thread to be the same
                // name
                ret += threadName
                        + ".setName(Thread.currentThread().getName());\n";
                // Start the blocking event method thread
                // See if condition is satisfied, otherwise won't start the
                // thread

                ret += "if (cloned_monitor_condition_satisfied) {\n";
                if (lock != null) {
                    ret += lock.getReleaseCode();
                }
                ret += threadName + ".start();\n";
                if (lock != null) {
                    ret += lock.getAcquireCode();
                }
                ret += "}\n";
                ret += "\n";
            }

            if (!event.isBlockingEvent()) {
                ret += this.afterEventMethod(monitorVar, prop, event, lock,
                        outputName);
                ret += handlerCode;
            }
        }

        return ret;
    }

    private String getHandlerCallingCode(RVMVariable monitorVar,
            EventDefinition event, PropMonitor propMonitor) {
        String ret = "";
        if (event.getCondition() != null && event.getCondition().length() != 0) {
            ret += "if(" + monitorVar + "." + conditionFail + "){\n";
            ret += monitorVar + "." + conditionFail + " = false;\n";
            ret += "} else {\n";
        }

        for (String category : propMonitor.handlerMethods.keySet()) {
            if (category.equals("deadlock"))
                continue;
            HandlerMethod handlerMethod = propMonitor.handlerMethods
                    .get(category);

            final RVMVariable rvmVariable = propMonitor.categoryVars
                    .get(category);
            if (!Main.eliminatePresumablyRemnantCode) {
                ret += BaseMonitor.getNiceVariable(rvmVariable) + " |= "
                        + monitorVar + "." + rvmVariable + ";\n";
            }

            // Generate code to trigger handler
            ret += "if(" + monitorVar + "." + rvmVariable + ") {\n";
            ret += monitorVar + "." + handlerMethod.getMethodName() + "(";
            if (!Main.stripUnusedParameterInMonitor)
                ret += event.getRVMParametersOnSpec().parameterStringIn(
                        specParam);
            ret += ");\n";

            ret += "}\n";
        }
        if (existSkip) {
            ret += skipEvent + " |= " + monitorVar + "." + skipEvent + ";\n";
            ret += monitorVar + "." + skipEvent + " = false;\n";
        }

        if (event.getCondition() != null && event.getCondition().length() != 0) {
            ret += "}\n";
        }
        return ret;
    }

    public String afterEventMethod(RVMVariable monitor,
            PropertyAndHandlers prop, EventDefinition event, GlobalLock l,
            String outputName) {
        return "";
    }

    public String beforeEventMethod(RVMVariable monitor,
            PropertyAndHandlers prop, EventDefinition event, GlobalLock l,
            String outputName, boolean inMonitorSet) {
        return "";
    }

    @Override
    public MonitorInfo getMonitorInfo() {
        return monitorInfo;
    }

    @Override
    public String toString() {
        this.checkIfAtomicMonitorCanBeEnabled();

        MonitorFeatures feature = this.getFeatures();
        String synch = feature.isSelfSynchronizationNeeded() ? " synchronized "
                : " ";

        String ret = "";

        String interfaceName = null;

        if (feature.isDisableHolderNeeded()) {
            interfaceName = feature.getInterfaceName(this.monitorName
                    .getVarName());
            ret += "interface " + interfaceName
                    + " extends IMonitor, IDisableHolder {\n";
            ret += "}\n\n";

            String holderName = feature.getDisableHolderName(this.monitorName
                    .getVarName());
            ret += "class " + holderName + " extends DisableHolder implements "
                    + interfaceName;
            if (Main.internalBehaviorObserving)
                ret += ", IObservableObject";
            ret += " {\n";
            ret += holderName + "(long tau) {\n";
            ret += "super(tau);\n";
            if (Main.internalBehaviorObserving)
                ret += "this.holderid = ++nextid;\n";
            ret += "}\n\n";

            // IMonitor.isTerminated()
            {
                ret += "@Override\n";
                ret += "public final boolean isTerminated() {\n";
                ret += "return false;\n";
                ret += "}\n\n";
            }

            // IMonitor.getLastEvent()
            {
                ret += "@Override\n";
                ret += "public int getLastEvent() {\n";
                ret += "return -1;\n";
                ret += "}\n\n";
            }

            // IMonitor.getState()
            {
                ret += "@Override\n";
                ret += "public int getState() {\n";
                ret += "return -1;\n";
                ret += "}\n\n";
            }

            if (Main.internalBehaviorObserving) {
                ret += "private int holderid;\n";
                ret += "private static int nextid;\n\n";
                ret += "@Override\n";
                ret += "public final String getObservableObjectDescription() {\n";
                ret += "StringBuilder s = new StringBuilder();\n";
                ret += "s.append('#');\n";
                ret += "s.append(this.holderid);\n";
                ret += "s.append(\"{t:\");\n";
                ret += "s.append(this.getTau());\n";
                ret += "s.append(\",dis:\");\n";
                ret += "s.append(this.getDisable());\n";
                ret += "s.append('}');\n";
                ret += "return s.toString();\n";
                ret += "}\n";
            }
            ret += "}\n\n";
        }

        ret += "class " + monitorName;
        if (isOutermost) {
            String clsname = this.isAtomicMoniorUsed() ? "AbstractAtomicMonitor"
                    : "AbstractSynchronizedMonitor";
            ret += " extends com.runtimeverification.rvmonitor.java.rt.tablebase."
                    + clsname;
        }
        ret += " implements Cloneable, com.runtimeverification.rvmonitor.java.rt.RVMObject";
        if (feature.isTimeTrackingNeeded()) {
            if (feature.isDisableHolderNeeded())
                ret += ", " + interfaceName;
            else
                ret += ", com.runtimeverification.rvmonitor.java.rt.tablebase.IDisableHolder";
        }
        if (Main.internalBehaviorObserving)
            ret += ", IObservableObject";
        ret += " {\n";

        if (isOutermost && varInOutermostMonitor != null)
            ret += varInOutermostMonitor;

        // clone()
        ret += "protected Object clone() {\n";
        if (Main.statistics) {
            ret += stat.incNumMonitor();
        }
        ret += "try {\n";
        ret += monitorName + " ret = (" + monitorName + ") super.clone();\n";
        if (monitorInfo != null)
            ret += monitorInfo.copy("ret", "this");
        for (PropertyAndHandlers prop : props)
            ret += propMonitors.get(prop).cloneCode;
        if (Main.internalBehaviorObserving) {
            ret += "ret.monitorid = ++nextid;\n";
            ret += "ret.trace = new ArrayList<String>();\n";
            ret += "ret.trace.addAll(this.trace);\n";
        }
        if (this.isAtomicMoniorUsed()) {
            ret += "ret.pairValue = new AtomicInteger(pairValue.get());\n";
        }
        ret += "return ret;\n";
        ret += "}\n";
        ret += "catch (CloneNotSupportedException e) {\n";
        ret += "throw new InternalError(e.toString());\n";
        ret += "}\n";
        ret += "}\n";

        // monitor variables
        ret += monitorDeclaration + "\n";
        if (this.has__ACTIVITY)
            ret += activityCode();
        // if (this.has__LOC)
        // ret += "String " + loc + ";\n";
        // monitor statistics variables
        if (Main.statistics) {
            ret += stat.fieldDecl() + "\n";
        }

        // references for saved parameters
        for (RVMVariable v : varsToSave.values()) {
            ret += "WeakReference " + v + " = null;\n";
        }

        if (existCondition) {
            ret += "boolean " + conditionFail + " = false;\n";
        }
        if (existSkip) {
            ret += "boolean " + skipEvent + " = false;\n";
        }

        // state declaration
        for (PropertyAndHandlers prop : props) {
            PropMonitor propMonitor = propMonitors.get(prop);
            ret += propMonitor.getStateDeclarationCode(this
                    .isAtomicMoniorUsed());
        }
        ret += "\n";

        // category condition
        for (PropertyAndHandlers prop : props) {
            PropMonitor propMonitor = propMonitors.get(prop);
            for (String category : propMonitor.categoryVars.keySet()) {
                if (this.isAtomicMoniorUsed())
                    ret += "volatile ";
                ret += "boolean " + propMonitor.categoryVars.get(category)
                        + " = false;\n";
            }
        }
        ret += "\n";

        if (this.isAtomicMoniorUsed()) {
            ICodeFormatter fmt = CodeFormatters.getDefault();
            this.pairValueField.getCode(fmt);
            ret += fmt.getCode();
        }

        // constructor
        ret += monitorName + "(";
        {
            List<String> args = new ArrayList<String>();
            if (feature.isTimeTrackingNeeded())
                args.add("long tau");
            {
                RVMParameters params;
                if (feature.isNonFinalWeakRefsInMonitorNeeded()
                        || feature.isFinalWeakRefsInMonitorNeeded())
                    params = this.specParam;
                else
                    params = feature.getRememberedParameters();
                for (RVMParameter param : params)
                    args.add(this.monitorTermination.getRefType(param) + " "
                            + this.monitorTermination.references.get(param));
            }
            for (int i = 0; i < args.size(); ++i) {
                if (i > 0)
                    ret += ", ";
                ret += args.get(i);
            }
        }
        ret += ") {\n";
        if (feature.isTimeTrackingNeeded())
            ret += "this.tau = tau;\n";
        for (PropertyAndHandlers prop : props) {
            PropMonitor propMonitor = propMonitors.get(prop);
            ret += propMonitor.localDeclaration;
            if (this.isAtomicMoniorUsed()) {
                ret += propMonitor.getInitializationCode(this
                        .isAtomicMoniorUsed());
                int initstate = propMonitor.getInitialState();
                ret += this.getStateUpdateCode(initstate, true);
            } else
                ret += propMonitor.initilization;
            ret += "\n";
        }
        {
            RVMParameters params;
            if (feature.isNonFinalWeakRefsInMonitorNeeded()
                    || feature.isFinalWeakRefsInMonitorNeeded())
                params = this.specParam;
            else
                params = feature.getRememberedParameters();
            for (RVMParameter param : params) {
                RVMVariable var = this.monitorTermination.references.get(param);
                ret += "this." + var + " = " + var + ";\n";
            }
        }
        if (Main.statistics) {
            ret += stat.incNumMonitor();
        }
        if (Main.internalBehaviorObserving) {
            ret += "this.trace = new ArrayList<String>();\n";
            ret += "this.monitorid = ++nextid;\n";
        }
        ret += "}\n";
        ret += "\n";

        if (Main.statistics) {
            ret += stat.methodDecl() + "\n";
        }
        // implements getState(), getLastEvent() and other related things
        if (isOutermost) {
            if (this.isAtomicMoniorUsed())
                ret += this.generatePairValueRelatedMethods();
            else {
                String statevar = this.getStateVariable();
                ret += "@Override\n";
                ret += "public final int getState() {\n";
                ret += "return ";
                if (statevar == null)
                    ret += "-1";
                else
                    ret += statevar;
                ret += ";\n";
                ret += "}\n\n";
            }
        }

        if (feature.isTimeTrackingNeeded()) {
            ret += "private final long tau;\n";
            ret += "private long disable = -1;\n\n";
            ret += "@Override\n";
            ret += "public final long getTau() {\n";
            ret += "return this.tau;\n";
            ret += "}\n\n";
            ret += "@Override\n";
            ret += "public final long getDisable() {\n";
            ret += "return this.disable;\n";
            ret += "}\n\n";
            ret += "@Override\n";
            ret += "public final void setDisable(long value) {\n";
            ret += "this.disable = value;\n";
            ret += "}\n\n";
        }

        if (this.isAtomicMoniorUsed())
            ret += this.getInternalEventHandlerCode();

        // events
        for (PropertyAndHandlers prop : props) {
            for (EventDefinition event : this.events) {
                ret += this.printEventMethod(prop, event) + "\n";
            }
        }

        // handlers
        for (PropertyAndHandlers prop : props) {
            PropMonitor propMonitor = propMonitors.get(prop);
            for (HandlerMethod handlerMethod : propMonitor.handlerMethods
                    .values()) {
                ret += handlerMethod + "\n";
            }
        }

        // reset
        ret += "final" + synch + "void reset() {\n";
        if (monitorInfo != null)
            ret += monitorInfo.initConnected();
        if (isOutermost) {
            if (!this.isAtomicMoniorUsed())
                ret += lastevent + " = -1;\n";
        }
        for (PropertyAndHandlers prop : props) {
            PropMonitor propMonitor = propMonitors.get(prop);

            ret += propMonitor.localDeclaration;
            if (this.isAtomicMoniorUsed()) {
                ret += propMonitor.getResetCode(this.isAtomicMoniorUsed());
                int resetstate = propMonitor.getResetState();
                ret += this.getStateUpdateCode(resetstate, false);
            } else
                ret += propMonitor.resetCode;
            for (String category : propMonitor.categoryVars.keySet()) {
                ret += propMonitor.categoryVars.get(category) + " = false;\n";
            }
        }

        ret += "}\n";
        ret += "\n";

        // hashcode
        if (props.size() > 1) {
            boolean newHashCode = false;
            for (PropertyAndHandlers prop : props) {
                PropMonitor propMonitor = propMonitors.get(prop);
                if (!propMonitor.hashcodeCode.isEmpty()) {
                    newHashCode = true;

                    propMonitor.hashcodeMethod = new RVMVariable("Prop_"
                            + prop.getPropertyId() + "_hashCode");

                    ret += "final int " + propMonitor.hashcodeMethod + "() {\n";
                    ret += propMonitor.hashcodeCode;
                    ret += "}\n";
                }
            }
            if (newHashCode) {
                ret += "public final int hashCode() {\n";
                ret += "return ";
                boolean first = true;
                for (PropertyAndHandlers prop : props) {
                    PropMonitor propMonitor = propMonitors.get(prop);
                    if (propMonitor.hashcodeMethod != null) {
                        if (first) {
                            first = false;
                        } else {
                            ret += "^";
                        }

                        ret += propMonitor.hashcodeMethod + "()";
                    }
                }
                ret += ";\n";
                ret += "}\n";
                ret += "\n";
            }
        } else if (props.size() == 1) {
            for (PropertyAndHandlers prop : props) {
                PropMonitor propMonitor = propMonitors.get(prop);
                if (!propMonitor.hashcodeCode.isEmpty()) {

                    ret += "public final int hashCode() {\n";
                    ret += propMonitor.hashcodeCode;
                    ret += "}\n";
                    ret += "\n";
                }
            }
        }

        // equals
        // if there are more than 1 property, there is no state collapsing.
        if (props.size() == 1) {
            for (PropertyAndHandlers prop : props) {
                PropMonitor propMonitor = propMonitors.get(prop);
                if (!propMonitor.equalsCode.isEmpty()) {
                    ret += "public final boolean equals(Object o) {\n";
                    ret += propMonitor.equalsCode;
                    ret += "}\n";
                    ret += "\n";
                }
            }
        }

        // Other declarations/methods for subclasses
        ret += this.printExtraDeclMethods();

        // endObject and some declarations
        if (isOutermost) {
            String decl = this.isAtomicMoniorUsed() ? "int lastEvent = this.getLastEvent();\n"
                    : null;
            String lastEventVar = this.isAtomicMoniorUsed() ? "lastEvent"
                    : null;
            ret += monitorTermination.getCode(feature, decl, lastEventVar);
        }

        if (monitorInfo != null)
            ret += monitorInfo.monitorDecl();

        // # of events, # of states
        {
            ret += "public static int getNumberOfEvents() {\n";
            ret += "return " + this.events.size() + ";\n";
            ret += "}\n\n";
            ret += "public static int getNumberOfStates() {\n";
            ret += "return " + this.getNumberOfStates() + ";\n";
            ret += "}\n\n";
        }

        if (Main.internalBehaviorObserving) {
            ret += "private List<String> trace;\n";
            ret += "private int monitorid;\n";
            ret += "private static int nextid;\n";
            ret += "\n";
            ret += "@Override\n";
            ret += "public final String getObservableObjectDescription() {\n";
            ret += "StringBuilder s = new StringBuilder();\n";
            ret += "s.append('#');\n";
            ret += "s.append(this.monitorid);\n";
            if (feature.isTimeTrackingNeeded()) {
                ret += "s.append(\"{t:\");\n";
                ret += "s.append(this.tau);\n";
                ret += "s.append(\",dis:\");\n";
                ret += "s.append(this.disable);\n";
                ret += "s.append('}');\n";
            }
            ret += "s.append('[');\n";
            ret += "for (int i = 0; i < this.trace.size(); ++i) {\n";
            ret += "if (i > 0)\n";
            ret += "s.append(',');\n";
            ret += "s.append(this.trace.get(i));\n";
            ret += "}\n";
            ret += "s.append(']');\n";
            ret += "return s.toString();\n";
            ret += "}\n";
        }

        ret += "}\n";

        if (has__ACTIVITY) {
            ret = ret.replaceAll("__ACTIVITY", "this." + activity);
        }

        return ret;
    }

    private CodeMemberMethod getPairValueWrapper(CodeThisRefExpr thisRef,
            CodeFieldRefExpr pairValueRef, String methodname) {
        CodeMethodInvokeExpr getvalue = new CodeMethodInvokeExpr(
                CodeType.integer(), pairValueRef, "get");
        CodeMethodInvokeExpr getstate = new CodeMethodInvokeExpr(
                CodeType.integer(), thisRef, methodname, getvalue);
        CodeReturnStmt ret = new CodeReturnStmt(getstate);

        CodeMemberMethod method = new CodeMemberMethod(methodname, true, false,
                true, CodeType.integer(), true, ret);
        return method;
    }

    private CodeMemberMethod getPairValueExtracter(boolean state) {
        CodeVariable pairvalue = new CodeVariable(CodeType.integer(),
                "pairValue");

        CodeExpr result;
        {
            int numStateBits = this.getNumberOfStateBits();
            CodeVarRefExpr pairvalueref = new CodeVarRefExpr(pairvalue);
            if (state) {
                // pairValue & ((2 ^ numStateBits) - 1)
                int mask = (1 << numStateBits) - 1;
                result = CodeBinOpExpr.bitwiseAnd(pairvalueref,
                        CodeLiteralExpr.integer(mask));
            } else {
                // pairValue >> numStateBits
                result = CodeBinOpExpr.rightShift(pairvalueref,
                        CodeLiteralExpr.integer(numStateBits));
            }
        }

        String methodname = state ? "getState" : "getLastEvent";
        CodeReturnStmt ret = new CodeReturnStmt(result);

        CodeMemberMethod method = new CodeMemberMethod(methodname, false,
                false, true, CodeType.integer(), false, ret, pairvalue);
        return method;
    }

    private CodeMemberMethod getPairValueCalculator() {
        CodeVariable levtvar = new CodeVariable(CodeType.integer(), "lastEvent");
        CodeVariable statevar = new CodeVariable(CodeType.integer(), "state");

        CodeExpr result;
        {
            // lastEvent: [-1 ~ numEvents)
            // state: [0 ~ numStates)
            // value := ((lastEvent + 1) << numStateBits) | state
            int numStateBits = this.getNumberOfStateBits();
            result = CodeBinOpExpr.bitwiseOr(CodeBinOpExpr.leftShift(
                    CodeBinOpExpr.add(new CodeVarRefExpr(levtvar),
                            CodeLiteralExpr.integer(1)), CodeLiteralExpr
                            .integer(numStateBits)), new CodeVarRefExpr(
                                    statevar));
        }
        CodeReturnStmt ret = new CodeReturnStmt(result);

        CodeMemberMethod method = new CodeMemberMethod("calculatePairValue",
                false, false, true, CodeType.integer(), false, ret, levtvar,
                statevar);
        return method;
    }

    private String generatePairValueRelatedMethods() {
        List<CodeMemberMethod> methods = new ArrayList<CodeMemberMethod>();

        CodeThisRefExpr thisRef = new CodeThisRefExpr(this.getRuntimeType());
        CodeFieldRefExpr pairValueRef = new CodeFieldRefExpr(thisRef,
                this.pairValueField);

        // getState()
        methods.add(this.getPairValueWrapper(thisRef, pairValueRef, "getState"));

        // getLastEvent()
        methods.add(this.getPairValueWrapper(thisRef, pairValueRef,
                "getLastEvent"));

        // getState(int)
        methods.add(this.getPairValueExtracter(true));

        // getLastEvent(int)
        methods.add(this.getPairValueExtracter(false));

        // calculatePairValue(int, int)
        methods.add(this.getPairValueCalculator());

        ICodeFormatter fmt = CodeFormatters.getDefault();
        for (CodeMemberMethod method : methods)
            method.getCode(fmt);
        return fmt.getCode();
    }

    private String getStateUpdateCode(int newState, boolean createNew) {
        CodeStmtCollection stmts = new CodeStmtCollection();

        {
            int initialLastEvent = -1;
            CodeMethodInvokeExpr calculate = new CodeMethodInvokeExpr(
                    CodeType.integer(), new CodeThisRefExpr(
                            this.getRuntimeType()), "calculatePairValue",
                            CodeLiteralExpr.integer(initialLastEvent),
                            CodeLiteralExpr.integer(newState));
            CodeFieldRefExpr fieldref = new CodeFieldRefExpr(
                    new CodeThisRefExpr(this.getRuntimeType()),
                    this.pairValueField);

            if (createNew) {
                CodeNewExpr construct = new CodeNewExpr(
                        CodeType.AtomicInteger(), calculate);
                CodeAssignStmt assign = new CodeAssignStmt(fieldref, construct);
                stmts.add(assign);
            } else {
                CodeMethodInvokeExpr set = new CodeMethodInvokeExpr(
                        CodeType.foid(), fieldref, "set", calculate);
                stmts.add(new CodeExprStmt(set));
            }
        }

        ICodeFormatter fmt = CodeFormatters.getDefault();
        stmts.getCode(fmt);
        return fmt.getCode();
    }

    private String getInternalEventHandlerCode() {
        CodeVarRefExpr eventid = new CodeVarRefExpr(new CodeVariable(
                CodeType.integer(), "eventId"));
        CodeVarRefExpr transtable = new CodeVarRefExpr(new CodeVariable(
                CodeType.array1(CodeType.integer()), "table"));

        CodeStmtCollection body = new CodeStmtCollection();
        {
            CodeThisRefExpr thisRef = new CodeThisRefExpr(this.getRuntimeType());
            CodeFieldRefExpr pairValueRef = new CodeFieldRefExpr(thisRef,
                    this.pairValueField);

            CodeVarRefExpr nextStateRef = new CodeVarRefExpr(new CodeVariable(
                    CodeType.integer(), "nextstate"));
            body.add(new CodeVarDeclStmt(nextStateRef.getVariable()));

            CodeStmtCollection loopbody = new CodeStmtCollection();
            {
                CodeVarRefExpr oldpairvalref = new CodeVarRefExpr(
                        new CodeVariable(CodeType.integer(), "oldpairvalue"));
                loopbody.add(new CodeVarDeclStmt(oldpairvalref.getVariable(),
                        new CodeMethodInvokeExpr(CodeType.integer(),
                                pairValueRef, "get")));

                CodeVarRefExpr oldstateref = new CodeVarRefExpr(
                        new CodeVariable(CodeType.integer(), "oldstate"));
                loopbody.add(new CodeVarDeclStmt(oldstateref.getVariable(),
                        new CodeMethodInvokeExpr(CodeType.integer(), thisRef,
                                "getState", oldpairvalref)));

                loopbody.add(new CodeAssignStmt(nextStateRef,
                        new CodeArrayLookup(CodeType.integer(), transtable,
                                oldstateref)));

                CodeVarRefExpr nextpairvalref = new CodeVarRefExpr(
                        new CodeVariable(CodeType.integer(), "nextpairvalue"));
                loopbody.add(new CodeVarDeclStmt(nextpairvalref.getVariable(),
                        new CodeMethodInvokeExpr(CodeType.integer(), thisRef,
                                "calculatePairValue", eventid, nextStateRef)));

                loopbody.add(new CodeConditionStmt(new CodeMethodInvokeExpr(
                        CodeType.bool(), pairValueRef, "compareAndSet",
                        oldpairvalref, nextpairvalref), new CodeBreakStmt()));
            }
            body.add(new CodeWhileStmt(CodeLiteralExpr.bool(true), loopbody));

            body.add(new CodeReturnStmt(nextStateRef));
        }

        CodeMemberMethod method = new CodeMemberMethod("handleEvent", false,
                false, true, CodeType.integer(), false, body,
                eventid.getVariable(), transtable.getVariable());

        ICodeFormatter fmt = CodeFormatters.getDefault();
        method.getCode(fmt);
        return fmt.getCode();
    }

    private String getInternalEventHandlerCallCode(int eventid,
            String tablevar, PropertyAndHandlers prop,
            HashMap<String, RVMJavaCode> categoryConditions) {
        CodeStmtCollection stmts = new CodeStmtCollection();

        CodeVarRefExpr nextStateRef;
        {
            CodeExpr tableRef = CodeExpr.fromLegacy(
                    CodeType.array1(CodeType.integer()), tablevar);
            CodeMethodInvokeExpr invoke = new CodeMethodInvokeExpr(
                    CodeType.integer(), new CodeThisRefExpr(
                            this.getRuntimeType()), "handleEvent",
                            CodeLiteralExpr.integer(eventid), tableRef);
            CodeVarDeclStmt decl = new CodeVarDeclStmt(new CodeVariable(
                    CodeType.integer(), "nextstate"), invoke);
            stmts.add(decl);

            nextStateRef = new CodeVarRefExpr(decl.getVariable());
        }

        for (Entry<String, RVMJavaCode> pair : categoryConditions.entrySet()) {
            CodeFieldRefExpr matchFieldRef;
            {
                RVMVariable matchvar = propMonitors.get(prop).categoryVars
                        .get(pair.getKey());
                CodeMemberField matchfield = new CodeMemberField(
                        matchvar.getVarName(), false, false, false,
                        CodeType.integer());
                matchFieldRef = new CodeFieldRefExpr(new CodeThisRefExpr(
                        this.getRuntimeType()), matchfield);
            }

            CodeExpr rhs;
            {
                String exprstr = pair.getValue().replaceStateVariable(
                        nextStateRef.getVariable().getName());
                rhs = CodeExpr.fromLegacy(CodeType.integer(), exprstr);
            }

            CodeAssignStmt assign = new CodeAssignStmt(matchFieldRef, rhs);
            stmts.add(assign);
        }

        ICodeFormatter fmt = CodeFormatters.getDefault();
        stmts.getCode(fmt);
        return fmt.getCode();
    }

    private String getStateVariable() {
        if (this.props.size() != 1)
            return null;

        PropertyAndHandlers prop = props.get(0);
        // It seems JavaMOP never parses the passed code; instead, it does
        // some string manipulation. As a result, we don't get the name of
        // the variable for holding the state. Here, I do some unreliable and
        // dirty trick, which is similar to existing string manipulation.
        String varname = this.propMonitors.get(prop).stateDeclaration
                .extractStateVariable();
        if (varname == null)
            return null;

        return varname;
    }

    private int getNumberOfStates() {
        if (this.props.size() != 1)
            return -1;

        PropertyAndHandlers prop = props.get(0);
        return this.propMonitors.get(prop).stateDeclaration.getNumberOfStates();
    }

    private int getNumberOfStateBits() {
        int num = this.getNumberOfStates();
        // [0 ~ num)
        int bits = 0;
        for (int i = num - 1; i > 0; i /= 2)
            ++bits;
        return bits;
    }

    /***
     *
     * Extra methods could be defined in subclasses.
     *
     * @return
     */
    public String printExtraDeclMethods() {
        return "";
    }

    static Map<RVMVariable, RVMVariable> niceVars = new HashMap<RVMVariable, RVMVariable>();

    public static RVMVariable getNiceVariable(RVMVariable var) {
        RVMVariable result = niceVars.get(var);
        if (result != null)
            return result;
        String v = var.getVarName();
        if (v.contains("_Category_")) {
            String[] parts = v.split("_Category_", 2);
            parts[1] = parts[1].replaceAll("_", "");
            parts[0] = parts[0].replaceAll("_", "");
            parts[0] = Character.toUpperCase(parts[0].charAt(0))
                    + parts[0].substring(1);
            v = parts[1] + parts[0];
        } else {
            String[] parts = v.split("_");
            v = parts[0];
            for (int i = 1; i < parts.length; i++) {
                v += Character.toUpperCase(parts[0].charAt(0))
                        + parts[0].substring(1);
            }
        }

        result = new RVMVariable(v);
        niceVars.put(var, result);
        return result;
    }

    @Override
    public CodeRVType.Monitor getRuntimeType() {
        CodeType type = new CodeType(this.getOutermostName().toString());
        return CodeRVType.forMonitor(type);
    }
}
