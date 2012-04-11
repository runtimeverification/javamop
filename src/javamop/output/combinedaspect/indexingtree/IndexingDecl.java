package javamop.output.combinedaspect.indexingtree;

import java.util.ArrayList;
import java.util.HashMap;

import javamop.MOPException;
import javamop.Main;
import javamop.output.EnableSet;
import javamop.output.combinedaspect.indexingtree.centralized.CentralizedIndexingTree;
import javamop.output.combinedaspect.indexingtree.decentralized.DecentralizedIndexingTree;
import javamop.output.combinedaspect.indexingtree.reftree.RefTree;
import javamop.output.monitor.SuffixMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameterPair;
import javamop.parser.ast.mopspec.MOPParameterPairSet;
import javamop.parser.ast.mopspec.MOPParameterSet;
import javamop.parser.ast.mopspec.MOPParameters;

public class IndexingDecl {
	JavaMOPSpec mopSpec;
	MOPParameters specParam;
	HashMap<MOPParameters, IndexingTree> indexingTrees = new HashMap<MOPParameters, IndexingTree>();
	HashMap<MOPParameterPair, IndexingTree> indexingTreesForCopy = new HashMap<MOPParameterPair, IndexingTree>();

	HashMap<EventDefinition, ArrayList<MOPParameterPair>> mapEventToCopyParams = new HashMap<EventDefinition, ArrayList<MOPParameterPair>>();

	HashMap<String, RefTree> refTrees;

	MonitorSet monitorSet;
	SuffixMonitor monitor;

	public MOPParameters endObjectParameters = new MOPParameters();

