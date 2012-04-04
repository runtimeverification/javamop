package javamop.output.combinedaspect.indexingtree;

import java.util.ArrayList;
import java.util.HashMap;

import javamop.MOPException;
import javamop.output.MOPVariable;
import javamop.output.combinedaspect.event.advice.LocalVariables;
import javamop.output.combinedaspect.indexingtree.reftree.RefTree;
import javamop.output.monitor.SuffixMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.mopspec.MOPParameters;

public abstract class IndexingTree {
	public MOPVariable name;
	
	public MOPParameters fullParam;
	public MOPParameters queryParam;
	public MOPParameters contentParam;
	
	public MonitorSet monitorSet;
	public SuffixMonitor monitorClass;
	
	public IndexingCache cache = null;
	
	public boolean anycontent = true;
	public boolean perthread = false;
	public boolean isFullParam = false;
	public boolean isGeneral = false;
	
	HashMap<String, RefTree> refTrees;
	
	public RefTree parasiticRefTree = null;
	
	public IndexingTree parentTree = null;
	public ArrayList<IndexingTree> childTrees = new ArrayList<IndexingTree>();

	public IndexingTree(String aspectName, MOPParameters queryParam, MOPParameters contentParam, MOPParameters fullParam, MonitorSet monitorSet, SuffixMonitor monitor, HashMap<String, RefTree> refTrees, boolean perthread, boolean isGeneral) {
		this.queryParam = queryParam;
		this.contentParam = contentParam;
		this.fullParam = fullParam;
		this.monitorSet = monitorSet;
		this.monitorClass = monitor;

		if (contentParam == null) {
			anycontent = true;
		} else {
			anycontent = false;
		}
		
		if (queryParam != null && fullParam != null && queryParam.equals(fullParam))
			isFullParam = true;
		if (queryParam != null && contentParam != null && queryParam.equals(contentParam))
			isFullParam = true;

		
		this.perthread = perthread;
		this.isGeneral = isGeneral;
	}

	public MOPVariable getName() {
		return name;
	}

	public boolean hasCache() {
		return cache != null;
	}
	
	public IndexingCache getCache(){
		return cache;
	}

	public abstract boolean containsSet();

	/*
	 * lookupNode, lookupSet, lookupNodeAndSet retrieve data from indexing tree
	 * They can use the following local variables if necessary: obj, m, and tempRef_*. 
	 */
	public abstract String lookupNode(LocalVariables localVars, String monitorStr, String lastMapStr, String lastSetStr, boolean creative) throws MOPException;

	public abstract String lookupSet(LocalVariables localVars, String monitorStr, String lastMapStr, String lastSetStr, boolean creative) throws MOPException;

	public abstract String lookupNodeAndSet(LocalVariables localVars, String monitorStr, String lastMapStr, String lastSetStr, boolean creative) throws MOPException;

	public abstract String attachNode(LocalVariables localVars, String monitorStr, String lastMapStr, String lastSetStr) throws MOPException;

	public abstract String attachSet(LocalVariables localVars, String monitorStr, String lastMapStr, String lastSetStr) throws MOPException;

	public String addMonitor(LocalVariables localVars, String monitorStr) throws MOPException {
		return addMonitor(localVars, monitorStr, "tempMap", "monitors");
	}

	public abstract String addMonitor(LocalVariables localVars, String monitorStr, String tempMapStr, String tempSetStr) throws MOPException;

	public abstract String retrieveTree();

	public abstract String getRefTreeType();

	public abstract String toString();
	
	////////////////////////
	/*
	public abstract String retrieveTree();
	public abstract String addMonitor(MOPVariable map, MOPVariable obj, MOPVariable monitors, HashMap<String, MOPVariable> mopRefs, MOPVariable monitor);
	public abstract String getWeakReferenceAfterLookup(MOPVariable map, MOPVariable monitor, HashMap<String, MOPVariable> mopRefs);
	public abstract String addMonitorAfterLookup(MOPVariable map, MOPVariable monitor, HashMap<String, MOPVariable> mopRefs);
	public abstract String addExactWrapper(MOPVariable wrapper, MOPVariable lastMap, MOPVariable set, HashMap<String, MOPVariable> mopRefs);
	public abstract String addWrapper(MOPVariable wrapper, MOPVariable lastMap, MOPVariable set, HashMap<String, MOPVariable> mopRefs);
	public abstract String lookup(MOPVariable map, MOPVariable obj, HashMap<String, MOPVariable> tempRefs, boolean creative);
	public abstract String lookupExactMonitor(MOPVariable wrapper, MOPVariable lastMap, MOPVariable set, MOPVariable map, MOPVariable obj,
			HashMap<String, MOPVariable> tempRefs, boolean creative);
	 */
}
