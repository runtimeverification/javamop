package javamop.output.combinedaspect.event.advice;

import java.util.ArrayList;
import java.util.HashMap;

import javamop.MOPException;
import javamop.output.MOPVariable;
import javamop.output.combinedaspect.CombinedAspect;
import javamop.output.combinedaspect.indexingtree.IndexingTree;
import javamop.output.monitor.MonitorInfo;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameterPair;
import javamop.parser.ast.mopspec.MOPParameterSet;
import javamop.parser.ast.mopspec.MOPParameters;

public class GeneralAdviceBody extends AdviceBody {
	AroundAdviceLocalDecl aroundLocalDecl = null;
	AroundAdviceReturn aroundAdviceReturn = null;
	public IndexingTree indexingTree;
	MOPParameterSet enable;

	public HashMap<MOPParameterPair, IndexingTree> indexingTreesForCopy = new HashMap<MOPParameterPair, IndexingTree>();
	public HashMap<MOPParameterPair, IndexingTree> myIndexingTreesForCopy = new HashMap<MOPParameterPair, IndexingTree>();

	public ArrayList<MOPParameterPair> paramPairsForCopy = new ArrayList<MOPParameterPair>();

	MOPVariable timestamp;

	HashMap<String, MOPVariable> tempRefs = new HashMap<String, MOPVariable>();
	HashMap<String, MOPVariable> mopRefs = new HashMap<String, MOPVariable>();

	boolean isFullBinding;
	boolean isConnected;
	MonitorInfo monitorInfo;

