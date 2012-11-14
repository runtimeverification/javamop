package javamop.output.combinedaspect.event.advice;

import java.util.HashMap;

import javamop.output.MOPVariable;
import javamop.output.combinedaspect.CombinedAspect;
import javamop.output.combinedaspect.MOPStatistics;
import javamop.output.combinedaspect.indexingtree.IndexingDecl;
import javamop.output.combinedaspect.indexingtree.IndexingTree;
import javamop.output.combinedaspect.indexingtree.reftree.RefTree;
import javamop.output.monitor.SuffixMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameters;

public abstract class AdviceBody {
	JavaMOPSpec mopSpec;
	public EventDefinition event;
	public MonitorSet monitorSet;
	public SuffixMonitor monitorClass;
	public MOPVariable monitorName;
	public HashMap<MOPParameters, IndexingTree> indexingTrees;
	public IndexingDecl indexingDecl;
	public HashMap<String, RefTree> refTrees;
	
	public MOPStatistics stat;
	
	public boolean isGeneral;
	MOPParameters eventParams;

	public boolean isFullParam;
	CombinedAspect aspect;
	
	public AdviceBody(JavaMOPSpec mopSpec, EventDefinition event, CombinedAspect combinedAspect) {
		this.mopSpec = mopSpec;
		this.aspect = combinedAspect;
		this.event = event;
		this.eventParams = event.getMOPParametersOnSpec();
		this.monitorSet = combinedAspect.monitorSets.get(mopSpec);
		this.monitorClass = combinedAspect.monitors.get(mopSpec);
		this.monitorClass.setAspectName(combinedAspect.getAspectName());
		this.monitorName = monitorClass.getOutermostName();
		this.indexingDecl = combinedAspect.indexingTreeManager.getIndexingDecl(mopSpec);
		this.indexingTrees = indexingDecl.getIndexingTrees();
		this.stat = combinedAspect.statManager.getStat(mopSpec);
		this.refTrees = combinedAspect.indexingTreeManager.refTrees;
		this.isGeneral = mopSpec.isGeneral();
		this.isFullParam = eventParams.equals(mopSpec.getParameters());
	}

	public abstract String toString();
	
	public abstract String toStringForShutdownHook();
}
