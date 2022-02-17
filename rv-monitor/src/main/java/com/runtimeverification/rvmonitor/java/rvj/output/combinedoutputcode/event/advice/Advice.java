package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event.advice;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMonitorStatistics;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.ActivatorManager;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.CombinedOutput;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.GlobalLock;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.InternalBehaviorObservableCodeGenerator;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.RVMonitorStatManager;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.EventDefinition;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;
import com.runtimeverification.rvmonitor.util.RVMException;

public class Advice {
    public final RVMonitorStatManager statManager;
    public final ActivatorManager activatorsManager;

    private final RVMVariable inlineFuncName;
    private final RVMParameters inlineParameters;

    private final RVMVariable pointcutName;
    private final RVMParameters parameters;

    public boolean beCounted = false;
    public RVMParameters threadVars = new RVMParameters();
    private final GlobalLock globalLock;
    private final boolean isSync;

    private final LinkedList<EventDefinition> events = new LinkedList<EventDefinition>();
    private final HashSet<RVMonitorSpec> specsForActivation = new HashSet<RVMonitorSpec>();
    private final HashSet<RVMonitorSpec> specsForChecking = new HashSet<RVMonitorSpec>();

    private final HashMap<EventDefinition, AdviceBody> advices = new HashMap<EventDefinition, AdviceBody>();

    private final InternalBehaviorObservableCodeGenerator internalBehaviorObservableGenerator;

    private boolean isCodeGenerated = false;

    public Advice(RVMonitorSpec rvmSpec, EventDefinition event,
            CombinedOutput combinedOutput) throws RVMException {

        String prefix = Main.merge ? rvmSpec.getName() + "_" : "";
        this.pointcutName = new RVMVariable(prefix + event.getId() + "Event");
        this.inlineFuncName = new RVMVariable("RVMInline" + rvmSpec.getName()
                + "_" + event.getUniqueId());
        this.parameters = event.getParametersWithoutThreadVar();
        this.inlineParameters = event.getRVMParametersWithoutThreadVar();

        if (event.getThreadVar() != null && event.getThreadVar().length() != 0) {
            if (event.getParameters().getParam(event.getThreadVar()) == null)
                throw new RVMException(
                        "thread variable is not included in the event definition.");

            this.threadVars.add(event.getParameters().getParam(
                    event.getThreadVar()));
        }

        this.statManager = combinedOutput.statManager;

        this.activatorsManager = combinedOutput.activatorsManager;

        this.globalLock = combinedOutput.lockManager.getLock();
        this.isSync = rvmSpec.isSync();

        this.advices.put(event,
                AdviceBody.createAdviceBody(rvmSpec, event, combinedOutput));

        this.events.add(event);
        if (event.getCountCond() != null && event.getCountCond().length() != 0) {
            this.beCounted = true;
        }

        if (event.isStartEvent())
            specsForActivation.add(rvmSpec);
        else
            specsForChecking.add(rvmSpec);

        this.internalBehaviorObservableGenerator = combinedOutput
                .getInternalBehaviorObservableGenerator();
    }

    public String getPointCutName() {
        return pointcutName.getVarName();
    }

    public boolean addEvent(RVMonitorSpec rvmSpec, EventDefinition event,
            CombinedOutput combinedOutput) throws RVMException {

        // Parameter Conflict Check
        for (RVMParameter param : event.getParametersWithoutThreadVar()) {
            RVMParameter param2 = parameters.getParam(param.getName());

            if (param2 == null)
                continue;

            if (!param.getType().equals(param2.getType())) {
                return false;
            }
        }

        parameters.addAll(event.getParametersWithoutThreadVar());

        if (event.getThreadVar() != null && event.getThreadVar().length() != 0) {
            if (event.getParameters().getParam(event.getThreadVar()) == null)
                throw new RVMException(
                        "thread variable is not included in the event definition.");

            this.threadVars.add(event.getParameters().getParam(
                    event.getThreadVar()));
        }

        // add an advice body.
        this.advices.put(event,
                AdviceBody.createAdviceBody(rvmSpec, event, combinedOutput));

        this.events.add(event);
        if (event.getCountCond() != null && event.getCountCond().length() != 0) {
            this.beCounted = true;
        }
        if (event.isStartEvent())
            specsForActivation.add(rvmSpec);
        else
            specsForChecking.add(rvmSpec);
        return true;
    }

