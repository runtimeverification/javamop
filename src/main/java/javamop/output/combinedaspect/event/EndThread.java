// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.output.combinedaspect.event;

import javamop.JavaMOPMain;
import javamop.util.MOPException;
import javamop.output.MOPVariable;
import javamop.output.combinedaspect.CombinedAspect;
import javamop.output.combinedaspect.GlobalLock;
import javamop.output.combinedaspect.event.advice.AdviceBody;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;

/**
 * A hook to trigger endThread pointcuts in the MOP files.
 */
public class EndThread {
    JavaMOPSpec mopSpec;
    private EventDefinition event;
    GlobalLock globalLock;
    
    private AdviceBody eventBody;
    
    MOPVariable runnableMap;
    MOPVariable mainThread;
    private MOPVariable mainCounter;
    MOPVariable threadSet;
    
    MOPVariable commonPointcut = new MOPVariable("MOP_CommonPointCut");


    /**
     * Construct an EndThread hook.
     * @param mopSpec The specification that the endThread is in.
     * @param event The endThread event to respond to.
     * @param combinedAspect The aspect code being constructed.
     * @throws MOPException If {@code event} is not an endThread event.
     */
    public EndThread(JavaMOPSpec mopSpec, EventDefinition event, CombinedAspect combinedAspect) 
            throws MOPException {
        if (!event.isEndThread())
            throw new MOPException("EndThread should be defined only for an endThread pointcut.");
        
        this.mopSpec = mopSpec;
        this.event = event;
        this.globalLock = combinedAspect.lockManager.getLock();
        
        this.runnableMap = new MOPVariable(mopSpec.getName() + "_" + event.getId() + 
            "_ThreadToRunnable");
        this.mainThread = new MOPVariable(mopSpec.getName() + "_" + event.getId() + "_MainThread");
        this.mainCounter = new MOPVariable(mopSpec.getName() + "_" + event.getId() + 
            "_MainCounter");
        this.threadSet = new MOPVariable(mopSpec.getName() + "_" + event.getId() + "_ThreadSet");
        
        this.eventBody = new AdviceBody(mopSpec, event, combinedAspect);

    }
    
    /**
     * Construct an empty EndThread event.
     */
    protected EndThread() {
        
    }
    
    /**
     * Java source code for data structures used for the endThread hook.
     * @return Java source code describing the necessary declarations.
     */
    public String printDataStructures() {
        String ret = "";
        
        ret += "static HashMap<Thread, Runnable> " + runnableMap + 
            " = new HashMap<Thread, Runnable>();\n";
        ret += "static Thread " + mainThread + " = null;\n";
        ret += "static long " + mainCounter + " = 0;\n";
        ret += "static HashSet<Thread> " + threadSet + " = new HashSet<Thread>();\n";
        
        return ret;
    }
    
    /**
     * Code describing the hook for Threads initialized with Runnables.
     * @see Thread(Runnable)
     * @return AspectJ source code with the code hook.
     */
    public String printAdviceForThreadWithRunnable() {
        String ret = "";
        
        ret += "after (Runnable r) returning (Thread t): ";
        ret += "(";
        ret += "(call(Thread+.new(Runnable+,..)) && args(r,..))";
        ret += "|| (initialization(Thread+.new(ThreadGroup+, Runnable+,..)) " +
            "&& args(ThreadGroup, r,..)))";
        ret += " && " + commonPointcut + "() {\n";
        ret += "while (!" + globalLock.getName() + ".tryLock()) {\n";
        ret += "Thread.yield();\n";
        ret += "}\n";
        ret += runnableMap + ".put(t, r);\n";
        ret += globalLock.getName() + ".unlock();\n";
        ret += "}\n";
        
        return ret;
    }
    
    /**
     * Code describing the hook for Thread subclasses.
     * @return AspectJ source code with the code hook.
     */
    public String printAdviceForEndThread() {
        String ret = "";
        MOPVariable threadVar = new MOPVariable("t");
        
        ret += "after (Thread " + threadVar + "): ( execution(void Thread+.run()) && target(" 
            + threadVar + ") )";
        ret += " && " + commonPointcut + "() {\n";
        
        ret += "if(Thread.currentThread() == " + threadVar + ") {\n";
        if (event.getThreadVar() != null && event.getThreadVar().length() != 0) {
            ret += "Thread " + event.getThreadVar() + " = Thread.currentThread();\n";
        }
        
        ret += "while (!" + globalLock.getName() + ".tryLock()) {\n";
        ret += "Thread.yield();\n";
        ret += "}\n";
        
        ret += threadSet + ".remove(Thread.currentThread());\n";
        
        ret += globalLock.getName() + ".unlock();\n";
        
        ret += EventManager.EventMethodHelper.methodName(eventBody.getMOPSpec(), event,
            eventBody.fileName);
        ret += "(";
        if (event.getThreadVar() != null && event.getThreadVar().length() != 0) {
            ret += event.getThreadVar();
        }
        ret += ");\n";
        
        ret += "}\n";
        
        ret += "}\n";
        
        return ret;
    }
    
