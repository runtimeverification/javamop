package javamop.output.aspect.advice;

import java.util.HashMap;

import javamop.output.MOPVariable;
import javamop.output.aspect.AspectBody;
import javamop.output.aspect.GlobalLock;
import javamop.output.aspect.MOPStatistics;
import javamop.output.aspect.indexingtree.IndexingTree;
import javamop.output.monitor.WrapperMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameters;

public abstract class AdviceBody {
	JavaMOPSpec mopSpec;
	public EventDefinition event;
	public MonitorSet monitorSet;
	public WrapperMonitor monitorClass;
	public MOPVariable monitorName;
	public HashMap<MOPParameters, IndexingTree> indexingTrees;
	GlobalLock globalLock;
	
	public MOPStatistics stat;

	public AdviceBody(JavaMOPSpec mopSpec, EventDefinition event, AspectBody aspectBody) {
		this.mopSpec = mopSpec;
		this.event = event;
		this.monitorSet = aspectBody.monitorSet;
		this.monitorClass = aspectBody.monitor;
		this.monitorName = aspectBody.monitor.getOutermostName();
		this.indexingTrees = aspectBody.indexingDecl.getIndexingTrees();
		this.globalLock = aspectBody.globalLock;
		this.stat = aspectBody.stat;
	}

	public abstract String toString();
}