    protected String adviceBody() {
        String ret = "";

        if (Main.empty_advicebody) {
            ret += "System.out.print(\"\");\n";

            Iterator<EventDefinition> iter;
            iter = this.events.iterator();

            if (this.beCounted) {
                ret += "++" + this.pointcutName + "_count;\n";
            }

            while (iter.hasNext()) {
                EventDefinition event = iter.next();

                AdviceBody advice = advices.get(event);

                if (advices.size() > 1) {
                    ret += "//" + advice.rvmSpec.getName() + "_"
                            + event.getUniqueId() + "\n";
                }
            }
        } else {
            for (RVMParameter threadVar : threadVars) {
                ret += "Thread " + threadVar.getName()
                        + " = Thread.currentThread();\n";
            }

            if (Main.useFineGrainedLock) {
                if (!Main.suppressActivator) {
                    for (RVMonitorSpec spec : specsForActivation) {
                        ret += this.activatorsManager.setValue(spec, true);
                        ret += ";\n";
                    }
                }
            } else {
                for (RVMonitorSpec spec : specsForActivation) {
                    ret += activatorsManager.setValue(spec, true);
                    ret += ";\n";
                }
                if (isSync)
                    ret += this.globalLock.getAcquireCode();
            }

            Iterator<EventDefinition> iter;
            iter = this.events.iterator();

            if (this.beCounted) {
                ret += "++" + this.pointcutName + "_count;\n";
            }

            while (iter.hasNext()) {
                EventDefinition event = iter.next();

                AdviceBody advice = advices.get(event);

                ret += this.internalBehaviorObservableGenerator
                        .generateEventMethodEnterCode(event);
                ret += this.statManager.incEvent(advice.rvmSpec, event);

                if (specsForChecking.contains(advice.rvmSpec)) {
                    if (advices.size() > 1) {
                        ret += "//" + advice.rvmSpec.getName() + "_"
                                + event.getUniqueId() + "\n";
                    }

                    if (Main.suppressActivator)
                        ret += "{\n";
                    else
                        ret += "if ("
                                + activatorsManager.getValue(advice.rvmSpec)
                                + ") {\n";
                } else {
                    if (advices.size() > 1) {
                        ret += "//" + advice.rvmSpec.getName() + "_"
                                + event.getUniqueId() + "\n";
                        ret += "{\n";
                    }
                }

                if (Main.statistics) {
                    RVMonitorStatistics stat = this.statManager
                            .getStat(advice.rvmSpec);

                    ret += stat.eventInc(event.getId());

                    for (RVMParameter param : event.getRVMParametersOnSpec()) {
                        ret += stat.paramInc(param);
                    }

                    ret += "\n";
                }

                // add check count condition here
                String countCond = event.getCountCond();

                if (countCond != null && countCond.length() != 0) {
                    countCond = countCond.replaceAll("count", this.pointcutName
                            + "_count");
                    ret += "if (" + countCond + ") {\n";
                }
                ret += advice;

                if (countCond != null && countCond.length() != 0) {
                    ret += "}\n";
                }

                if (specsForChecking.contains(advice.rvmSpec)) {
                    ret += "}\n";
                } else {
                    if (advices.size() > 1) {
                        ret += "}\n";
                    }
                }
                ret += this.internalBehaviorObservableGenerator
                        .generateEventMethodLeaveCode(event);
            }

            if (!Main.useFineGrainedLock) {
                if (isSync)
                    ret += this.globalLock.getReleaseCode();
            }
        }

        return ret;
    }

    @Override
    public String toString() {
        String ret = "";

        if (Main.inline) {
            ret += "void " + inlineFuncName + "("
                    + inlineParameters.parameterDeclString();
            ret += ") {\n";

            ret += adviceBody();

            ret += "}\n";
        }

        ret += "public static final void " + pointcutName;
        ret += "(";
        ret += parameters.parameterDeclString();
        ret += ")";
        ret += " {\n";

        if (Main.inline) {
            ret += inlineFuncName + "(" + inlineParameters.parameterString();
            ret += ");\n";
        } else {
            ret += adviceBody();
        }

        ret += "}\n";

        return ret;
    }

    public void generateCode() {
        if (!this.isCodeGenerated) {
            // this.eventManager.generateCode();
            Iterator<EventDefinition> iter;
            iter = this.events.iterator();

            while (iter.hasNext()) {
                EventDefinition event = iter.next();
                AdviceBody advice = advices.get(event);
                advice.generateCode();
            }
        }

        this.isCodeGenerated = true;
    }
}
