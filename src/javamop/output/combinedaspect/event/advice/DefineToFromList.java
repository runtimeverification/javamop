package javamop.output.combinedaspect.event.advice;

import java.util.HashMap;

import javamop.output.MOPVariable;
import javamop.output.combinedaspect.indexingtree.IndexingTree;
import javamop.output.monitor.MonitorInfo;
import javamop.output.monitor.WrapperMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameterPair;
import javamop.parser.ast.mopspec.MOPParameters;

public class DefineToFromList {
	MOPParameters from;
	MOPParameters to;
	MOPVariable origSet;
	EventDefinition event;
	GeneralAdviceBody generalAdviceBody;
	WrapperMonitor monitor;
	MonitorSet monitorSet;
	IndexingTree toIndexingTree;
	MonitorInfo monitorInfo;

	HashMap<String, MOPVariable> tempRefs = new HashMap<String, MOPVariable>();
	HashMap<String, MOPVariable> mopRefs = new HashMap<String, MOPVariable>();

	public DefineToFromList(MOPParameters from, MOPParameters to, MOPVariable origSet, EventDefinition event, GeneralAdviceBody generalAdviceBody) {
		this.from = from;
		this.to = to;
		this.origSet = origSet;
		this.event = event;
		this.generalAdviceBody = generalAdviceBody;
		this.monitor = generalAdviceBody.monitorClass;
		this.monitorSet = generalAdviceBody.monitorSet;
		this.monitorInfo = generalAdviceBody.monitorInfo;

		for (MOPParameter p : generalAdviceBody.mopSpec.getParameters()) {
			tempRefs.put(p.getName(), new MOPVariable("TempRef_" + p.getName()));
			mopRefs.put(p.getName(), new MOPVariable("MOPRef_" + p.getName()));
		}

		for (MOPParameters param : generalAdviceBody.indexingTrees.keySet()) {
			if (param.equals(to))
				toIndexingTree = generalAdviceBody.indexingTrees.get(param);
		}
	}

