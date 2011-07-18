package javamop.output.aspect.advice;

import java.util.HashMap;

import javamop.Main;
import javamop.output.MOPVariable;
import javamop.output.aspect.indexingtree.IndexingTree;
import javamop.output.monitor.MonitorInfo;
import javamop.output.monitor.WrapperMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameterPair;
import javamop.parser.ast.mopspec.MOPParameters;

public class DefineToFromMonitor {
	MOPParameters from;
	MOPParameters to;
	MOPVariable origWrapper;
	EventDefinition event;
	GeneralAdviceBody generalAdviceBody;
	WrapperMonitor monitor;
	MonitorSet monitorSet;
	IndexingTree fromIndexingTree;
	IndexingTree toIndexingTree;
	MonitorInfo monitorInfo;

	HashMap<String, MOPVariable> tempRefs = new HashMap<String, MOPVariable>();
	HashMap<String, MOPVariable> mopRefs = new HashMap<String, MOPVariable>();
	
	public DefineToFromMonitor(MOPParameters from, MOPParameters to, MOPVariable origWrapper, EventDefinition event, GeneralAdviceBody generalAdviceBody){
		this.from = from;
		this.to = to;
		this.origWrapper = origWrapper;
		this.event = event;
		this.generalAdviceBody = generalAdviceBody;
		this.monitor = generalAdviceBody.monitorClass;
		this.monitorSet = generalAdviceBody.monitorSet;
		this.mopRefs = generalAdviceBody.mopRefs;
		this.monitorInfo = generalAdviceBody.monitorInfo;
		
		for (MOPParameter p : generalAdviceBody.mopSpec.getParameters()) {
			tempRefs.put(p.getName(), new MOPVariable("TempRef_" + p.getName()));
			mopRefs.put(p.getName(), new MOPVariable("MOPRef_" + p.getName()));
		}

		for (MOPParameters param : generalAdviceBody.indexingTrees.keySet()) {
			if (param.equals(from))
				fromIndexingTree = generalAdviceBody.indexingTrees.get(param);
			if (param.equals(to))
				toIndexingTree = generalAdviceBody.indexingTrees.get(param);
		}
	}
	
	public String toString() {
		String ret = "";

		MOPVariable obj = new MOPVariable("obj");
		MOPVariable m = new MOPVariable("m");
		MOPVariable mainWrapper = new MOPVariable("mainWrapper");
		MOPVariable wrapper = new MOPVariable("wrapper");

		MOPVariable mainMap = new MOPVariable("mainMap");
		MOPVariable lastMap = new MOPVariable("lastMap");

		MOPVariable mainSet = new MOPVariable("mainSet");
		MOPVariable origSet = new MOPVariable("origSet");
		MOPVariable monitors = new MOPVariable("monitors");

		MOPVariable timeCheck = new MOPVariable("timeCheck");

		
		ret += "// line 1 of defineTo in Algorithm D\n";
		
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

		ret += "if (" + mainWrapper + " == null){\n";
		ret += monitor.newWrapper(mainWrapper, to);
		for (MOPParameter p : to) {
			if (from.contains(p)) {
				ret += mainWrapper + "." + mopRefs.get(p.getName()) + " = " + origWrapper + "." + mopRefs.get(p.getName()) + ";\n";
			} else if(to.contains(p)){
				ret += "if (" + tempRefs.get(p.getName()) + " == null){\n";
				ret += mainWrapper + "." + mopRefs.get(p.getName()) + " = " + tempRefs.get(p.getName()) + " = new javamoprt.MOPWeakReference(" + p.getName() + ");\n";
				ret += "} else {\n";
				ret += mainWrapper + "." + mopRefs.get(p.getName()) + " = " + tempRefs.get(p.getName()) + ";\n";
				ret += "}\n";
			}
		}
		ret += monitor.copyAliveParameters(mainWrapper, origWrapper);
		ret += toIndexingTree.addExactWrapper(mainWrapper, mainMap, mainSet, mopRefs);
		ret += "}\n";

		ret += monitor.getTau(mainWrapper) + " = " + monitor.getTau(origWrapper) + ";\n";
		ret += monitor.getSubMonitor(mainWrapper) + " = " + "(" + monitor.getSubMonitorName() + ")" + monitor.getSubMonitor(origWrapper) + ".clone();\n";
		if(monitorInfo != null)
			ret += monitorInfo.expand(monitor.getSubMonitor(mainWrapper), monitor.getSubMonitorClass(), to);

		ret += monitor.getLastEvent(mainWrapper) + " = " + monitor.getLastEvent(origWrapper) + ";\n";
		
		ret += toIndexingTree.addWrapper(mainWrapper, mainMap, mainSet, mopRefs);

		for (MOPParameters param : generalAdviceBody.indexingTrees.keySet()) {
			if (param.equals(to))
				continue;
			
			if (!to.contains(param))
				continue;

			IndexingTree indexingTree = generalAdviceBody.indexingTrees.get(param);

			ret += "\n";
			ret += indexingTree.addMonitor(m, obj, monitors, mopRefs, mainWrapper);
		}
		
		for(MOPParameterPair paramPair : generalAdviceBody.indexingTreesForCopy.keySet()){
			if(!paramPair.getParam2().equals(to))
				continue;
			
			IndexingTree indexingTree = generalAdviceBody.indexingTreesForCopy.get(paramPair);
			
			ret += "\n";
			ret += indexingTree.addMonitor(m, obj, monitors, mopRefs, mainWrapper);
		}

		
		ret += "}\n"; // timeCheck

		return ret;
	}

}
