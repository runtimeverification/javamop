package javamop.output.aspect.specialevent;

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
import javamop.parser.ast.aspectj.TypePattern;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;

public class EndObject {
	JavaMOPSpec mopSpec;
	EventDefinition event;
	MonitorSet monitorSet;
	WrapperMonitor monitor;
	HashMap<MOPParameters, IndexingTree> indexingTrees;
	GlobalLock globalLock;

	String endObjectVar;
	TypePattern endObjectType;
	IndexingTree indexingTree;
	
	boolean isStart;
	AdviceBody eventBody = null;
	
	MOPVariable endObjectSupportType;

	public EndObject(JavaMOPSpec mopSpec, EventDefinition event, AspectBody aspectBody) throws MOPException {
		if (!event.isEndObject())
			throw new MOPException("EndObject should be defined only for endObject pointcut.");

		this.mopSpec = mopSpec;
		this.event = event;
		this.monitorSet = aspectBody.monitorSet;
		this.monitor = aspectBody.monitor;
		this.indexingTrees = aspectBody.indexingDecl.getIndexingTrees();
		this.globalLock = aspectBody.globalLock;

		this.endObjectType = event.getEndObjectType();
		this.endObjectVar = event.getEndObjectVar();
		if (this.endObjectVar == null || this.endObjectVar.length() == 0)
			throw new MOPException("The variable for an endObject pointcut is not defined.");
		this.endObjectSupportType = new MOPVariable(endObjectType.toString() + "MOPFinalized"); 
		
		this.isStart = event.isStartEvent();

		MOPParameter endParam = event.getMOPParametersOnSpec().getParam(event.getEndObjectVar());
		MOPParameters endParams = new MOPParameters();
		if (endParam != null)
			endParams.add(endParam);

		for (MOPParameters params : indexingTrees.keySet()) {
			if (endParams.equals(params))
				this.indexingTree = indexingTrees.get(params);
		}
		
		if (mopSpec.isGeneral())
			this.eventBody = new GeneralAdviceBody(mopSpec, event, aspectBody);
		else
			this.eventBody = new SpecialAdviceBody(mopSpec, event, aspectBody);
	}

	public String printDecl() {
		String ret = "";

		ret += "public static abstract class " + endObjectSupportType + "{\n";
		ret += "protected void finalize() throws Throwable{\n";
		ret += "try {\n";
		ret += endObjectType + " " + endObjectVar + " = (" + endObjectType + ")this;\n";
		ret += eventBody;
		ret += "} finally {\n";
		ret += "super.finalize();\n";
		ret += "}\n";
		ret += "}\n"; //method
		ret += "}\n"; //abstract class
		ret += "\n";
		
		ret += "declare parents : " + endObjectType + " extends " + endObjectSupportType + ";\n";
		ret += "\n";
		
		ret += "after(" + endObjectType + " " + endObjectVar + ") : execution(void " + endObjectType + ".finalize()) && this(" + endObjectVar + "){\n";
		ret += eventBody;
		ret += "}\n";

		return ret;
	}


}