	public String toString() {
		String ret = "";

		MOPVariable obj = new MOPVariable("obj");
		MOPVariable m = new MOPVariable("m");
		MOPVariable mainWrapper = new MOPVariable("mainWrapper");
		MOPVariable origWrapper = new MOPVariable("origWrapper");
		MOPVariable lastWrapper = new MOPVariable("lastWrapper");
		MOPVariable wrapper = new MOPVariable("wrapper");

		MOPVariable lastMap = new MOPVariable("lastMap");

		MOPVariable lastSet = new MOPVariable("lastSet");
		MOPVariable monitors = new MOPVariable("monitors");

		MOPVariable num_terminated_monitors = new MOPVariable("num_terminated_monitors");
		MOPVariable i = new MOPVariable("i");

		MOPVariable timeCheck = new MOPVariable("timeCheck");

		ret += "int " + num_terminated_monitors + " = 0 ;\n";
		ret += "for(int " + i + " = 0; " + i + " + " + num_terminated_monitors + " < " + origSet + ".size; " + i + " ++){\n";
		ret += origWrapper + " = (" + monitor.getOutermostName() + ")" + origSet + ".elementData[" + i + " + "
				+ num_terminated_monitors + "];\n";

		MOPParameters newParam = new MOPParameters();

		for (MOPParameter p : from) {
			if (event.getMOPParametersOnSpec().contains(p))
				continue;

			newParam.add(p);

			ret += p.getType() + " " + p.getName() + " = " + "(" + p.getType() + ")" + origWrapper + "." + this.mopRefs.get(p.getName()) + ".get();\n";
		}

		ret += "if(" + origWrapper + ".MOP_terminated";
		for (MOPParameter p : newParam)
			ret += " || " + p.getName() + " == null";
		ret += "){\n";
		
		ret += "if(" + i + " + " + num_terminated_monitors + " + 1 < " + origSet + ".size){\n";
		ret += "do{\n";
		ret += origWrapper + " = (" + monitor.getOutermostName() + ")" + origSet + ".elementData[" + i + " + (++" + num_terminated_monitors + ")];\n";
		for (MOPParameter p : newParam)
			ret += p.getName() + " = " + "(" + p.getType() + ")" + origWrapper + "." + this.mopRefs.get(p.getName()) + ".get();\n";
		ret += "} while(" + "(" + origWrapper + ".MOP_terminated";
		for (MOPParameter p : newParam)
			ret += " || " + p.getName() + " == null";
		ret += ")";
		ret += " && " + i + " + " + num_terminated_monitors + " + 1 < " + origSet + ".size);\n";
		
		ret += "if(" + origWrapper + ".MOP_terminated";
		for (MOPParameter p : newParam)
			ret += " || " + p.getName() + " == null";
		ret += "){\n";
		ret += num_terminated_monitors + "++;\n";
		ret += "break;\n";
		ret += "}\n";
		ret += "} else {\n";
		ret += num_terminated_monitors + "++;\n";
		ret += "break;\n";
		ret += "}\n";
		ret += "}\n";
		
		ret += "if(" + num_terminated_monitors + " != 0){\n";
		ret += origSet + ".elementData[" + i + "] = " + origWrapper + ";\n";
		ret += "}\n";
		ret += "\n";

		boolean found = false;
		for (MOPParameters param : generalAdviceBody.indexingTrees.keySet()) {
			if (param.equals(to)) {
				IndexingTree indexingTree = generalAdviceBody.indexingTrees.get(param);
				ret += indexingTree.lookupExactMonitor(lastWrapper, lastMap, lastSet, m, obj, tempRefs);
				found = true;
			}
		}
		
		if (!found)
			throw new Error("[Internal] cannot find the indexing tree");

		ret += "if (" + lastWrapper + " == null || " + monitor.getSubMonitor(lastWrapper) + " == null" + ") {\n";
		ret += "// line 1 of defineTo in Algorithm D\n";
		ret += "// from: " + from + "\n";
		ret += "// to: " + to + "\n";
		ret += "boolean " + timeCheck + " = true;\n";

		for (MOPParameters param : generalAdviceBody.indexingTrees.keySet()) {
			if (to.contains(param) && !from.contains(param)) {
				IndexingTree indexingTree = generalAdviceBody.indexingTrees.get(param);

				ret += "if (" + timeCheck + "){\n";
				ret += indexingTree.checkTime(timeCheck, wrapper, origWrapper, monitors, m, obj);
				ret += "}\n"; // timeCheck
				ret += "\n";
			}
		}

		ret += "if (" + timeCheck + "){\n";

		ret += "if (" + lastWrapper + " == null){\n";
		ret += monitor.newWrapper(lastWrapper, to);

		for (MOPParameter p : generalAdviceBody.mopSpec.getParameters()) {
			if (from.contains(p)) {
				ret += lastWrapper + "." + mopRefs.get(p.getName()) + " = " + origWrapper + "." + mopRefs.get(p.getName()) + ";\n";
			} else if (to.contains(p)){
				ret += "if (" + tempRefs.get(p.getName()) + " == null){\n";
				ret += lastWrapper + "." + mopRefs.get(p.getName()) + " = " + tempRefs.get(p.getName()) + " = new javamoprt.MOPWeakReference(" + p.getName() + ");\n";
				ret += "} else {\n";
				ret += lastWrapper + "." + mopRefs.get(p.getName()) + " = " + tempRefs.get(p.getName()) + ";\n";
				ret += "}\n";
			}
		}
		ret += monitor.copyAliveParameters(lastWrapper, origWrapper);
		ret += toIndexingTree.addExactWrapper(lastWrapper, lastMap, lastSet, mopRefs);
		ret += "}\n";

		ret += monitor.getTau(lastWrapper) + " = " + monitor.getTau(origWrapper) + ";\n";
		ret += monitor.getSubMonitor(lastWrapper) + " = " + "(" + monitor.getSubMonitorName() + ")" + monitor.getSubMonitor(origWrapper) + ".clone();\n";
		if(monitorInfo != null)
			ret += monitorInfo.expand(monitor.getSubMonitor(lastWrapper), monitor.getSubMonitorClass(), to);

		ret += monitor.getLastEvent(lastWrapper) + " = " + monitor.getLastEvent(origWrapper) + ";\n";

		ret += toIndexingTree.addWrapper(lastWrapper, lastMap, lastSet, mopRefs);

		for (MOPParameters param : generalAdviceBody.indexingTrees.keySet()) {
			if (!to.contains(param))
				continue;

			IndexingTree indexingTree = generalAdviceBody.indexingTrees.get(param);

			ret += "\n";
			ret += indexingTree.addMonitor(m, obj, monitors, mopRefs, lastWrapper);
		}
		
		for(MOPParameterPair paramPair : generalAdviceBody.indexingTreesForCopy.keySet()){
			if(!paramPair.getParam2().equals(to))
				continue;
			
			IndexingTree indexingTree = generalAdviceBody.indexingTreesForCopy.get(paramPair);
			
			ret += "\n";
			ret += indexingTree.addMonitor(m, obj, monitors, mopRefs, lastWrapper);
		}

		ret += "}\n"; // timeCheck

		ret += "}\n"; // null check of wrapper

		ret += "}\n"; // for

		ret += "if(" + num_terminated_monitors + " != 0){\n";
		ret += origSet + ".size -= " + num_terminated_monitors + ";\n";
		ret += "for(int " + i + " = " + origSet + ".size;";
		ret += " " + i + " < " + origSet + ".size + " + num_terminated_monitors + ";";
		ret += " " + i + "++){\n";
		ret += origSet + ".elementData[" + i + "] = null;\n";
		ret += "}\n";
		ret += "}\n";

		return ret;
	}

}
