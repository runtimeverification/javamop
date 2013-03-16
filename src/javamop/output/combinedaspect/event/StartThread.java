package javamop.output.combinedaspect.event;

import java.util.HashMap;

import javamop.MOPException;
import javamop.Main;
import javamop.output.MOPVariable;
import javamop.output.combinedaspect.CombinedAspect;
import javamop.output.combinedaspect.GlobalLock;
import javamop.output.combinedaspect.event.advice.AdviceBody;
import javamop.output.combinedaspect.event.advice.GeneralAdviceBody;
import javamop.output.combinedaspect.indexingtree.IndexingDecl;
import javamop.output.combinedaspect.indexingtree.IndexingTree;
import javamop.output.monitor.SuffixMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameters;

public class StartThread {
	JavaMOPSpec mopSpec;
	EventDefinition event;
	MonitorSet monitorSet;
	SuffixMonitor monitorClass;
	IndexingDecl indexingDecl;
	HashMap<MOPParameters, IndexingTree> indexingTrees;
	GlobalLock globalLock;

	AdviceBody eventBody;

	MOPVariable runnableMap;
	MOPVariable mainThread;

	MOPVariable commonPointcut = new MOPVariable("MOP_CommonPointCut");

	public StartThread(JavaMOPSpec mopSpec, EventDefinition event, CombinedAspect combinedAspect) throws MOPException {
		if (!event.isStartThread())
			throw new MOPException("StartThread should be defined only for an startThread pointcut.");

		this.mopSpec = mopSpec;
		this.event = event;
		this.monitorSet = combinedAspect.monitorSets.get(mopSpec);
		this.monitorClass = combinedAspect.monitors.get(mopSpec);
		this.indexingDecl = combinedAspect.indexingTreeManager.getIndexingDecl(mopSpec);
		this.indexingTrees = indexingDecl.getIndexingTrees();
		this.globalLock = combinedAspect.lockManager.getLock();
		this.runnableMap = new MOPVariable(mopSpec.getName() + "_" + event.getId() + "_ThreadToRunnable");
		this.mainThread = new MOPVariable(mopSpec.getName() + "_" + event.getId() + "_MainThread");

		this.eventBody = new GeneralAdviceBody(mopSpec, event, combinedAspect);
	}

	public String printDataStructures() {
		String ret = "";

		ret += "static HashMap<Thread, Runnable> " + runnableMap + " = new HashMap<Thread, Runnable>();\n";
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
		ret += "while (!" + globalLock.getName() + ".tryLock()) {\n";
		ret += "Thread.yield();\n";
		ret += "}\n";
		ret += runnableMap + ".put(t, r);\n";
		ret += globalLock.getName() + ".unlock();\n";
		ret += "}\n";

		return ret;
	}

	public String printAdviceForStartThread() {
		String ret = "";
		MOPVariable threadVar = new MOPVariable("t");

		ret += "before (Thread " + threadVar + "): ( execution(void Thread+.run()) && target(" + threadVar + ") )";
		ret += " && " + commonPointcut + "() {\n";
		
		ret += "if(Thread.currentThread() == " + threadVar + ") {\n";
		if (event.getThreadVar() != null && event.getThreadVar().length() != 0) {
			ret += "Thread " + event.getThreadVar() + " = Thread.currentThread();\n";
		}

		if(Main.translate2RV) {
			ret += mopSpec.getName() + "RVRuntimeMonitor." + event.getUniqueId() + "Event(";
			if (event.getThreadVar() != null && event.getThreadVar().length() != 0) {
				ret += event.getThreadVar();
			}
			ret += ");\n";
		} else {
			ret += eventBody;
		}
		ret += "}\n";

		ret += "}\n";

		return ret;
	}

	public String printAdviceForStartRunnable() {
		String ret = "";
		MOPVariable runnableVar = new MOPVariable("r");

		ret += "before (Runnable " + runnableVar + "): ( execution(void Runnable+.run()) && !execution(void Thread+.run()) && target(" + runnableVar + ") )";
		ret += " && " + commonPointcut + "() {\n";
		ret += "while (!" + globalLock.getName() + ".tryLock()) {\n";
		ret += "Thread.yield();\n";
		ret += "}\n";
		ret += "if(" + runnableMap + ".get(Thread.currentThread()) == " + runnableVar + ") {\n";
		if (event.getThreadVar() != null && event.getThreadVar().length() != 0) {
			ret += "Thread " + event.getThreadVar() + " = Thread.currentThread();\n";
		}
		
		if(Main.translate2RV) {
			ret += mopSpec.getName() + "RVRuntimeMonitor." + event.getUniqueId() + "Event(";
			if (event.getThreadVar() != null && event.getThreadVar().length() != 0) {
				ret += event.getThreadVar();
			}
			ret += ");\n";
		} else {
			ret += eventBody;
		}
		ret += "}\n";
		ret += globalLock.getName() + ".unlock();\n";

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
			ret += "Thread " + event.getThreadVar() + " = Thread.currentThread();\n";
		}
		if(Main.translate2RV) {
			ret += mopSpec.getName() + "RVRuntimeMonitor." + event.getUniqueId() + "Event(";
			if (event.getThreadVar() != null && event.getThreadVar().length() != 0) {
				ret += event.getThreadVar();
			}
			ret += ");\n";
		} else {
			ret += eventBody;
		}
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
