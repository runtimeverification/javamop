package javamop.output.aspect.advice;

import java.util.HashMap;

import javamop.MOPException;
import javamop.Main;
import javamop.output.MOPVariable;
import javamop.output.aspect.AspectBody;
import javamop.output.aspect.GlobalLock;
import javamop.output.aspect.MOPStatistics;
import javamop.output.aspect.indexingtree.IndexingTree;
import javamop.output.monitor.WrapperMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.aspectj.BaseTypePattern;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;

public class Advice {
	JavaMOPSpec mopSpec;
	EventDefinition event;
	MonitorSet monitorSet;
	WrapperMonitor monitor;
	MOPVariable pointcutName;
	HashMap<MOPParameters, IndexingTree> indexingTrees;
	GlobalLock globalLock;
	AdviceBody adviceBody;

	public MOPStatistics stat;

	public Advice(JavaMOPSpec mopSpec, EventDefinition event, MOPVariable pointcutName, AspectBody aspectBody) throws MOPException {
		this.mopSpec = mopSpec;
		this.event = event;
		this.monitorSet = aspectBody.monitorSet;
		this.monitor = aspectBody.monitor;
		this.pointcutName = pointcutName;
		this.indexingTrees = aspectBody.indexingDecl.getIndexingTrees();
		this.globalLock = aspectBody.globalLock;

		this.stat = aspectBody.stat;

		if (mopSpec.isGeneral())
			this.adviceBody = new GeneralAdviceBody(mopSpec, event, aspectBody);
		else
			this.adviceBody = new SpecialAdviceBody(mopSpec, event, aspectBody);

	}

	public String toString() {
		String ret = "";

		if (event.getPos().equals("around"))
			ret += event.getRetType().toString() + " ";

		ret += event.getPos() + " (" + event.getParametersWithoutThreadVar().parameterDeclString() + ") ";

		if (event.getRetVal() != null && event.getRetVal().size() > 0) {
			ret += "returning (";
			ret += event.getRetVal().parameterDeclString();
			ret += ") ";
		}

		if (event.getThrowVal() != null && event.getThrowVal().size() > 0) {
			ret += "throwing (";
			ret += event.getThrowVal().parameterDeclString();
			ret += ") ";
		}

		ret += ": " + pointcutName + "(" + event.getParametersWithoutThreadVar().parameterString() + ") {\n";

		if (event.getThreadVar() != null && event.getThreadVar().length() != 0) {
			ret += "Thread " + event.getThreadVar() + " = Thread.currentThread();\n";
		}

		if (Main.statistics) {
			ret += stat.eventInc(event.getId());

			for (MOPParameter param : event.getMOPParametersOnSpec()) {
				ret += stat.paramInc(param);
			}

			ret += "\n";
		}

		ret += adviceBody;

		ret += "}\n";

		return ret;
	}
}
