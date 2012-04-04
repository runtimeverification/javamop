package javamop.output.combinedaspect.indexingtree.centralized;

import java.util.HashMap;

import javamop.MOPException;
import javamop.output.MOPVariable;
import javamop.output.combinedaspect.event.advice.LocalVariables;
import javamop.output.combinedaspect.indexingtree.IndexingTree;
import javamop.output.combinedaspect.indexingtree.reftree.RefTree;
import javamop.output.monitor.SuffixMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.mopspec.MOPParameters;

public class NoParamIndexingTree extends IndexingTree {
	public MOPVariable globalNode;

	public NoParamIndexingTree(String aspectName, MOPParameters queryParam, MOPParameters contentParam, MOPParameters fullParam, MonitorSet monitorSet, SuffixMonitor monitor,
			HashMap<String, RefTree> refTrees, boolean perthread, boolean isGeneral) throws MOPException {
		super(aspectName, queryParam, contentParam, fullParam, monitorSet, monitor, refTrees, perthread, isGeneral);

		if (anycontent) {
			if (fullParam.size() == 0) {
				this.name = new MOPVariable(aspectName + "_Monitor");
			} else {
				this.name = new MOPVariable(aspectName + "_Set");
				if (isGeneral)
					this.globalNode = new MOPVariable(aspectName + "_Monitor");
			}
		} else {
			if (contentParam.size() == 0) {
				this.name = new MOPVariable(aspectName + "_Monitor");
			} else {
				this.name = new MOPVariable(aspectName + "__To__" + contentParam.parameterStringUnderscore() + "_Set");
			}
		}
	}

	public String lookupNode(LocalVariables localVars, String monitorStr, String lastMapStr, String lastSetStr, boolean creative) {
		String ret = "";

		if (isFullParam || globalNode != null) {
			MOPVariable monitor = localVars.get(monitorStr);

			if (isFullParam) {
				ret += monitor + " = " + retrieveTree() + ";\n";
			} else if (globalNode != null) {
				ret += monitor + " = " + retrieveGlobalMonitor() + ";\n";
			}
		}

		return ret;
	}

	public String lookupSet(LocalVariables localVars, String monitorStr, String lastMapStr, String lastSetStr, boolean creative) {
		String ret = "";

		if (!isFullParam) {
			MOPVariable lastSet = localVars.get(lastSetStr);

			ret += lastSet + " = " + retrieveTree() + ";\n";
		}

		return ret;
	}

	public String lookupNodeAndSet(LocalVariables localVars, String monitorStr, String lastMapStr, String lastSetStr, boolean creative) {
		String ret = "";

		if (isFullParam) {
			MOPVariable monitor = localVars.get(monitorStr);

			ret += monitor + " = " + retrieveTree() + ";\n";
		} else {
			MOPVariable lastSet = localVars.get(lastSetStr);

			ret += lastSet + " = " + retrieveTree() + ";\n";
			if (globalNode != null) {
				MOPVariable monitor = localVars.get(monitorStr);

				ret += monitor + " = " + retrieveGlobalMonitor() + ";\n";
			}
		}

		return ret;
	}

	public String attachNode(LocalVariables localVars, String monitorStr, String lastMapStr, String lastSetStr) {
		String ret = "";

		if (isFullParam || globalNode != null) {
			MOPVariable monitor = localVars.get(monitorStr);

			if (isFullParam) {
				ret += retrieveTree() + " = " + monitor + ";\n";
			} else if (globalNode != null) {
				ret += retrieveGlobalMonitor() + " = " + monitor + ";\n";
				ret += retrieveTree() + ".add(" + monitor + ");\n";
			}
		}

		return ret;
	}

	public String attachSet(LocalVariables localVars, String monitorStr, String lastMapStr, String lastSetStr) {
		String ret = "";

		if (!isFullParam) {
			MOPVariable lastSet = localVars.get(lastSetStr);

			ret += retrieveTree() + " = " + lastSet + ";\n";
		}

		return ret;
	}

	public String addMonitor(LocalVariables localVars, String monitorStr, String tempMapStr, String tempSetStr) {
		String ret = "";
		MOPVariable monitor = localVars.get(monitorStr);

		if (isFullParam) {
			ret += retrieveTree() + " = " + monitor + ";\n";
		} else {
			ret += retrieveTree() + ".add(" + monitor + ");\n";
		}

		
		return ret;
	}

	public boolean containsSet() {
		return fullParam.size() != 0;
	}

	public String retrieveTree() {
		if (perthread) {
			String ret = "";

			ret += "(";

			ret += "(" + monitorClass.getOutermostName() + ")";

			ret += name + ".get()";
			ret += ")";

			return ret;

		} else {
			return name.toString();
		}
	}

	public String retrieveGlobalMonitor() {
		if (globalNode == null)
			return "";

		if (perthread) {
			String ret = "";

			ret += "(";
			ret += "(" + monitorClass.getOutermostName() + ")";
			ret += globalNode + ".get()";
			ret += ")";

			return ret;
		} else {
			return globalNode.toString();
		}
	}
	
	public String getRefTreeType(){
		return "";
	}

	public String toString() {
		String ret = "";

		if (perthread) {
			ret += "static final ThreadLocal " + name + " = new ThreadLocal() {\n";
			ret += "protected Object initialValue(){\n";
			ret += "return ";

			if (isFullParam) {
				ret += "new " + monitorClass.getOutermostName() + "();\n";
			} else {
				ret += "new " + monitorSet.getName() + "();\n";
			}

			ret += "}\n";
			ret += "};\n";

			if (globalNode != null) {
				ret += "static final ThreadLocal " + globalNode + " = new ThreadLocal() {\n";
				ret += "protected Object initialValue(){\n";
				ret += "return null;\n";

				ret += "}\n";
				ret += "};\n";
			}
		} else {
			if (isFullParam) {
				ret += "static " + monitorClass.getOutermostName() + " " + name + " = new " + monitorClass.getOutermostName() + "();\n";
			} else {
				ret += "static " + monitorSet.getName() + " " + name + " = new " + monitorSet.getName() + "();\n";
				if (globalNode != null) {
					ret += "static " + monitorClass.getOutermostName() + " " + globalNode + " = null;\n";
				}
			}
		}

		return ret;
	}
}
