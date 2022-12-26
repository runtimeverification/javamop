package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree;

/*
 * import com.runtimeverification.rvmonitor.util.RVMException; import
 * com.runtimeverification.rvmonitor.java.rvj.output.EnableSet; import
 * com.runtimeverification
 * .rvmonitor.java.rvj.output.combinedoutputcode.indexingtree
 * .centralized.CentralizedIndexingTree; import
 * com.runtimeverification.rvmonitor
 * .java.rvj.output.combinedoutputcode.indexingtree
 * .decentralized.DecentralizedIndexingTree; import
 * com.runtimeverification.rvmonitor
 * .java.rvj.output.combinedoutputcode.indexingtree.reftree.RefTree; import
 * com.runtimeverification.rvmonitor.java.rvj.output.monitor.SuffixMonitor;
 * import
 * com.runtimeverification.rvmonitor.java.rvj.output.monitorset.MonitorSet;
 * import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.*;
 *
 * import java.util.ArrayList; import java.util.HashMap;
 *
 * public class IndexingDecl { RVMonitorSpec rvmSpec; RVMParameters specParam;
 * HashMap<RVMParameters, IndexingTree> indexingTrees = new
 * HashMap<RVMParameters, IndexingTree>(); HashMap<RVMonitorParameterPair,
 * IndexingTree> indexingTreesForCopy = new HashMap<RVMonitorParameterPair,
 * IndexingTree>();
 *
 * HashMap<EventDefinition, ArrayList<RVMonitorParameterPair>>
 * mapEventToCopyParams = new HashMap<EventDefinition,
 * ArrayList<RVMonitorParameterPair>>();
 *
 * HashMap<String, RefTree> refTrees;
 *
 * MonitorSet monitorSet; SuffixMonitor monitor;
 *
 * public RVMParameters endObjectParameters = new RVMParameters();
 *
 * public IndexingDecl(RVMonitorSpec rvmSpec, MonitorSet monitorSet,
 * SuffixMonitor monitor, EnableSet enableSet, HashMap<String, RefTree>
 * refTrees) throws RVMException { this.rvmSpec = rvmSpec; this.specParam =
 * rvmSpec.getParameters(); this.refTrees = refTrees;
 *
 * RVMParameterSet indexingParameterSet = new RVMParameterSet();
 * RVMParameterPairSet indexingRestrictedParameterSet = new
 * RVMParameterPairSet();
 *
 * for (EventDefinition event : rvmSpec.getEvents()) { if (event.isEndObject()
 * && event.getRVMParameters().size() != 0)
 * endObjectParameters.addAll(event.getRVMParameters()); }
 *
 * for (EventDefinition event : rvmSpec.getEvents()) { RVMParameters param =
 * event.getRVMParametersOnSpec();
 *
 * indexingParameterSet.add(param);
 *
 * if (event.isEndObject()) { RVMParameter endParam =
 * param.getParam(event.getEndObjectVar()); RVMParameters endParams = new
 * RVMParameters(); if (endParam != null) { endParams.add(endParam); }
 * indexingParameterSet.add(endParams); } }
 *
 * if(rvmSpec.isGeneral()){ for (EventDefinition event : rvmSpec.getEvents()) {
 * ArrayList<RVMonitorParameterPair> pairs = new
 * ArrayList<RVMonitorParameterPair>();
 *
 * RVMParameters param = event.getRVMParametersOnSpec(); RVMParameterSet enable
 * = enableSet.getEnable(event.getId());
 *
 * for (RVMParameters enableEntity : enable) { if (enableEntity.size() == 0 &&
 * !rvmSpec.hasNoParamEvent()) { continue; }
 *
 * RVMParameters unionOfEnableEntityAndParam =
 * RVMParameters.unionSet(enableEntity, param); unionOfEnableEntityAndParam =
 * specParam.sortParam(unionOfEnableEntityAndParam);
 *
 * if (!enableEntity.contains(param)) { RVMParameters
 * intersectionOfEnableEntityAndParam =
 * RVMParameters.intersectionSet(enableEntity, param);
 * intersectionOfEnableEntityAndParam =
 * specParam.sortParam(intersectionOfEnableEntityAndParam);
 *
 * RVMonitorParameterPair paramPair = new
 * RVMonitorParameterPair(intersectionOfEnableEntityAndParam, enableEntity); if
 * (!param.contains(enableEntity)) {
 * indexingRestrictedParameterSet.add(paramPair);
 * indexingParameterSet.add(unionOfEnableEntityAndParam); } else { if
 * (!indexingParameterSet.contains(enableEntity)) {
 * indexingRestrictedParameterSet.add(paramPair); } } pairs.add(paramPair); } }
 *
 * mapEventToCopyParams.put(event, pairs); } }
 *
 * if (rvmSpec.isCentralized()) { for (RVMParameters param :
 * indexingParameterSet) { if (param.size() == 1 &&
 * this.endObjectParameters.getParam(param.get(0).getName()) != null) {
 * IndexingTree indexingTree =
 * DecentralizedIndexingTree.defineIndexingTree(rvmSpec.getName(), param, null,
 * specParam, monitorSet, monitor, refTrees, rvmSpec.isPerThread(),
 * rvmSpec.isGeneral()); indexingTrees.put(param, indexingTree); } else {
 * IndexingTree indexingTree =
 * CentralizedIndexingTree.defineIndexingTree(rvmSpec.getName(), param, null,
 * specParam, monitorSet, monitor, refTrees, rvmSpec.isPerThread(),
 * rvmSpec.isGeneral()); indexingTrees.put(param, indexingTree); } }
 *
 * if (rvmSpec.isGeneral()) { for (RVMonitorParameterPair paramPair :
 * indexingRestrictedParameterSet) { indexingTreesForCopy.put(paramPair,
 * CentralizedIndexingTree.defineIndexingTree(rvmSpec.getName(),
 * paramPair.getParam1(), paramPair.getParam2(), specParam, monitorSet, monitor,
 * refTrees, rvmSpec.isPerThread(), rvmSpec.isGeneral())); } }
 *
 * combineCentralIndexingTrees();
 *
 * combineRefTreesIntoIndexingTrees(); } else {
 *
 * // TODO: Decentralized RefTree which does not require any mapping. *
 *
 * for (RVMParameters param : indexingParameterSet) { IndexingTree indexingTree
 * = DecentralizedIndexingTree.defineIndexingTree(rvmSpec.getName(), param,
 * null, specParam, monitorSet, monitor, refTrees, rvmSpec.isPerThread(),
 * rvmSpec.isGeneral());
 *
 * indexingTrees.put(param, indexingTree); } if (rvmSpec.isGeneral()) { for
 * (RVMonitorParameterPair paramPair : indexingRestrictedParameterSet) {
 * IndexingTree indexingTree =
 * DecentralizedIndexingTree.defineIndexingTree(rvmSpec.getName(),
 * paramPair.getParam1(), paramPair.getParam2(), specParam, monitorSet, monitor,
 * refTrees, rvmSpec.isPerThread(), rvmSpec.isGeneral());
 *
 * indexingTreesForCopy.put(paramPair, indexingTree); } } }
 *
 * }
 *
 * public HashMap<RVMParameters, IndexingTree> getIndexingTrees() { return
 * indexingTrees; }
 *
 * public HashMap<RVMonitorParameterPair, IndexingTree>
 * getIndexingTreesForCopy() { return indexingTreesForCopy; }
 *
 * public ArrayList<RVMonitorParameterPair> getCopyParamForEvent(EventDefinition
 * e) { return mapEventToCopyParams.get(e); }
 *
 * protected void combineCentralIndexingTrees() { if (!rvmSpec.isCentralized())
 * return;
 *
 * for (IndexingTree indexingTree : indexingTrees.values()) { if
 * (indexingTree.parentTree == null) { RVMParameters sortedParam =
 * specParam.sortParam(indexingTree.queryParam);
 *
 * treeSearch: for (IndexingTree indexingTree2 : indexingTrees.values()) { if
 * (indexingTree == indexingTree2) continue;
 *
 * if (!indexingTree2.queryParam.contains(indexingTree.queryParam)) continue;
 *
 * RVMParameters sortedParam2 = specParam.sortParam(indexingTree2.queryParam);
 *
 * for (int i = 0; i < sortedParam.size(); i++) { if
 * (!sortedParam.get(i).equals(sortedParam2.get(i))) continue treeSearch; }
 *
 * // System.out.println(sortedParam + " -> " + sortedParam2);
 *
 * IndexingTree host = indexingTree2; while (host.parentTree != null) { host =
 * host.parentTree; }
 *
 * indexingTree.parentTree = host; if (indexingTree.parasiticRefTree != null) {
 * host.parasiticRefTree = indexingTree.parasiticRefTree;
 * host.parasiticRefTree.setHost(host); indexingTree.parasiticRefTree = null; }
 *
 * if (indexingTree.childTrees.size() > 0) {
 * host.childTrees.addAll(indexingTree.childTrees); indexingTree.childTrees =
 * new ArrayList<IndexingTree>(); }
 *
 * break; } } } }
 *
 * protected void combineRefTreesIntoIndexingTrees(){ if(rvmSpec.isPerThread())
 * return;
 *
 * if(rvmSpec.isGeneral()) return;
 *
 * for (RVMParameters param : indexingTrees.keySet()) { if (param.size() == 1 &&
 * this.endObjectParameters.getParam(param.get(0).getName()) != null) continue;
 *
 * IndexingTree indexingTree = indexingTrees.get(param);
 *
 * if (indexingTree.parentTree == null && param.size() == 1) { RVMParameter p =
 * param.get(0); RefTree refTree = refTrees.get(p.getType().toString());
 *
 * if (refTree.generalProperties.size() == 0 && refTree.hostIndexingTree ==
 * null) { refTree.setHost(indexingTree); indexingTree.parasiticRefTree =
 * refTree; } } } }
 *
 * public String toString() { String ret = "";
 *
 * for (IndexingTree indexingTree : indexingTrees.values()) { ret +=
 * indexingTree; }
 *
 * for (IndexingTree indexingTree : indexingTreesForCopy.values()) { ret +=
 * indexingTree; }
 *
 * return ret; }
 *
 * public String reset() { String ret = "";
 *
 * for (IndexingTree indexingTree : indexingTrees.values()) { ret +=
 * indexingTree.reset(); }
 *
 * for (IndexingTree indexingTree : indexingTreesForCopy.values()) { ret +=
 * indexingTree.reset(); }
 *
 * return ret; }
 *
 *
 *
 * }
 */