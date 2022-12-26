// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.output.combinedaspect.event;

import java.util.List;

import javamop.JavaMOPMain;
import javamop.output.MOPVariable;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.PropertyAndHandlers;
import javamop.output.combinedaspect.CombinedAspect;

/// TODO: disentangle this from EndThread.

/**
 * Generates code to maintain a set of current active threads, similar to the generated code for
 * the EndThread event.
 */
public class ThreadStatusMonitor extends EndThread{
    
    private final static String eventName = "ThreadMonitor";
    private final MOPVariable monitorName = null;
    
    private boolean hasDeadlockHandler = false;


    /**
     * Construct a thread status monitor.
     * @param mopSpec The specification this is running for.
     * @param combinedAspect The generated aspect that this is a part of.
     */
    public ThreadStatusMonitor(JavaMOPSpec mopSpec, CombinedAspect combinedAspect) {
        this.mopSpec = mopSpec;
        this.runnableMap = new MOPVariable(mopSpec.getName() + "_" + 
            eventName + "_ThreadToRunnable");
        this.mainThread = new MOPVariable(mopSpec.getName() + "_" + eventName + "_MainThread");
        this.threadSet = new MOPVariable(mopSpec.getName() + "_" + eventName + "_ThreadSet");
        this.globalLock = combinedAspect.lockManager.getLock();

        List<PropertyAndHandlers> props = mopSpec.getPropertiesAndHandlers();
        for (PropertyAndHandlers p : props) {
            if (p.getHandlers().containsKey("deadlock"))
                this.hasDeadlockHandler = true;
        }
    }
    
    @Override
    public String printDataStructures() {
        String ret = "";
        
        ret += "static HashMap<Thread, Runnable> " + runnableMap + 
            " = new HashMap<Thread, Runnable>();\n";
        ret += "static Thread " + mainThread + " = null;\n";
        ret += "static HashSet<Thread> " + threadSet + " = new HashSet<Thread>();\n";
        return ret;
    }
    
    @Override
    public String printAdviceBodyAtEndProgram(){
        String ret = "";
        return ret;
    }
    
    @Override
    public String printAdviceForMainEnd() {
        String ret = "";
        
        ret += "before (): " + "(execution(void *.main(..)) )";
        ret += " && " + commonPointcut + "() {\n";
        ret += "while (!" + globalLock.getName() + ".tryLock()) {\n";
        ret += "Thread.yield();\n";
        ret += "}\n";
        ret += "if(" + mainThread + " == null){\n";
        ret += mainThread + " = Thread.currentThread();\n";
        ret += threadSet + ".add(Thread.currentThread());\n";
        ret += globalLock.getName() + "_cond.signalAll();\n";
        ret += "}\n";
        
        //Start deadlock detection thread here
        if (this.hasDeadlockHandler) {
            ret += "javamoprt.MOPDeadlockDetector.startDeadlockDetectionThread(" + this.threadSet 
                + ", " + this.mainThread + ", " + this.globalLock.getName() + ", new "
                + this.monitorName + "." + this.monitorName + "DeadlockCallback()" +");\n";
        }
        
        ret += globalLock.getName() + ".unlock();\n";
        ret += "}\n";
        ret += "\n";
        
        ret += "after (): " + "(execution(void *.main(..)) )";
        ret += " && " + commonPointcut + "() {\n";
        ret += "while (!" + globalLock.getName() + ".tryLock()) {\n";
        ret += "Thread.yield();\n";
        ret += "}\n";
        
        ret += threadSet + ".remove(Thread.currentThread());\n";
        
        ret += globalLock.getName() + ".unlock();\n";       
        ret += "}\n";
        
        ret += "\n";
        
        return ret;
    }
    
    @Override
    public String printAdviceForEndThread() {
        String ret = "";
        MOPVariable threadVar = new MOPVariable("t");
        
        ret += "after (Thread " + threadVar + "): ( execution(void Thread+.run()) && target(" + 
            threadVar + ") )";
        ret += " && " + commonPointcut + "() {\n";
        
        ret += "while (!" + globalLock.getName() + ".tryLock()) {\n";
        ret += "Thread.yield();\n";
        ret += "}\n";
        ret += threadSet + ".remove(Thread.currentThread());\n";
        ret += globalLock.getName() + ".unlock();\n";
        
        
        ret += "}\n";
        
        return ret;
    }
    
    @Override
    public String printAdviceForEndRunnable() {
        String ret = "";
        MOPVariable runnableVar = new MOPVariable("r");
        
        ret += "after (Runnable " + runnableVar + "): ( execution(void Runnable+.run()) " +
            "&& !execution(void Thread+.run()) && target(" + runnableVar + ") )";
        ret += " && " + commonPointcut + "() {\n";
        
        ret += "while (!" + globalLock.getName() + ".tryLock()) {\n";
        ret += "Thread.yield();\n";
        ret += "}\n";
        ret += threadSet + ".remove(Thread.currentThread());\n";
        ret += globalLock.getName() + ".unlock();\n";
        
        ret += "}\n";
        
        return ret;
    }
    
