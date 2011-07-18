package javamop.output.combinedaspect.indexingtree;

import java.util.HashMap;

import javamop.output.MOPVariable;
import javamop.output.monitor.WrapperMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.mopspec.MOPParameters;

public abstract class IndexingTree {
	MOPVariable name;
	MOPParameters fullParam;
	MOPParameters queryParam;
	MOPParameters contentParam;
	MonitorSet monitorSet;
	WrapperMonitor monitor;
	IndexingCache cache = null;
	boolean anycontent = true;
	boolean perthread = false;

	public IndexingTree(String name, MOPParameters queryParam, MOPParameters contentParam, MOPParameters fullParam, MonitorSet monitorSet, WrapperMonitor monitor, boolean perthread) {
		this.queryParam = queryParam;
		this.contentParam = contentParam;
		this.fullParam = fullParam;
		this.monitorSet = monitorSet;
		this.monitor = monitor;

		if (contentParam == null) {
			anycontent = true;
		} else {
			anycontent = false;
		}
		
		this.perthread = perthread;
	}

	public MOPVariable getName() {
		return name;
	}

	public boolean hasCache() {
		return cache != null;
	}
	
	public abstract String retrieveTree();

	public abstract String addMonitor(MOPVariable map, MOPVariable obj, MOPVariable monitors, HashMap<String, MOPVariable> mopRefs, MOPVariable monitor);

	public abstract String addMonitorAfterLookup(MOPVariable map, MOPVariable set, MOPVariable monitor, HashMap<String, MOPVariable> mopRefs);

	public abstract String addExactWrapper(MOPVariable wrapper, MOPVariable lastMap, MOPVariable set, HashMap<String, MOPVariable> mopRefs);

	public abstract String addWrapper(MOPVariable wrapper, MOPVariable lastMap, MOPVariable set, HashMap<String, MOPVariable> mopRefs);

	public abstract String lookup(MOPVariable map, MOPVariable obj, HashMap<String, MOPVariable> tempRefs, boolean creative);

	public abstract String lookupExactMonitor(MOPVariable wrapper, MOPVariable lastMap, MOPVariable set, MOPVariable map, MOPVariable obj,
			HashMap<String, MOPVariable> tempRefs);

	public abstract String checkTime(MOPVariable timeCheck, MOPVariable wrapper, MOPVariable fromWrapper, MOPVariable set, MOPVariable map, MOPVariable obj);

	public abstract String getCachedValue(MOPVariable obj);

	public abstract String setCacheKeys();

	public abstract String setCacheValue(MOPVariable monitor);

	public abstract boolean containsSet();

	public abstract String toString();

}
