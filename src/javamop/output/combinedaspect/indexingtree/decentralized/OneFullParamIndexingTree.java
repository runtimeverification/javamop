package javamop.output.combinedaspect.indexingtree.decentralized;

import java.util.HashMap;

import javamop.MOPException;
import javamop.output.MOPVariable;
import javamop.output.combinedaspect.event.advice.LocalVariables;
import javamop.output.combinedaspect.indexingtree.IndexingCache;
import javamop.output.combinedaspect.indexingtree.IndexingTree;
import javamop.output.combinedaspect.indexingtree.reftree.RefTree;
import javamop.output.monitor.SuffixMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;

public class OneFullParamIndexingTree extends IndexingTree {
	MOPParameter firstKey;

	public OneFullParamIndexingTree(String aspectName, MOPParameters queryParam, MOPParameters contentParam, MOPParameters fullParam, MonitorSet monitorSet, SuffixMonitor monitor,
			HashMap<String, RefTree> refTrees, boolean perthread, boolean isGeneral) throws MOPException {
		super(aspectName, queryParam, contentParam, fullParam, monitorSet, monitor, refTrees, perthread, isGeneral);

		this.name = new MOPVariable(aspectName + "_Monitor");
		this.firstKey = queryParam.get(0);
	}

	public boolean containsSet() {
		return false;
	}

	public String lookupNode(LocalVariables localVars, String monitorStr, String lastMapStr, String lastSetStr, boolean creative) {
		String ret = "";

		MOPVariable monitor = localVars.get(monitorStr);
		ret += monitor + " = " + retrieveTree() + ";\n";

		return ret;
	}

	public String lookupSet(LocalVariables localVars, String monitorStr, String lastMapStr, String lastSetStr, boolean creative) {
		String ret = "";

		// do nothing

		return ret;
	}

	public String lookupNodeAndSet(LocalVariables localVars, String monitorStr, String lastMapStr, String lastSetStr, boolean creative) {
		return lookupNode(localVars, monitorStr, lastMapStr, lastSetStr, creative);
	}

	public String attachNode(LocalVariables localVars, String monitorStr, String lastMapStr, String lastSetStr) {
		String ret = "";

		MOPVariable monitor = localVars.get(monitorStr);

		ret += retrieveTree() + " = " + monitor + ";\n";

		return ret;
	}

	public String attachSet(LocalVariables localVars, String monitorStr, String lastMapStr, String lastSetStr) {
		String ret = "";

		// do nothing

		return ret;
	}

	public String addMonitor(LocalVariables localVars, String monitorStr, String tempMapStr, String tempSetStr) {
		String ret = "";

		MOPVariable monitor = localVars.get(monitorStr);
		ret += retrieveTree() + " = " + monitor + ";\n";

		return ret;
	}

	public String retrieveTree() {
		return firstKey.getName() + "." + name.toString();
	}

	public String getRefTreeType() {
		String ret = "";

		if (parentTree != null)
			return parentTree.getRefTreeType();

		return ret;
	}

	public String toString() {
		String ret = "";

		ret += monitorClass.getOutermostName() + " " + firstKey.getType() + "." + name + " = null;\n";

		if (cache != null)
			ret += cache;

		return ret;
	}

}