    /**
     * Generate a helper method used to check whether a thread is blocked or not.
     * @return Java source code checking if a thread is blocked.
     */
    public String printContainsBlockedThread() {
        String ret = "";
        
        ret += "static boolean containsBlockedThread(String name) {\n";
        
        ret += "while (!" + globalLock.getName() + ".tryLock()) {\n";
        ret += "Thread.yield();\n";
        ret += "}\n";
        
        ret += "for (Thread t : " + threadSet + ") {\n";
        ret += "if (t.getName().equals(name)) {\n";
        ret += "if (t.getState() == Thread.State.BLOCKED || t.getState() == " +
            "Thread.State.WAITING) {\n";
        ret += globalLock.getName() + ".unlock();\n";
        ret += "return true;\n";
        ret += "}\n";
        ret += "}\n";
        ret += "}\n";
        
        ret += globalLock.getName() + ".unlock();\n";
        ret += "return false;\n";
        ret += "}\n";
        
        return ret;
    }
    
    /**
     * Generate a helper method used to check whether a thread is contained in the threadSet.
     * @return Java source code checking if a thread is registered.
     */
    public String printContainsThread() {
        String ret = "";
        
        ret += "static boolean containsThread(String name) {\n";
        
        ret += "while (!" + globalLock.getName() + ".tryLock()) {\n";
        ret += "Thread.yield();\n";
        ret += "}\n";
        
        ret += "for (Thread t : " + threadSet + ") {\n";
        ret += "if (t.getName().equals(name)) {\n";
        ret += globalLock.getName() + ".unlock();\n";
        ret += "return true;\n";
        ret += "}\n";
        ret += "}\n";
        ret += globalLock.getName() + ".unlock();\n";
        ret += "return false;\n";
        ret += "}\n";
        
        return ret;
    }
    
    /**
     * Generate AspectJ code to register new threads in the global set.
     * @return AspectJ/Java source code registering new threads.
     */
    public String printAdviceForNewThread() {
        String ret = "";
        
        ret += "after (Thread t): ";
        ret += "(";
        ret += "call(void Thread+.start()) && target(t))";
        ret += " && " + commonPointcut + "() {\n";
        ret += "while (!" + globalLock.getName() + ".tryLock()) {\n";
        ret += "Thread.yield();\n";
        ret += "}\n";
        ret += JavaMOPMain.options.aspectname + "RuntimeMonitor." + threadSet + ".add(t);\n";
        ret += globalLock.getName() + "_cond.signalAll();\n";
        ret += globalLock.getName() + ".unlock();\n";
        ret += "}\n";
        
        return ret;
    }
    
    /**
     * Generate AspectJ to trigger deadlock detection on starting new threads.
     * @return AspectJ/Java code that starts deadlock detection.
     */
    public String printMethodCallForNewThread() {
        String ret = "";
        
        ret += "after (Thread t): ";
        ret += "(";
        ret += "call(void Thread+.start()) && target(t))";
        ret += " && " + commonPointcut + "() {\n";
        
        if (JavaMOPMain.options.merge && JavaMOPMain.options.aspectname != null
                && JavaMOPMain.options.aspectname.length() > 0) {
            ret += JavaMOPMain.options.aspectname + "RuntimeMonitor.startDeadlockDetection();\n";
        }
        else {
            ret += this.mopSpec.getName() + "RuntimeMonitor.startDeadlockDetection();\n";
        }
        ret += "}\n";
        return ret;
    }
    
    /**
     * Generate AspectJ to trigger deadlock detection on starting the main thread.
     * @return AspectJ/Java code that starts deadlock detection.
     */
    public String printMethodCallForMainStart() {
        String ret = "";
        ret += "before (): " + "(execution(void *.main(..)) )";
        ret += " && " + commonPointcut + "() {\n";
        if (JavaMOPMain.options.merge && JavaMOPMain.options.aspectname != null
                && JavaMOPMain.options.aspectname.length() > 0) {
            ret += JavaMOPMain.options.aspectname + "RuntimeMonitor.startDeadlockDetection();\n";
        }
        else {
            ret += this.mopSpec.getName() + "RuntimeMonitor.startDeadlockDetection();\n";
        }
        ret += "}\n";
        return ret;
    }
    
    @Override
    public String printAdvices() {
        String ret = "";
        ret += printMethodCallForMainStart();
        ret += printAdviceForNewThread();
        ret += "\n";
        return ret;
    }
    
}
