package javamop.output.combinedaspect.indexingtree.decentralized;

import java.util.HashMap;

import javamop.MOPException;
import javamop.output.MOPVariable;
import javamop.output.combinedaspect.event.advice.LocalVariables;
import javamop.output.combinedaspect.indexingtree.IndexingTree;
import javamop.output.combinedaspect.indexingtree.reftree.RefTree;
import javamop.output.monitor.SuffixMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;

public class OnePartialParamIndexingTree extends IndexingTree {
	MOPParameter firstKey;
	public MOPVariable oneParamNode;

	public OnePartialParamIndexingTree(String aspectName, MOPParameters queryParam, MOPParameters contentParam, MOPParameters fullParam, MonitorSet monitorSet,
			SuffixMonitor monitor, HashMap<String, RefTree> refTrees, boolean perthread, boolean isGeneral) throws MOPException {
		super(aspectName, queryParam, contentParam, fullParam, monitorSet, monitor, refTrees, perthread, isGeneral);

		if (anycontent) {
			this.name = new MOPVariable(aspectName + "_Set");
			if (isGeneral)
				this.oneParamNode = new MOPVariable(aspectName + "_Monitor");
		} else {
			this.name = new MOPVariable(aspectName + "__To__" + contentParam.parameterStringUnderscore() + "_Set");
		}

		this.firstKey = queryParam.get(0);
	}

	public boolean containsSet() {
		return true;
	}

	public String lookupNode(LocalVariables localVars, String monitorStr, String lastMapStr, String lastSetStr, boolean creative) {
		String ret = "";

		if(oneParamNode != null){
			MOPVariable monitor = localVars.get(monitorStr);
			ret += monitor + " = " + retrieveOneParamMonitor() + ";\n";
		}

		return ret;
	}

	public String lookupSet(LocalVariables localVars, String monitorStr, String lastMapStr, String lastSetStr, boolean creative) {
		String ret = "";

		MOPVariable lastSet = localVars.get(lastSetStr);

		if (creative){
			ret += createTree();
		}
		
		ret += lastSet + " = " + retrieveTree() + ";\n";

		return ret;
	}

	public String lookupNodeAndSet(LocalVariables localVars, String monitorStr, String lastMapStr, String lastSetStr, boolean creative) {
		String ret = "";
		
		MOPVariable lastSet = localVars.get(lastSetStr);
		
		if (creative){
			ret += createTree();
		}

		ret += lastSet + " = " + retrieveTree() + ";\n";
		
		if(oneParamNode != null){
			MOPVariable monitor = localVars.get(monitorStr);
			ret += monitor + " = " + retrieveOneParamMonitor() + ";\n";
		}
		
		return ret;
	}

	public String attachNode(LocalVariables localVars, String monitorStr, String lastMapStr, String lastSetStr) {
		String ret = "";

		if(oneParamNode != null){
			MOPVariable monitor = localVars.get(monitorStr);
			ret += retrieveOneParamMonitor() + " = " + monitor + ";\n";
		}

		return ret;
	}

	public String attachSet(LocalVariables localVars, String monitorStr, String lastMapStr, String lastSetStr) {
		String ret = "";

		MOPVariable lastSet = localVars.get(lastSetStr);
		ret += retrieveTree() + " = " + lastSet + ";\n";

		return ret;
	}

	public String addMonitor(LocalVariables localVars, String monitorStr, String tempMapStr, String tempSetStr) {
		String ret = "";

		MOPVariable monitor = localVars.get(monitorStr);
		
		ret += createTree();
		ret += retrieveTree() + ".add(" + monitor + ");\n";

		return ret;
	}

	public String retrieveTree() {
		return firstKey.getName() + "." + name.toString();
	}

	public String retrieveOneParamMonitor() {
		if (oneParamNode == null)
			return "";

		return firstKey.getName() + "." + oneParamNode.toString();
	}
	
	protected String createTree() {
		String ret = "";
		
		ret += "if (" + retrieveTree() + " == null) {\n";
		ret += retrieveTree() + " = " + "new " + monitorSet.getName() + "()" + ";\n";
		ret += "}\n";

		return ret;
	}

	public String getRefTreeType() {
		String ret = "";

		if (parentTree != null)
			return parentTree.getRefTreeType();

		return ret;
	}

	public String toString() {
		String ret = "";

		ret += monitorSet.getName() + " " + firstKey.getType() + "." + name + " = null;\n";
		if (oneParamNode != null) {
			ret += monitorClass.getOutermostName() + " " + firstKey.getType() + "." + oneParamNode + " = null;\n";
		}

		if (cache != null)
			ret += cache;

		return ret;
	}
}
