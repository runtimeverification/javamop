package javamop.output.aspect.specialevent;

import java.util.ArrayList;
import java.util.HashMap;

import javamop.MOPException;
import javamop.output.MOPVariable;
import javamop.output.aspect.AspectBody;
import javamop.output.aspect.GlobalLock;
import javamop.output.aspect.advice.AdviceBody;
import javamop.output.aspect.advice.GeneralAdviceBody;
import javamop.output.aspect.advice.SpecialAdviceBody;
import javamop.output.aspect.indexingtree.IndexingTree;
import javamop.output.monitor.WrapperMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameters;

public class EndProgram {
	JavaMOPSpec mopSpec;
	EventDefinition event;
	MonitorSet monitorSet;
	WrapperMonitor monitor;
	HashMap<MOPParameters, IndexingTree> indexingTrees;
	GlobalLock globalLock;

	ArrayList<EndThread> endThreadEvents;

	MOPVariable hookName;
	AdviceBody eventBody = null;

	public EndProgram(JavaMOPSpec mopSpec, EventDefinition event, ArrayList<EndThread> endThreadEvents, AspectBody aspectBody)
			throws MOPException {
		if (!event.isEndProgram())
			throw new MOPException("EndProgram should be defined only for an endProgram pointcut.");

		this.mopSpec = mopSpec;
		this.event = event;
		this.monitorSet = aspectBody.monitorSet;
		this.monitor = aspectBody.monitor;
		this.indexingTrees = aspectBody.indexingDecl.getIndexingTrees();
		this.globalLock = aspectBody.globalLock;

		this.endThreadEvents = endThreadEvents;
		this.hookName = new MOPVariable(event.getId() + "HookThread");

		if (mopSpec.isGeneral())
			this.eventBody = new GeneralAdviceBody(mopSpec, event, aspectBody);
		else
			this.eventBody = new SpecialAdviceBody(mopSpec, event, aspectBody);
	}

	public EndProgram(JavaMOPSpec mopSpec, ArrayList<EndThread> endThreadEvents, AspectBody aspectBody) throws MOPException {
		this.mopSpec = mopSpec;
		this.monitorSet = aspectBody.monitorSet;
		this.monitor = aspectBody.monitor;
		this.indexingTrees = aspectBody.indexingDecl.getIndexingTrees();
		this.globalLock = aspectBody.globalLock;
		this.endThreadEvents = endThreadEvents;

		this.hookName = new MOPVariable(mopSpec.getName() + "_DummyHookThread");
	}

	public String printAddStatement() {
		String ret = "";

		ret += "Runtime.getRuntime().addShutdownHook(new " + hookName + "());\n";

		return ret;
	}

	public String printHookThread() {
		String ret = "";

		ret += "class " + hookName + " extends Thread {\n";
		ret += "public void run(){\n";

		if (endThreadEvents != null && endThreadEvents.size() > 0) {
			for (EndThread endThread : endThreadEvents) {
				ret += endThread.printAdviceBodyAtEndProgram();
			}
		}

		if (eventBody != null)
			ret += eventBody;

		ret += "}\n";
		ret += "}\n";

		return ret;
	}
}
