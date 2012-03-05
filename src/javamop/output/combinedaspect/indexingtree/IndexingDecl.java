package javamop.output.combinedaspect.indexingtree;

import java.util.HashMap;

import javamop.MOPException;
import javamop.Main;
import javamop.output.EnableSet;
import javamop.output.monitor.WrapperMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameterPair;
import javamop.parser.ast.mopspec.MOPParameterPairSet;
import javamop.parser.ast.mopspec.MOPParameterSet;
import javamop.parser.ast.mopspec.MOPParameters;

public class IndexingDecl {
	HashMap<MOPParameters, IndexingTree> indexingTrees = new HashMap<MOPParameters, IndexingTree>();
	HashMap<MOPParameterPair, IndexingTree> indexingTreesForCopy = new HashMap<MOPParameterPair, IndexingTree>();

	MonitorSet monitorSet;
	WrapperMonitor monitor;

	public MOPParameters endObjectParameters = new MOPParameters();

	public IndexingDecl(JavaMOPSpec mopSpec, MonitorSet monitorSet, WrapperMonitor monitor, EnableSet enableSet) throws MOPException {
		MOPParameterSet indexingParameterSet = new MOPParameterSet();
		MOPParameterPairSet indexingRestrictedParameterSet = new MOPParameterPairSet();

		for (EventDefinition event : mopSpec.getEvents()) {
			if (event.isEndObject() && event.getMOPParameters().size() != 0)
				endObjectParameters.addAll(event.getMOPParameters());
		}

		for (EventDefinition event : mopSpec.getEvents()) {
			indexingParameterSet.add(event.getMOPParametersOnSpec());

			if (event.isEndObject()) {
				MOPParameter endParam = event.getMOPParametersOnSpec().getParam(event.getEndObjectVar());
				MOPParameters endParams = new MOPParameters();
				if (endParam != null) {
					endParams.add(endParam);
				}
				indexingParameterSet.add(endParams);
			}

			MOPParameters param = event.getMOPParametersOnSpec();
			MOPParameterSet enable = enableSet.getEnable(event.getId());

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

				if (!enableEntity.contains(param)) {
					MOPParameters intersectionOfEnableEntityAndParam = MOPParameters.intersectionSet(enableEntity, param);
					intersectionOfEnableEntityAndParam = mopSpec.getParameters().sortParam(intersectionOfEnableEntityAndParam);
					if (!param.contains(enableEntity)) {
						indexingRestrictedParameterSet.add(intersectionOfEnableEntityAndParam, enableEntity);
						indexingParameterSet.add(unionOfEnableEntityAndParam);
					} else {
						indexingParameterSet.add(enableEntity);
					}
				}
			}
		}

		if (mopSpec.isCentralized()) {
			for (MOPParameters param : indexingParameterSet) {
				if (param.size() == 1 && this.endObjectParameters.getParam(param.get(0).getName()) != null) {
					indexingTrees.put(param, new DecentralizedIndexingTree(mopSpec.getName(), param, null, mopSpec.getParameters(), monitorSet, monitor, mopSpec.isPerThread()));
				} else {
					if (Main.scalable)
						indexingTrees.put(param,
								new ScalableCentralizedIndexingTree(mopSpec.getName(), param, null, mopSpec.getParameters(), monitorSet, monitor, mopSpec.isPerThread()));
					else
						indexingTrees.put(param, new CentralizedIndexingTree(mopSpec.getName(), param, null, mopSpec.getParameters(), monitorSet, monitor, mopSpec.isPerThread()));
				}
			}
			if (mopSpec.isGeneral()) {
				for (MOPParameterPair paramPair : indexingRestrictedParameterSet) {
					if (Main.scalable)
						indexingTreesForCopy.put(paramPair, new ScalableCentralizedIndexingTree(mopSpec.getName(), paramPair.getParam1(), paramPair.getParam2(), mopSpec.getParameters(),
								monitorSet, monitor, mopSpec.isPerThread()));
					else
						indexingTreesForCopy.put(paramPair, new CentralizedIndexingTree(mopSpec.getName(), paramPair.getParam1(), paramPair.getParam2(), mopSpec.getParameters(),
								monitorSet, monitor, mopSpec.isPerThread()));

				}
			}
		} else {
			for (MOPParameters param : indexingParameterSet) {
				indexingTrees.put(param, new DecentralizedIndexingTree(mopSpec.getName(), param, null, mopSpec.getParameters(), monitorSet, monitor, mopSpec.isPerThread()));
			}
			if (mopSpec.isGeneral()) {
				for (MOPParameterPair paramPair : indexingRestrictedParameterSet) {
					indexingTreesForCopy.put(paramPair, new DecentralizedIndexingTree(mopSpec.getName(), paramPair.getParam1(), paramPair.getParam2(), mopSpec.getParameters(),
							monitorSet, monitor, mopSpec.isPerThread()));
				}
			}
		}

	}

	public HashMap<MOPParameters, IndexingTree> getIndexingTrees() {
		return indexingTrees;
	}

	public HashMap<MOPParameterPair, IndexingTree> getIndexingTreesForCopy() {
		return indexingTreesForCopy;
	}

	public String toString() {
		String ret = "";

		for (IndexingTree indexingTree : indexingTrees.values()) {
			ret += indexingTree;
		}

		for (IndexingTree indexingTree : indexingTreesForCopy.values()) {
			ret += indexingTree;
		}

		return ret;
	}

}