	// assumes: mopSpec.getParameters().size() != 0
	public GeneralAdviceBody(JavaMOPSpec mopSpec, EventDefinition event, CombinedAspect combinedAspect) throws MOPException {
		super(mopSpec, event, combinedAspect);

		this.isFullBinding = mopSpec.isFullBinding();
		this.isConnected = mopSpec.isConnected();
		if (this.isFullBinding || this.isConnected)
			this.monitorInfo = new MonitorInfo(mopSpec);

		this.timestamp = combinedAspect.timestampManager.getTimestamp(mopSpec);
		this.indexingTreesForCopy = indexingDecl.getIndexingTreesForCopy();

		if (mopSpec.has__SKIP() || event.getPos().equals("around"))
			aroundLocalDecl = new AroundAdviceLocalDecl();
		if (event.getPos().equals("around"))
			aroundAdviceReturn = new AroundAdviceReturn(event.getRetType(), event.getParametersWithoutThreadVar());

		for (MOPParameter p : event.getMOPParametersOnSpec()) {
			tempRefs.put(p.getName(), new MOPVariable("TempRef_" + p.getName()));
			mopRefs.put(p.getName(), new MOPVariable("MOPRef_" + p.getName()));
		}

		for (MOPParameters param : indexingTrees.keySet()) {
			if (param.equals(event.getMOPParametersOnSpec()))
				this.indexingTree = indexingTrees.get(param);
		}

		MOPParameters param = event.getMOPParametersOnSpec();
		enable = combinedAspect.enableSets.get(mopSpec).getEnable(event.getId());

		for (MOPParameters enableEntity : enable) {
			if (enableEntity.size() == 0) {
				boolean found = false;
				for (EventDefinition event2 : mopSpec.getEvents()) {
					if (event2.getMOPParametersOnSpec().size() == 0)
						found = true;
				}

				if (!found)
					continue;
			}

			MOPParameters unionOfEnableEntityAndParam = MOPParameters.unionSet(enableEntity, param);
			unionOfEnableEntityAndParam = mopSpec.getParameters().sortParam(unionOfEnableEntityAndParam);

			// line 2 of createNewMonitorStates
			if (!enableEntity.contains(param)) {
				// line 3 of createNewMonitorStates
				MOPParameters intersectionOfEnableEntityAndParam = MOPParameters.intersectionSet(enableEntity, param);
				intersectionOfEnableEntityAndParam = mopSpec.getParameters().sortParam(intersectionOfEnableEntityAndParam);

				if (!param.contains(enableEntity)) {
					for (MOPParameter p : unionOfEnableEntityAndParam) {
						tempRefs.put(p.getName(), new MOPVariable("TempRef_" + p.getName()));
						mopRefs.put(p.getName(), new MOPVariable("MOPRef_" + p.getName()));
					}

					for (MOPParameterPair paramPair : indexingDecl.getIndexingTreesForCopy().keySet()) {
						if (paramPair.getParam1().equals(intersectionOfEnableEntityAndParam) && paramPair.getParam2().equals(enableEntity)) {
							IndexingTree tempIndexingTree = indexingDecl.getIndexingTreesForCopy().get(paramPair);
							if (tempIndexingTree != null) {
								this.myIndexingTreesForCopy.put(paramPair, tempIndexingTree);

								this.paramPairsForCopy.add(paramPair);
							}
						}
					}

				} else {
					this.paramPairsForCopy.add(new MOPParameterPair(enableEntity, enableEntity));

					for (MOPParameter p : enableEntity) {
						tempRefs.put(p.getName(), new MOPVariable("TempRef_" + p.getName()));
						mopRefs.put(p.getName(), new MOPVariable("MOPRef_" + p.getName()));
					}
				}

			}

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

		MOPVariable mainMap = new MOPVariable("mainMap");
		MOPVariable origMap = new MOPVariable("origMap");
		MOPVariable lastMap = new MOPVariable("lastMap");

		MOPVariable mainSet = new MOPVariable("mainSet");
		MOPVariable origSet = new MOPVariable("origSet");
		MOPVariable monitors = new MOPVariable("monitors");
		MOPVariable lastSet = new MOPVariable("lastSet");
		MOPVariable thisJoinPoint = new MOPVariable("thisJoinPoint");

		if (aroundLocalDecl != null)
			ret += aroundLocalDecl;

		if (event.getMOPParametersOnSpec().size() != 0) {
			ret += "Object " + obj + " = null;\n";
		}

		ret += "javamoprt.MOPMap " + m + ";\n";
		ret += monitorName + " " + mainWrapper + ";\n";
		ret += monitorName + " " + origWrapper + ";\n";
		ret += monitorName + " " + lastWrapper + ";\n";
		ret += monitorName + " " + wrapper + ";\n";

		ret += "javamoprt.MOPMap " + mainMap + " = null;\n";
		ret += "javamoprt.MOPMap " + origMap + " = null;\n";
		ret += "javamoprt.MOPMap " + lastMap + " = null;\n";

		ret += monitorSet.getName() + " " + mainSet + " = null;\n";
		ret += monitorSet.getName() + " " + origSet + " = null;\n";
		ret += monitorSet.getName() + " " + monitors + " = null;\n";
		ret += monitorSet.getName() + " " + lastSet + " = null;\n";
		for (MOPVariable tempRef : tempRefs.values()) {
			ret += "javamoprt.MOPWeakReference " + tempRef + " = null;\n";
		}

		// separator
		if (ret.length() != 0)
			ret += "\n";

		if (mopSpec.isSync())
			ret += "synchronized(" + globalLock.getName() + ") {\n";

		// cache
		if (indexingTree.hasCache()) {
			ret += indexingTree.getCachedValue(obj);

			ret += "if (" + obj + " == null) {\n";
		}

		// lookup
		ret += indexingTree.lookupExactMonitor(mainWrapper, mainMap, mainSet, m, obj, tempRefs);

		if (indexingTree.hasCache()) {
			ret += indexingTree.setCacheKeys();
			if (indexingTree.containsSet())
				ret += indexingTree.setCacheValue(mainSet);
			else
				ret += indexingTree.setCacheValue(mainWrapper);

			ret += "} else {\n";
			if (indexingTree.containsSet()) {
				ret += mainSet + " = " + "(" + monitorSet.getName() + ")" + obj + ";\n";
				ret += monitorSet.getNode(mainWrapper, mainSet);
			} else {
				ret += mainWrapper + " = " + "(" + monitorClass.getOutermostName() + ")" + obj + ";\n";
			}
			ret += "}\n";
		}

		// separator
		ret += "\n";

		// main, line 1
		ret += "// line 1 of Main in Algorithm D\n";
		ret += "if (" + mainWrapper + " == null || " + monitorClass.getSubMonitor(mainWrapper) + " == null" + ") {\n";

		// main, line 2
		// createNewMonitorStates, line 1
		if (myIndexingTreesForCopy.size() != 0) {
			ret += "// line 2 of Main in Algorithm D\n";
		}
		for (int i = 0; i < paramPairsForCopy.size(); i++) {
			MOPParameterPair paramPair = paramPairsForCopy.get(i);
			IndexingTree indexingTreeForCopy = myIndexingTreesForCopy.get(paramPair);

			if (!event.getMOPParametersOnSpec().contains(paramPair.getParam2())) {
				ret += indexingTreeForCopy.lookup(m, obj, tempRefs, false);
				ret += origSet + " = (" + monitorSet.getName() + ") " + obj + ";\n";

				ret += "if (" + origSet + " != null) {\n";

				ret += "// line 4 of createNewMonitorStates in Algorithm D\n";
				// defineTo
				MOPParameters unionOfEnableAndEventParams = MOPParameters.unionSet(paramPair.getParam2(), event.getMOPParametersOnSpec());
				unionOfEnableAndEventParams = mopSpec.getParameters().sortParam(unionOfEnableAndEventParams);

				ret += new DefineToFromList(paramPair.getParam2(), unionOfEnableAndEventParams, origSet, event, this);

				ret += "}\n";
			} else {
				for (MOPParameters param : indexingTrees.keySet()) {
					if (param.equals(paramPair.getParam2())) {
						indexingTreeForCopy = indexingTrees.get(param);
					}
				}

				ret += "if (" + mainWrapper + " == null || " + monitorClass.getSubMonitor(mainWrapper) + " == null" + ") {\n";
				ret += indexingTreeForCopy.lookupExactMonitor(origWrapper, origMap, origSet, m, obj, tempRefs);
				ret += "if (" + origWrapper + " != null && " + monitorClass.getSubMonitor(origWrapper) + " != null" + ") {\n";

				MOPParameters unionOfEnableAndEventParams = MOPParameters.unionSet(paramPair.getParam2(), event.getMOPParametersOnSpec());
				unionOfEnableAndEventParams = mopSpec.getParameters().sortParam(unionOfEnableAndEventParams);

				ret += new DefineToFromMonitor(paramPair.getParam2(), unionOfEnableAndEventParams, origWrapper, event, this);

				ret += "}\n";

				ret += "}\n";
			}
		}

		/*
		 * Assume at this point
		 * 
		 * mainWrapper can be null
		 * 
		 * mainSet is not null unless it is a full parameter event
		 * 
		 * mainMap is not null unless it has more than one parameter
		 * 
		 * use methods from indexing trees to handle these cases properly
		 */

		// main, line 3
		ret += "if (" + mainWrapper + " == null) {\n";
		ret += monitorClass.newWrapper(mainWrapper, event.getMOPParametersOnSpec());

		for (MOPParameter p : event.getMOPParametersOnSpec()) {
			ret += "if (" + tempRefs.get(p.getName()) + " == null){\n";
			ret += mainWrapper + "." + mopRefs.get(p.getName()) + " = " + tempRefs.get(p.getName()) + " = new javamoprt.MOPWeakReference(" + p.getName() + ");\n";
			ret += "} else {\n";
			ret += mainWrapper + "." + mopRefs.get(p.getName()) + " = " + tempRefs.get(p.getName()) + ";\n";
			ret += "}\n";
		}
		ret += indexingTree.addExactWrapper(mainWrapper, mainMap, mainSet, mopRefs);
		ret += "}\n";
		ret += "\n";
		/* at this point, mainWrapper is defined */
		if (event.isStartEvent()) {
			ret += "if (" + monitorClass.getSubMonitor(mainWrapper) + " == null) {\n";
			ret += new DefineNewMonitorInWrapper(mainWrapper, event, this);
			ret += "}\n";
			ret += "\n";
		}

		// main, line 4-5
		ret += "// line 4-5 of Main in Algorithm D\n";
		ret += monitorClass.incDisable(mainWrapper, timestamp);

		ret += "}\n";

		// main, line 7
		ret += "// line 7 of Main in Algorithm D\n";
		if (indexingTree.containsSet()) {
			ret += monitorSet.Monitoring(mainSet, event, thisJoinPoint);
		} else {
			ret += "if (" + mainWrapper + " != null && " + monitorClass.getSubMonitor(mainWrapper) + " != null" + ") {\n";
			ret += monitorClass.Monitoring(mainWrapper, event, thisJoinPoint);
			ret += "}\n";
		}

		if (mopSpec.isSync())
			ret += "}\n";

		if (aroundAdviceReturn != null)
			ret += aroundAdviceReturn;

		return ret;
	}
}
