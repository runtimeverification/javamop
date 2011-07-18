package javamop.output.aspect.advice;

import java.util.HashMap;

import javamop.MOPException;
import javamop.output.aspect.AspectBody;
import javamop.output.aspect.GlobalLock;
import javamop.output.aspect.indexingtree.IndexingTree;
import javamop.output.monitor.WrapperMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameters;

public class AdviceAndPointCut {
	JavaMOPSpec mopSpec;
	EventDefinition event;
	MonitorSet monitorSet;
	WrapperMonitor monitor;
	HashMap<MOPParameters, IndexingTree> indexingTrees;
	GlobalLock globalLock;

	PointCutDecl pointcut;
	Advice advice;

	public AdviceAndPointCut(JavaMOPSpec mopSpec, EventDefinition event, AspectBody aspectBody) throws MOPException {
		this.mopSpec = mopSpec;
		this.event = event;
		this.monitorSet = aspectBody.monitorSet;
		this.monitor = aspectBody.monitor;
		this.indexingTrees = aspectBody.indexingDecl.getIndexingTrees();
		this.globalLock = aspectBody.globalLock;

		this.pointcut = new PointCutDecl(mopSpec, event);

		this.advice = new Advice(mopSpec, event, this.pointcut.getName(), aspectBody);
	}

	public String toString() {
		String ret = "";

		ret += pointcut;
		ret += advice;

		return ret;
	}
}
