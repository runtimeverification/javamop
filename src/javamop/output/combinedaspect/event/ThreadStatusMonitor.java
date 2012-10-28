package javamop.output.combinedaspect.event;

import java.util.HashMap;
import java.util.List;

import javamop.output.MOPVariable;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.PropertyAndHandlers;
import javamop.parser.ast.stmt.BlockStmt;
import javamop.output.combinedaspect.CombinedAspect;

/**
 * 
 * This class is used to generate code to maintain a set of current active threads, similar to EndThread event.
 * 
 * */
public class ThreadStatusMonitor extends EndThread{
	
	private final static String eventName = "ThreadMonitor";
	private MOPVariable monitorName;
	
	private boolean hasDeadlockHandler = false;
	
	public ThreadStatusMonitor(JavaMOPSpec mopSpec, CombinedAspect combinedAspect) {
		this.monitorClass = combinedAspect.monitors.get(mopSpec);
		this.monitorName = monitorClass.getOutermostName();
		this.runnableMap = new MOPVariable(mopSpec.getName() + "_" + eventName + "_ThreadToRunnable");
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

		ret += "static HashMap<Thread, Runnable> " + runnableMap + " = new HashMap<Thread, Runnable>();\n";
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
				+ ", " + this.mainThread + ", " + this.globalLock.getName() + ", new " + this.monitorName + "." + this.monitorName + "DeadlockCallback()" +");\n";
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

		ret += "after (Thread " + threadVar + "): ( execution(void Thread+.run()) && target(" + threadVar + ") )";
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

		ret += "after (Runnable " + runnableVar + "): ( execution(void Runnable+.run()) && !execution(void Thread+.run()) && target(" + runnableVar + ") )";
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
	 * Print a helper method used to check whether a thread is contained in the threadSet.
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
	
	public String printAdviceForNewThread() {
		String ret = "";
		
		ret += "after (Thread t): ";
		ret += "(";
		ret += "call(void Thread+.start()) && target(t))";
		ret += " && " + commonPointcut + "() {\n";
		ret += "while (!" + globalLock.getName() + ".tryLock()) {\n";
		ret += "Thread.yield();\n";
		ret += "}\n";
		ret += threadSet + ".add(t);\n";
		ret += globalLock.getName() + "_cond.signalAll();\n";
		ret += globalLock.getName() + ".unlock();\n";
		ret += "}\n";
		
		return ret;
	}
	
	public String printAdvices() {
		String ret = "";
		ret += printDataStructures();
		ret += "\n";
		ret += printContainsBlockedThread();
		ret += "\n";
		ret += printContainsThread();
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
