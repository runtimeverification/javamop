package com.runtimeverification.rvmonitor.java.rvj.output.monitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.java.rvj.output.OptimizedCoenableSet;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodePhantomStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmtCollection;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeVarRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeRVType;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.GlobalLock;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.reftree.RefTree;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.EventDefinition;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.PropertyAndHandlers;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;
import com.runtimeverification.rvmonitor.util.RVMException;

public class SuffixMonitor extends Monitor {
    private final RVMVariable activity = new RVMVariable("RVM_activity");
    private final RVMVariable loc = new RVMVariable("RVM_loc");
    private final RVMVariable lastevent = new RVMVariable("RVM_lastevent");

    private List<EventDefinition> events;

    Monitor innerMonitor = null;

    private ArrayList<String> categories;
    private final RVMVariable monitorList = new RVMVariable("monitorList");
    private boolean existSkip = false;

    @Override
    public MonitorFeatures getFeatures() {
        // The inner monitor thing looks quirky to me, but, to deal with the
        // quirkiness,
        // it forwards the request.
        return this.innerMonitor.getFeatures();
    }

    public SuffixMonitor(String outputName, RVMonitorSpec rvmSpec,
            OptimizedCoenableSet coenableSet, boolean isOutermost)
                    throws RVMException {
        super(outputName, rvmSpec, coenableSet, isOutermost);

        this.isDefined = rvmSpec.isSuffixMatching();

        if (this.isDefined) {
            monitorName = new RVMVariable(rvmSpec.getName() + "SuffixMonitor");

            if (isOutermost) {
                varInOutermostMonitor = new VarInOutermostMonitor(outputName,
                        rvmSpec, rvmSpec.getEvents());
                monitorTermination = new MonitorTermination(outputName,
                        rvmSpec, rvmSpec.getEvents(), coenableSet);
            }

            if (rvmSpec.isEnforce()) {
                // TODO Do we need raw monitor for enforcing properties?
                innerMonitor = new EnforceMonitor(outputName, rvmSpec,
                        coenableSet, false);
                for (PropertyAndHandlers p : rvmSpec.getPropertiesAndHandlers()) {
                    int totalHandlers = p.getHandlers().size();
                    if (p.getHandlers().containsKey("deadlock"))
                        totalHandlers--;
                    // We only allow one handler (except deadlock handler) when
                    // enforcing a property
                    if (totalHandlers > 1)
                        throw new RVMException(
                                "Only one handler (except deadlock handler) is allowed when enforcing a property");
                }

            } else {
                if (rvmSpec.getPropertiesAndHandlers().size() == 0)
                    innerMonitor = new RawMonitor(outputName, rvmSpec,
                            coenableSet, false);
                else
                    innerMonitor = new BaseMonitor(outputName, rvmSpec,
                            coenableSet, false);
            }
            events = rvmSpec.getEvents();

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
                if (event.has__SKIP()) {
                    existSkip = true;
                    break;
                }
            }
        } else {
            if (rvmSpec.isEnforce()) {
                // TODO Do we need raw monitor for enforcing properties?
                innerMonitor = new EnforceMonitor(outputName, rvmSpec,
                        coenableSet, isOutermost);
                for (PropertyAndHandlers p : rvmSpec.getPropertiesAndHandlers()) {
                    int totalHandlers = p.getHandlers().size();
                    if (p.getHandlers().containsKey("deadlock"))
                        totalHandlers--;
                    // We only allow one handler (except deadlock handler) when
                    // enforcing a property
                    if (totalHandlers > 1)
                        throw new RVMException(
                                "Only one handler (except deadlock handler) is allowed when enforcing a property");
                }
            } else {
                if (rvmSpec.getPropertiesAndHandlers().size() == 0)
                    innerMonitor = new RawMonitor(outputName, rvmSpec,
                            coenableSet, isOutermost);
                else
                    innerMonitor = new BaseMonitor(outputName, rvmSpec,
                            coenableSet, isOutermost);
            }
        }

