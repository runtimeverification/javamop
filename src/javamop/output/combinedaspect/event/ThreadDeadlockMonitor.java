package javamop.output.combinedaspect.event;

import javamop.output.MOPVariable;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.output.combinedaspect.CombinedAspect;

/**
 * 
 * This class is used to generate code to maintain a set of current active threads, similar to EndThread event.
 * 
 * */
public class ThreadDeadlockMonitor extends EndThread{
	
	private final static String eventName = "ThreadMonitor";
	private MOPVariable monitorName;
	
	public ThreadDeadlockMonitor(JavaMOPSpec mopSpec, CombinedAspect combinedAspect) {
		this.monitorClass = combinedAspect.monitors.get(mopSpec);
		this.monitorName = monitorClass.getOutermostName();
		this.runnableMap = new MOPVariable(mopSpec.getName() + "_" + eventName + "_ThreadToRunnable");
		this.mainThread = new MOPVariable(mopSpec.getName() + "_" + eventName + "_MainThread");
		this.threadSet = new MOPVariable(mopSpec.getName() + "_" + eventName + "_ThreadSet");
		this.globalLock = combinedAspect.lockManager.getLock();
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
		ret += "synchronized (" + globalLock.getName() + ") {\n";
		ret += "if(" + mainThread + " == null){\n";
		ret += mainThread + " = Thread.currentThread();\n";
		ret += threadSet + ".add(Thread.currentThread());\n";
		ret += "}\n";
		
		ret += "javamoprt.MOPDeadlockDetector.startDeadlockDetectionThread(" + this.threadSet 
				+ ", " + this.mainThread + ", " + this.globalLock.getName() + ", new " + this.monitorName + "." + this.monitorName + "DeadlockCallback()" +");\n";
		//Start deadlock detection thread here
		
		ret += "}\n";
		ret += "}\n";
		ret += "\n";

		ret += "after (): " + "(execution(void *.main(..)) )";
		ret += " && " + commonPointcut + "() {\n";
		ret += "synchronized (" + globalLock.getName() + ") {\n";
				
		ret += threadSet + ".remove(Thread.currentThread());\n";
		
		// Stop deadlock detection thread here
		ret += "}\n";		
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

		ret += "synchronized (" + globalLock.getName() + ") {\n";
		ret += threadSet + ".remove(Thread.currentThread());\n";
		ret += "}\n";


		ret += "}\n";

		return ret;
	}
	
	@Override
	public String printAdviceForEndRunnable() {
		String ret = "";
		MOPVariable runnableVar = new MOPVariable("r");

		ret += "after (Runnable " + runnableVar + "): ( execution(void Runnable+.run()) && !execution(void Thread+.run()) && target(" + runnableVar + ") )";
		ret += " && " + commonPointcut + "() {\n";

		ret += "synchronized (" + globalLock.getName() + ") {\n";
		ret += threadSet + ".remove(Thread.currentThread());\n";
		ret += "}\n";

		ret += "}\n";

		return ret;
	}
	
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