    /**
     * Code describing the hook for classes that implement the Runnable interface.
     * @return AspectJ source code with the code hook.
     */
    public String printAdviceForEndRunnable() {
        String ret = "";
        MOPVariable runnableVar = new MOPVariable("r");
        
        ret += "after (Runnable " + runnableVar + "): ( execution(void Runnable+.run()) "+
            "&& !execution(void Thread+.run()) && target(" + runnableVar + ") )";
        ret += " && " + commonPointcut + "() {\n";
        
        ret += "if(" + runnableMap + ".get(Thread.currentThread()) == " + runnableVar + ") {\n";
        if (event.getThreadVar() != null && event.getThreadVar().length() != 0) {
            ret += "Thread " + event.getThreadVar() + " = Thread.currentThread();\n";
        }
        ret += "while (!" + globalLock.getName() + ".tryLock()) {\n";
        ret += "Thread.yield();\n";
        ret += "}\n";
        ret += threadSet + ".remove(Thread.currentThread());\n";
        ret += globalLock.getName() + ".unlock();\n";
        
        ret += EventManager.EventMethodHelper.methodName(eventBody.getMOPSpec(), event,
            eventBody.fileName);
        ret += "(";
        if (event.getThreadVar() != null && event.getThreadVar().length() != 0) {
            ret += event.getThreadVar();
        }
        ret += ");\n";
        
        ret += "}\n";
        
        ret += "}\n";
        
        return ret;
    }
    
    /**
     * Code describing the hook for code to run before main ends.
     * @return AspectJ code with advice to run after main executes.
     */
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
        ret += "}\n";
        ret += "if(" + mainThread + " == Thread.currentThread()){\n";
        ret += mainCounter + "++;\n";
        ret += "}\n";
        ret += globalLock.getName() + ".unlock();\n";
        ret += "}\n";
        ret += "\n";
        
        ret += "after (): " + "(execution(void *.main(..)) )";
        ret += " && " + commonPointcut + "() {\n";
        ret += "while (!" + globalLock.getName() + ".tryLock()) {\n";
        ret += "Thread.yield();\n";
        ret += "}\n";
        ret += "if(" + mainThread + " == Thread.currentThread()){\n";
        ret += mainCounter + "--;\n";
        ret += "if(" + mainCounter + " <= 0){\n";
        if (event.getThreadVar() != null && event.getThreadVar().length() != 0) {
            ret += "Thread " + event.getThreadVar() + " = Thread.currentThread();\n";
        }
        ret += threadSet + ".remove(Thread.currentThread());\n";
        
        ret += EventManager.EventMethodHelper.methodName(eventBody.getMOPSpec(), event,
            eventBody.fileName);
        ret += "(";
        if (event.getThreadVar() != null && event.getThreadVar().length() != 0) {
            ret += event.getThreadVar();
        }
        ret += ");\n";
        
        ret += "}\n";
        ret += "}\n";
        ret += globalLock.getName() + ".unlock();\n";
        ret += "}\n";
        ret += "\n";
        
        return ret;
    }
    
    /**
     * Code that adds new created threads to the thread set.
     * @return AspectJ/Java code that adds new threads to the global thread set.
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
        ret += globalLock.getName() + ".unlock();\n";
        ret += "}\n";
        
        return ret;
    }
    
    /**
     * Code that triggers the endThread events on program termination.
     * @return Java code to trigger endThread events for all open threads.
     */
    public String printAdviceBodyAtEndProgram(){
        String ret = "";
        MOPVariable t = new MOPVariable("t");
        ret += "while (!" + globalLock.getName() + ".tryLock()) {\n";
        ret += "Thread.yield();\n";
        ret += "}\n";
        if (event.getThreadVar() != null && event.getThreadVar().length() != 0){
            ret += "for(Thread " + event.getThreadVar() + " : " + threadSet + ") {\n";
            ret += threadSet + ".remove(" + event.getThreadVar() + ");\n";
        } else {
            ret += "for(Thread " + t + " : " + threadSet + ") {\n";
            ret += threadSet + ".remove(" + t + ");\n";
        }
        
        ret += EventManager.EventMethodHelper.methodName(eventBody.getMOPSpec(), event,
            eventBody.fileName);
        ret += "(";
        if (event.getThreadVar() != null && event.getThreadVar().length() != 0) {
            ret += event.getThreadVar();
        }
        ret += ");\n";
        
        ret += "}\n";
        ret += globalLock.getName() + ".unlock();\n";
        
        return ret;
    }
    
    /**
     * Aggregate all the generated source advice together.
     * @return All the Java/AspectJ source generated by this class put together.
     */
    public String printAdvices() {
        String ret = "";
        
        ret += printDataStructures();
        ret += "\n";
        ret += printAdviceForThreadWithRunnable();
        ret += "\n";
        ret += printAdviceForEndThread();
        ret += "\n";
        ret += printAdviceForEndRunnable();
        ret += "\n";
        ret += printAdviceForMainEnd();
        ret += "\n";
        ret += printAdviceForNewThread();
        
        return ret;
    }
    
}