        if (this.isDefined && rvmSpec.isGeneral()) {
            if (rvmSpec.isFullBinding() || rvmSpec.isConnected())
                monitorInfo = new MonitorInfo(rvmSpec);
        }
    }

    @Override
    public void setRefTrees(TreeMap<String, RefTree> refTrees) {
        this.refTrees = refTrees;
        innerMonitor.setRefTrees(refTrees);

        if (monitorTermination != null)
            monitorTermination.setRefTrees(refTrees);
    }

    @Override
    public RVMVariable getOutermostName() {
        if (isDefined)
            return monitorName;
        else
            return innerMonitor.getOutermostName();
    }

    @Override
    public Set<String> getNames() {
        Set<String> ret = innerMonitor.getNames();
        if (isDefined)
            ret.add(monitorName.toString());
        return ret;
    }

    @Override
    public Set<RVMVariable> getCategoryVars() {
        return innerMonitor.getCategoryVars();
    }

    public String doEvent(EventDefinition event) {
        String synch = Main.useFineGrainedLock ? " synchronized " : " ";
        String ret = "";

        int idnum = event.getIdNum();

        RVMVariable monitor = new RVMVariable("monitor");
        RVMVariable monitorSet = new RVMVariable("monitorSet");
        RVMVariable newMonitor = new RVMVariable("newMonitor");
        RVMVariable it = new RVMVariable("it");
        HashSet<RVMVariable> categoryVars = new HashSet<RVMVariable>();

        categoryVars.addAll(innerMonitor.getCategoryVars());

        ret += "final" + synch + "void event_" + event.getId() + "(";
        {
            RVMParameters params;
            if (Main.stripUnusedParameterInMonitor)
                params = event.getReferredParameters(event.getRVMParameters());
            else
                params = event.getRVMParameters();
            ret += params.parameterDeclString();
        }
        ret += ") {\n";

        for (RVMVariable var : getCategoryVars()) {
            ret += BaseMonitor.getNiceVariable(var) + " = false;\n";
        }

        if (isOutermost) {
            ret += lastevent + " = " + idnum + ";\n";
        }

        ret += "HashSet " + monitorSet + " = new HashSet();\n";

        if (event.isStartEvent()) {
            ret += innerMonitor.getOutermostName() + " " + newMonitor
                    + " = new " + innerMonitor.getOutermostName() + "();\n";
            if (monitorInfo != null) {
                ret += monitorInfo.copy(newMonitor);
            }
            ret += monitorList + ".add(" + newMonitor + ");\n";
        }

        ret += "Iterator " + it + " = " + monitorList + ".iterator();\n";
        ret += "while (" + it + ".hasNext()){\n";
        ret += innerMonitor.getOutermostName() + " " + monitor + " = ("
                + innerMonitor.getOutermostName() + ")" + it + ".next();\n";

        ret += innerMonitor.Monitoring(monitor, event, loc, null,
                this.getOutputName(), false);

        ret += "if(" + monitorSet + ".contains(" + monitor + ")";
        for (RVMVariable categoryVar : categoryVars) {
            ret += " || " + monitor + "." + categoryVar;
        }
        ret += " ) {\n";
        ret += it + ".remove();\n";
        ret += "} else {\n";
        ret += monitorSet + ".add(" + monitor + ");\n";
        ret += "}\n";

        ret += "}\n";

        ret += "}\n";

        return ret;
    }

    @Override
    public String Monitoring(RVMVariable monitorVar, EventDefinition event,
            RVMVariable loc, GlobalLock l, String outputName,
            boolean inMonitorSet) {
        String ret = "";

        if (!isDefined)
            return innerMonitor.Monitoring(monitorVar, event, loc, l,
                    outputName, inMonitorSet);

        // if (has__LOC) {
        // if(loc != null)
        // ret += monitorVar + "." + this.loc + " = " + loc + ";\n";
        // else
        // ret += monitorVar + "." + this.loc + " = " +
        // "Thread.currentThread().getStackTrace()[2].toString()"
        // + ";\n";
        // }

        ret += monitorVar + ".event_" + event.getId() + "(";
        {
            RVMParameters passing;
            if (Main.stripUnusedParameterInMonitor)
                passing = event.getReferredParameters(event.getRVMParameters());
            else
                passing = event.getRVMParameters();
            ret += passing.parameterString();
        }
        ret += ");\n";

        if (!Main.eliminatePresumablyRemnantCode) {
            for (RVMVariable var : getCategoryVars()) {
                ret += BaseMonitor.getNiceVariable(var) + " |= " + monitorVar
                        + "." + BaseMonitor.getNiceVariable(var) + ";" + "\n";
            }
        }

        if (existSkip) {
            ret += BaseMonitor.skipEvent + " |= " + monitorVar + "."
                    + BaseMonitor.skipEvent + ";\n";
            ret += monitorVar + "." + BaseMonitor.skipEvent + " = false;\n";
        }

        return ret;
    }

    @Override
    public MonitorInfo getMonitorInfo() {
        if (isDefined)
            return monitorInfo;
        else
            return innerMonitor.getMonitorInfo();

    }

    @Override
    public String toString() {
        String ret = "";

        RVMVariable monitor = new RVMVariable("monitor");
        RVMVariable newMonitor = new RVMVariable("newMonitor");

        if (isDefined) {
            ret += "class " + monitorName;
            if (isOutermost)
                ret += " extends com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractSynchronizedMonitor";
            ret += " implements Cloneable, com.runtimeverification.rvmonitor.java.rt.RVMObject {\n";

            for (RVMVariable var : getCategoryVars()) {
                ret += "boolean " + BaseMonitor.getNiceVariable(var) + ";\n";
            }

            if (varInOutermostMonitor != null)
                ret += varInOutermostMonitor;

            ret += "Vector<" + innerMonitor.getOutermostName() + "> "
                    + monitorList + " = new Vector<"
                    + innerMonitor.getOutermostName() + ">();\n";

            if (this.has__ACTIVITY)
                ret += activityCode();
            if (existSkip)
                ret += "boolean " + BaseMonitor.skipEvent + " = false;\n";

            // clone()
            ret += "protected Object clone() {\n";
            ret += "try {\n";
            ret += monitorName + " ret = (" + monitorName
                    + ") super.clone();\n";
            if (monitorInfo != null)
                ret += monitorInfo.copy("ret", "this");
            ret += "ret." + monitorList + " = new Vector<"
                    + innerMonitor.getOutermostName() + ">();\n";
            ret += "for(" + innerMonitor.getOutermostName() + " " + monitor
                    + " : this." + monitorList + "){\n";
            ret += innerMonitor.getOutermostName() + " " + newMonitor + " = ";
            ret += "(" + innerMonitor.getOutermostName() + ")" + monitor
                    + ".clone()" + ";\n";
            if (monitorInfo != null)
                ret += monitorInfo.copy(newMonitor, monitor);
            ret += "ret." + monitorList + ".add(" + newMonitor + ");\n";
            ret += "}\n";
            ret += "return ret;\n";
            ret += "}\n";
            ret += "catch (CloneNotSupportedException e) {\n";
            ret += "throw new InternalError(e.toString());\n";
            ret += "}\n";
            ret += "}\n";
            ret += "\n";

            // implements getState()
            {
                ret += "@Override\n";
                ret += "public final int getState() {\n";
                ret += "return -1;\n";
                ret += "}\n\n";
            }

            // events
            for (EventDefinition event : this.events) {
                ret += this.doEvent(event) + "\n";
            }

            // endObject and some declarations
            if (isOutermost && monitorTermination != null) {
                ret += monitorTermination.getCode(this.getFeatures(), null,
                        null);
            }

            if (monitorInfo != null) {
                ret += monitorInfo.monitorDecl();
            }

            ret += "}\n";
            ret += "\n";
        }

        ret += this.innerMonitor;

        return ret;
    }

    public CodeStmtCollection generateMonitorTransitionedCode(
            CodeVarRefExpr affectedref, EventDefinition event,
            GlobalLock enforcelock) {
        CodeStmtCollection stmts = new CodeStmtCollection();

        // Modernizing the monitoring code takes too much effort. Maybe later.
        RVMVariable monitorvar = affectedref.getVariable().toLegacy();
        String mntcode = this.Monitoring(monitorvar, event, null, enforcelock,
                this.getOutputName(), false);
        stmts.add(CodeStmtCollection.fromLegacy(mntcode));

        // The referred variable is marked so that the dead-code elimination
        // step
        // won't remove the definition of the variable.
        stmts.add(new CodePhantomStmt(affectedref.getVariable()));
        return stmts;
    }

    @Override
    public CodeRVType.Monitor getRuntimeType() {
        CodeType type = new CodeType(this.getOutermostName().toString());
        return CodeRVType.forMonitor(type);
    }
}
