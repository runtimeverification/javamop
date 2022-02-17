package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event;

import java.util.List;

import com.runtimeverification.rvmonitor.java.rvj.output.RVMVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.CombinedOutput;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.PropertyAndHandlers;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;

/**
 *
 * This class is used to generate code to maintain a set of current active
 * threads, similar to EndThread event.
 *
 * */
public class ThreadStatusMonitor extends EndThread {

    private final static String eventName = "ThreadMonitor";
    private RVMVariable monitorName;

    private boolean hasDeadlockHandler = false;

    public ThreadStatusMonitor(RVMonitorSpec rvmSpec,
            CombinedOutput combinedOutput) {
        this.monitorClass = combinedOutput.monitors.get(rvmSpec);
        this.monitorName = monitorClass.getOutermostName();
        this.runnableMap = new RVMVariable(rvmSpec.getName() + "_" + eventName
                + "_ThreadToRunnable");
        this.mainThread = new RVMVariable(rvmSpec.getName() + "_" + eventName
                + "_MainThread");
        this.threadSet = new RVMVariable(rvmSpec.getName() + "_" + eventName
                + "_ThreadSet");
        this.globalLock = combinedOutput.lockManager.getLock();

        List<PropertyAndHandlers> props = rvmSpec.getPropertiesAndHandlers();
        for (PropertyAndHandlers p : props) {
            if (p.getHandlers().containsKey("deadlock"))
                this.hasDeadlockHandler = true;
        }
    }

    @Override
    public String printDataStructures() {
        String ret = "";

        // We don't need those data structures for deadlock detection.
        // ret += "static HashMap<Thread, Runnable> " + runnableMap +
        // " = new HashMap<Thread, Runnable>();\n";
        // ret += "static Thread " + mainThread + " = null;\n";
        ret += "static HashSet<Thread> " + threadSet
                + " = new HashSet<Thread>();\n";
        return ret;
    }

    @Override
    public String printAdviceBodyAtEndProgram() {
        String ret = "";
        return ret;
    }

    @Override
    public String printAdviceForMainEnd() {
        String ret = "";

        ret += "before (): " + "(execution(void *.main(..)) )";
        ret += " && " + commonPointcut + "() {\n";
        ret += this.globalLock.getAcquireCode();
        ret += "if(" + mainThread + " == null){\n";
        ret += mainThread + " = Thread.currentThread();\n";
        ret += threadSet + ".add(Thread.currentThread());\n";
        ret += globalLock.getName() + "_cond.signalAll();\n";
        ret += "}\n";

        // Start deadlock detection thread here
        if (this.hasDeadlockHandler) {
            ret += "com.runtimeverification.rvmonitor.java.rt.RVMDeadlockDetector.startDeadlockDetectionThread("
                    + this.threadSet
                    + ", "
                    + this.mainThread
                    + ", "
                    + this.globalLock.getName()
                    + ", new "
                    + this.monitorName
                    + "." + this.monitorName + "DeadlockCallback()" + ");\n";
        }

        ret += this.globalLock.getReleaseCode();
        ret += "}\n";
        ret += "\n";

        ret += "after (): " + "(execution(void *.main(..)) )";
        ret += " && " + commonPointcut + "() {\n";
        ret += this.globalLock.getAcquireCode();

        ret += threadSet + ".remove(Thread.currentThread());\n";

        ret += this.globalLock.getReleaseCode();
        ret += "}\n";

        ret += "\n";

        return ret;
    }

    @Override
    public String printAdviceForEndThread() {
        String ret = "";
        RVMVariable threadVar = new RVMVariable("t");

        ret += "after (Thread " + threadVar
                + "): ( execution(void Thread+.run()) && target(" + threadVar
                + ") )";
        ret += " && " + commonPointcut + "() {\n";

        ret += this.globalLock.getAcquireCode();
        ret += threadSet + ".remove(Thread.currentThread());\n";
        ret += this.globalLock.getReleaseCode();

        ret += "}\n";

        return ret;
    }

    @Override
    public String printAdviceForEndRunnable() {
        String ret = "";
        RVMVariable runnableVar = new RVMVariable("r");

        ret += "after (Runnable "
                + runnableVar
                + "): ( execution(void Runnable+.run()) && !execution(void Thread+.run()) && target("
                + runnableVar + ") )";
        ret += " && " + commonPointcut + "() {\n";

        ret += this.globalLock.getAcquireCode();
        ret += threadSet + ".remove(Thread.currentThread());\n";
        ret += this.globalLock.getReleaseCode();

        ret += "}\n";

        return ret;
    }

    /**
     *
     * Print a helper method used to check whether a thread is blocked or not.
     *
     * */
    public String printContainsBlockedThread() {
        String ret = "";

        ret += "static boolean containsBlockedThread(String name) {\n";
        ret += "for (Thread t : " + threadSet + ") {\n";
        ret += "if (t.getName().equals(name)) {\n";
        ret += "if (t.getState() == Thread.State.BLOCKED || t.getState() == Thread.State.WAITING) {\n";
        ret += "return true;\n";
        ret += "}\n";
        ret += "}\n";
        ret += "}\n";
        ret += "return false;\n";
        ret += "}\n";

        return ret;
    }

    /**
     *
     * Print a helper method used to check whether a thread is contained in the
     * threadSet.
     *
     * */
    public String printContainsThread() {
        String ret = "";

        ret += "static boolean containsThread(String name) {\n";
        ret += "for (Thread t : " + threadSet + ") {\n";
        ret += "if (t.getName().equals(name)) {\n";
        ret += "return true;\n";
        ret += "}\n";
        ret += "}\n";
        ret += "return false;\n";
        ret += "}\n";

        return ret;
    }

    @Override
    public String printAdviceForNewThread() {
        String ret = "";

        // ret += "after (Thread t): ";
        // ret += "(";
        // ret += "call(void Thread+.start()) && target(t))";
        // ret += " && " + commonPointcut + "() {\n";

        ret += "public static void startDeadlockDetection() { \n";
        ret += this.globalLock.getAcquireCode();
        ret += threadSet + ".add(Thread.currentThread());\n";
        // Start deadlock detection thread here
        if (this.hasDeadlockHandler) {
            ret += "if (!com.runtimeverification.rvmonitor.java.rt.RVMDeadlockDetector.startedDeadlockDetection) {\n";
            ret += "com.runtimeverification.rvmonitor.java.rt.RVMDeadlockDetector.startDeadlockDetectionThread("
                    + this.threadSet
                    + ", "
                    + this.globalLock.getName()
                    + ", new "
                    + this.monitorName
                    + "."
                    + this.monitorName
                    + "DeadlockCallback()" + ");\n";
            ret += "com.runtimeverification.rvmonitor.java.rt.RVMDeadlockDetector.startedDeadlockDetection = true;\n";
            ret += "}\n";
        }
        ret += globalLock.getName() + "_cond.signalAll();\n";
        ret += this.globalLock.getReleaseCode();
        ret += "}\n";

        return ret;
    }

    @Override
    public String printAdvices() {
        String ret = "";
        ret += printDataStructures();
        ret += "\n";

        // Do we need those helper methods?
        // ret += printContainsBlockedThread();
        // ret += "\n";
        // ret += printContainsThread();
        // ret += "\n";

        // ret += printAdviceForThreadWithRunnable();
        // ret += "\n";
        // ret += printAdviceForEndThread();
        // ret += "\n";
        // ret += printAdviceForEndRunnable();
        // ret += "\n";
        // ret += printAdviceForMainEnd();
        // ret += "\n";
        ret += printAdviceForNewThread();
        ret += "\n";
        return ret;
    }

}
