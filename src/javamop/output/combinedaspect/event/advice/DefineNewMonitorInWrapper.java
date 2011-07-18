package javamop.output.combinedaspect.event.advice;

import java.util.HashMap;

import javamop.output.MOPVariable;
import javamop.output.combinedaspect.indexingtree.IndexingTree;
import javamop.output.monitor.MonitorInfo;
import javamop.output.monitor.WrapperMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.MOPParameterPair;
import javamop.parser.ast.mopspec.MOPParameters;

public class DefineNewMonitorInWrapper {
	MOPVariable wrapper;
	EventDefinition event;
	GeneralAdviceBody generalAdviceBody;
	WrapperMonitor monitor;
	MonitorSet monitorSet;
	IndexingTree myIndexingTree;
	MonitorInfo monitorInfo;

	HashMap<String, MOPVariable> mopRefs = new HashMap<String, MOPVariable>();

	public DefineNewMonitorInWrapper(MOPVariable wrapper, EventDefinition event, GeneralAdviceBody generalAdviceBody) {
		this.wrapper = wrapper;
		this.event = event;
		this.generalAdviceBody = generalAdviceBody;
		this.monitor = generalAdviceBody.monitorClass;
		this.monitorSet = generalAdviceBody.monitorSet;
		this.mopRefs = generalAdviceBody.mopRefs;
		this.monitorInfo = generalAdviceBody.monitorInfo;

		for (MOPParameters param : generalAdviceBody.indexingTrees.keySet()) {
			if (param.equals(event.getMOPParametersOnSpec()))
				myIndexingTree = generalAdviceBody.indexingTrees.get(param);
		}
	}

	public String toString() {
		String ret = "";

		MOPVariable obj = new MOPVariable("obj");
		MOPVariable m = new MOPVariable("m");

		MOPVariable mainMap = new MOPVariable("mainMap");

		MOPVariable mainSet = new MOPVariable("mainSet");
		MOPVariable monitors = new MOPVariable("monitors");

		ret += "// line 4 of defineNew in Algorithm D\n";

		ret += monitor.getSubMonitor(wrapper) + " = " + "new " + monitor.getSubMonitorName() + "();\n";
		if (monitorInfo != null)
			ret += monitorInfo.newInfo(monitor.getSubMonitor(wrapper), event.getMOPParametersOnSpec());
		
		ret += monitor.getTau(wrapper) + " = " + generalAdviceBody.timestamp + "++;\n";
		ret += myIndexingTree.addWrapper(wrapper, mainMap, mainSet, mopRefs);
		ret += "\n";

		for (MOPParameters param : generalAdviceBody.indexingTrees.keySet()) {
			if (param.equals(event.getMOPParametersOnSpec()))
				continue;

			if (!event.getMOPParametersOnSpec().contains(param))
				continue;

			IndexingTree indexingTree = generalAdviceBody.indexingTrees.get(param);

			ret += "\n";
			ret += indexingTree.addMonitor(m, obj, monitors, mopRefs, wrapper);
		}

		for (MOPParameterPair paramPair : generalAdviceBody.indexingTreesForCopy.keySet()) {
			if (!paramPair.getParam2().equals(event.getMOPParametersOnSpec()))
				continue;

			IndexingTree indexingTree = generalAdviceBody.indexingTreesForCopy.get(paramPair);

			ret += "\n";
			ret += indexingTree.addMonitor(m, obj, monitors, mopRefs, wrapper);
		}

		return ret;
	}

}
