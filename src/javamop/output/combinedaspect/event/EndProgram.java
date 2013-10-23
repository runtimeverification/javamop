package javamop.output.combinedaspect.event;

import java.util.ArrayList;

import javamop.MOPException;
import javamop.output.MOPVariable;
import javamop.output.combinedaspect.CombinedAspect;
import javamop.output.combinedaspect.event.advice.AdviceBody;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;

public class EndProgram {
	MOPVariable hookName = null;

	ArrayList<EndThread> endThreadEvents = new ArrayList<EndThread>();
	ArrayList<AdviceBody> eventBodies = new ArrayList<AdviceBody>();

	public EndProgram(String name) {
		this.hookName = new MOPVariable(name + "_DummyHookThread");
	}

	public void addEndProgramEvent(JavaMOPSpec mopSpec, EventDefinition event, CombinedAspect combinedAspect) throws MOPException {
		if (!event.isEndProgram())
			throw new MOPException("EndProgram should be defined only for an endProgram pointcut.");

		this.eventBodies.add(new AdviceBody(mopSpec, event, combinedAspect));
	}

	public void registerEndThreadEvents(ArrayList<EndThread> endThreadEvents) {
		this.endThreadEvents.addAll(endThreadEvents);
	}

	public String printAddStatement() {
		String ret = "";
		
		if(eventBodies.size() == 0 && endThreadEvents.size() == 0)
			return ret;

		ret += "Runtime.getRuntime().addShutdownHook(new " + hookName + "());\n";

		return ret;
	}

	public String printHookThread() {
		String ret = "";

		if(eventBodies.size() == 0 && endThreadEvents.size() == 0)
			return ret;

		ret += "class " + hookName + " extends Thread {\n";
		ret += "public void run(){\n";

		if (endThreadEvents != null && endThreadEvents.size() > 0) {
			for (EndThread endThread : endThreadEvents) {
				ret += endThread.printAdviceBodyAtEndProgram();
			}
		}

		for (AdviceBody eventBody : eventBodies) {
			if (eventBodies.size() > 1) {
				ret += "{\n";
			}

			ret += EventManager.EventMethodHelper.methodName(eventBody.specName, eventBody.event, eventBody.fileName);
			ret += "();\n";

			if (eventBodies.size() > 1) {
				ret += "}\n";
			}
		}

		ret += "}\n";
		ret += "}\n";

		return ret;
	}
}