	public IndexingDecl(JavaMOPSpec mopSpec, MonitorSet monitorSet, SuffixMonitor monitor, EnableSet enableSet, HashMap<String, RefTree> refTrees) throws MOPException {
		this.mopSpec = mopSpec;
		this.specParam = mopSpec.getParameters();
		this.refTrees = refTrees;

		MOPParameterSet indexingParameterSet = new MOPParameterSet();
		MOPParameterPairSet indexingRestrictedParameterSet = new MOPParameterPairSet();

		for (EventDefinition event : mopSpec.getEvents()) {
			if (event.isEndObject() && event.getMOPParameters().size() != 0)
				endObjectParameters.addAll(event.getMOPParameters());
		}

		for (EventDefinition event : mopSpec.getEvents()) {
			MOPParameters param = event.getMOPParametersOnSpec();

			indexingParameterSet.add(param);

			if (event.isEndObject()) {
				MOPParameter endParam = param.getParam(event.getEndObjectVar());
				MOPParameters endParams = new MOPParameters();
				if (endParam != null) {
					endParams.add(endParam);
				}
				indexingParameterSet.add(endParams);
			}
		}

		if(mopSpec.isGeneral()){
			for (EventDefinition event : mopSpec.getEvents()) {
				ArrayList<MOPParameterPair> pairs = new ArrayList<MOPParameterPair>();
	
				MOPParameters param = event.getMOPParametersOnSpec();
				MOPParameterSet enable = enableSet.getEnable(event.getId());
	
				for (MOPParameters enableEntity : enable) {
					if (enableEntity.size() == 0 && !mopSpec.hasNoParamEvent()) {
						continue;
					}
	
					MOPParameters unionOfEnableEntityAndParam = MOPParameters.unionSet(enableEntity, param);
					unionOfEnableEntityAndParam = specParam.sortParam(unionOfEnableEntityAndParam);
	
					if (!enableEntity.contains(param)) {
						MOPParameters intersectionOfEnableEntityAndParam = MOPParameters.intersectionSet(enableEntity, param);
						intersectionOfEnableEntityAndParam = specParam.sortParam(intersectionOfEnableEntityAndParam);
	
						MOPParameterPair paramPair = new MOPParameterPair(intersectionOfEnableEntityAndParam, enableEntity);
						if (!param.contains(enableEntity)) {
							indexingRestrictedParameterSet.add(paramPair);
							indexingParameterSet.add(unionOfEnableEntityAndParam);
						} else {
							if (!indexingParameterSet.contains(enableEntity)) {
								indexingRestrictedParameterSet.add(paramPair);
							}
						}
						pairs.add(paramPair);
					}
				}
	
				mapEventToCopyParams.put(event, pairs);
			}
		}

		if (mopSpec.isCentralized()) {
			for (MOPParameters param : indexingParameterSet) {
				if (param.size() == 1 && this.endObjectParameters.getParam(param.get(0).getName()) != null) {
					IndexingTree indexingTree = DecentralizedIndexingTree.defineIndexingTree(mopSpec.getName(), param, null, specParam, monitorSet, monitor, refTrees,
							mopSpec.isPerThread(), mopSpec.isGeneral());
					indexingTrees.put(param, indexingTree);
				} else {
					IndexingTree indexingTree = CentralizedIndexingTree.defineIndexingTree(mopSpec.getName(), param, null, specParam, monitorSet, monitor, refTrees,
							mopSpec.isPerThread(), mopSpec.isGeneral());
					indexingTrees.put(param, indexingTree);
				}
			}
			
			if (mopSpec.isGeneral()) {
				for (MOPParameterPair paramPair : indexingRestrictedParameterSet) {
					indexingTreesForCopy.put(paramPair, CentralizedIndexingTree.defineIndexingTree(mopSpec.getName(), paramPair.getParam1(), paramPair.getParam2(), specParam,
							monitorSet, monitor, refTrees, mopSpec.isPerThread(), mopSpec.isGeneral()));
				}
			}

			combineCentralIndexingTrees();
			
			combineRefTreesIntoIndexingTrees();
		} else {

			/* TODO: Decentralized RefTree which does not require any mapping. */
			
			for (MOPParameters param : indexingParameterSet) {
				IndexingTree indexingTree = DecentralizedIndexingTree.defineIndexingTree(mopSpec.getName(), param, null, specParam, monitorSet, monitor, refTrees,
						mopSpec.isPerThread(), mopSpec.isGeneral());

				indexingTrees.put(param, indexingTree);
			}
			if (mopSpec.isGeneral()) {
				for (MOPParameterPair paramPair : indexingRestrictedParameterSet) {
					IndexingTree indexingTree = DecentralizedIndexingTree.defineIndexingTree(mopSpec.getName(), paramPair.getParam1(), paramPair.getParam2(), specParam,
							monitorSet, monitor, refTrees, mopSpec.isPerThread(), mopSpec.isGeneral());

					indexingTreesForCopy.put(paramPair, indexingTree);
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

	public ArrayList<MOPParameterPair> getCopyParamForEvent(EventDefinition e) {
		return mapEventToCopyParams.get(e);
	}

	protected void combineCentralIndexingTrees() {
		if (!mopSpec.isCentralized())
			return;

		for (IndexingTree indexingTree : indexingTrees.values()) {
			if (indexingTree.parentTree == null) {
				MOPParameters sortedParam = specParam.sortParam(indexingTree.queryParam);

				treeSearch: for (IndexingTree indexingTree2 : indexingTrees.values()) {
					if (indexingTree == indexingTree2)
						continue;

					if (!indexingTree2.queryParam.contains(indexingTree.queryParam))
						continue;

					MOPParameters sortedParam2 = specParam.sortParam(indexingTree2.queryParam);

					for (int i = 0; i < sortedParam.size(); i++) {
						if (!sortedParam.get(i).equals(sortedParam2.get(i)))
							continue treeSearch;
					}

					// System.out.println(sortedParam + " -> " + sortedParam2);

					IndexingTree host = indexingTree2;
					while (host.parentTree != null) {
						host = host.parentTree;
					}

					indexingTree.parentTree = host;
					if (indexingTree.parasiticRefTree != null) {
						host.parasiticRefTree = indexingTree.parasiticRefTree;
						host.parasiticRefTree.setHost(host);
						indexingTree.parasiticRefTree = null;
					}

					if (indexingTree.childTrees.size() > 0) {
						host.childTrees.addAll(indexingTree.childTrees);
						indexingTree.childTrees = new ArrayList<IndexingTree>();
					}

					break;
				}
			}
		}
	}
	
	protected void combineRefTreesIntoIndexingTrees(){
		if(mopSpec.isPerThread())
			return;
		
		if(mopSpec.isGeneral())
			return;
		
		for (MOPParameters param : indexingTrees.keySet()) {
			if (param.size() == 1 && this.endObjectParameters.getParam(param.get(0).getName()) != null)
				continue;
		
			IndexingTree indexingTree = indexingTrees.get(param);
			
			if (indexingTree.parentTree == null && param.size() == 1) {
				MOPParameter p = param.get(0);
				RefTree refTree = refTrees.get(p.getType().toString());
				
				if (refTree.generalProperties.size() == 0 && refTree.hostIndexingTree == null) {
					refTree.setHost(indexingTree);
					indexingTree.parasiticRefTree = refTree;
				}
			}
		}
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

	public String reset() {
		String ret = "";

		for (IndexingTree indexingTree : indexingTrees.values()) {
			ret += indexingTree.reset();
		}

		for (IndexingTree indexingTree : indexingTreesForCopy.values()) {
			ret += indexingTree.reset();
		}

		return ret;
	}

	
	
}
