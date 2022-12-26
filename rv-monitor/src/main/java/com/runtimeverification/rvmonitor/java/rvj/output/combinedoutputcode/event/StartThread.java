package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event;

import com.runtimeverification.rvmonitor.java.rvj.output.RVMVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.CombinedOutput;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.GlobalLock;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event.advice.AdviceBody;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.EventDefinition;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;
import com.runtimeverification.rvmonitor.util.RVMException;

public class StartThread {
    private final EventDefinition event;
    private final GlobalLock globalLock;

    private final AdviceBody eventBody;

    private final RVMVariable runnableMap;
    private final RVMVariable mainThread;

    private final RVMVariable commonPointcut = new RVMVariable(
            "RVM_CommonPointCut");

    public StartThread(RVMonitorSpec rvmSpec, EventDefinition event,
            CombinedOutput combinedOutput) throws RVMException {
        if (!event.isStartThread())
            throw new RVMException(
                    "StartThread should be defined only for an startThread pointcut.");

        this.event = event;
        this.globalLock = combinedOutput.lockManager.getLock();
        this.runnableMap = new RVMVariable(rvmSpec.getName() + "_"
                + event.getId() + "_ThreadToRunnable");
        this.mainThread = new RVMVariable(rvmSpec.getName() + "_"
                + event.getId() + "_MainThread");

        this.eventBody = AdviceBody.createAdviceBody(rvmSpec, event,
                combinedOutput);
    }

    public String printDataStructures() {
        String ret = "";

        ret += "static HashMap<Thread, Runnable> " + runnableMap
                + " = new HashMap<Thread, Runnable>();\n";
        ret += "static Thread " + mainThread + " = null;\n";
        return ret;
    }

    public String printAdviceForThreadWithRunnable() {
        String ret = "";

        ret += "after (Runnable r) returning (Thread t): ";
        ret += "(";
        ret += "(call(Thread+.new(Runnable+,..)) && args(r,..))";
        ret += "|| (initialization(Thread+.new(ThreadGroup+, Runnable+,..)) && args(ThreadGroup, r,..)))";
        ret += " && " + commonPointcut + "() {\n";
        ret += this.globalLock.getAcquireCode();
        ret += runnableMap + ".put(t, r);\n";
        ret += this.globalLock.getReleaseCode();
        ret += "}\n";

        return ret;
    }

    public String printAdviceForStartThread() {
        String ret = "";
        RVMVariable threadVar = new RVMVariable("t");

        ret += "before (Thread " + threadVar
                + "): ( execution(void Thread+.run()) && target(" + threadVar
                + ") )";
        ret += " && " + commonPointcut + "() {\n";

        ret += "if(Thread.currentThread() == " + threadVar + ") {\n";
        if (event.getThreadVar() != null && event.getThreadVar().length() != 0) {
            ret += "Thread " + event.getThreadVar()
                    + " = Thread.currentThread();\n";
        }

        ret += eventBody;
        ret += "}\n";

        ret += "}\n";

        return ret;
    }

    public String printAdviceForStartRunnable() {
        String ret = "";
        RVMVariable runnableVar = new RVMVariable("r");

        ret += "before (Runnable "
                + runnableVar
                + "): ( execution(void Runnable+.run()) && !execution(void Thread+.run()) && target("
                + runnableVar + ") )";
        ret += " && " + commonPointcut + "() {\n";
        ret += this.globalLock.getAcquireCode();
        ret += "if(" + runnableMap + ".get(Thread.currentThread()) == "
                + runnableVar + ") {\n";
        if (event.getThreadVar() != null && event.getThreadVar().length() != 0) {
            ret += "Thread " + event.getThreadVar()
                    + " = Thread.currentThread();\n";
        }
        ret += "}\n";
        ret += eventBody;
        ret += this.globalLock.getReleaseCode();

        ret += "}\n";

        return ret;
    }

    public String printAdviceForMainStart() {
        String ret = "";

        ret += "before (): " + "(execution(void *.main(..)) )";
        ret += " && " + commonPointcut + "() {\n";
        ret += "if(" + mainThread + " == null){\n";
        ret += mainThread + " = Thread.currentThread();\n";

        if (event.getThreadVar() != null && event.getThreadVar().length() != 0) {
            ret += "Thread " + event.getThreadVar()
                    + " = Thread.currentThread();\n";
        }
        ret += eventBody;
        ret += "}\n";
        ret += "}\n";
        ret += "\n";

        return ret;
    }

    public String printAdvices() {
        String ret = "";

        ret += printDataStructures();
        ret += "\n";
        ret += printAdviceForThreadWithRunnable();
        ret += "\n";
        ret += printAdviceForStartThread();
        ret += "\n";
        ret += printAdviceForStartRunnable();
        ret += "\n";
        ret += printAdviceForMainStart();

        return ret;
    }

}
