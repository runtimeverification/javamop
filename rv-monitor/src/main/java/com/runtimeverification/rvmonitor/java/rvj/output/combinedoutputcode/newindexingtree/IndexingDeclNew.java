package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.java.rvj.output.EnableSet;
import com.runtimeverification.rvmonitor.java.rvj.output.NotImplementedException;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMemberField;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeFormatters;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeRVType;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.reftree.RefTree;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeImplementation.EventKind;
import com.runtimeverification.rvmonitor.java.rvj.output.monitor.SuffixMonitor;
import com.runtimeverification.rvmonitor.java.rvj.output.monitorset.MonitorSet;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.EventDefinition;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameterPairSet;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameterSet;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorParameterPair;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;
import com.runtimeverification.rvmonitor.util.RVMException;

public class IndexingDeclNew {
    private final RVMonitorSpec rvmSpec;
    private final RVMParameters specParam;
    private final TreeMap<RVMParameters, IndexingTreeInterface> indexingTrees = new TreeMap<RVMParameters, IndexingTreeInterface>();
    private final TreeMap<RVMonitorParameterPair, IndexingTreeInterface> indexingTreesForCopy = new TreeMap<RVMonitorParameterPair, IndexingTreeInterface>();

    private final TreeMap<EventDefinition, ArrayList<RVMonitorParameterPair>> mapEventToCopyParams = new TreeMap<EventDefinition, ArrayList<RVMonitorParameterPair>>();

    private final TreeMap<String, RefTree> refTrees;

    public final RVMParameters endObjectParameters = new RVMParameters();

    public IndexingDeclNew(RVMonitorSpec rvmSpec, MonitorSet monitorSet,
            SuffixMonitor monitor, EnableSet enableSet,
            TreeMap<String, RefTree> refTrees) throws RVMException {
        this.rvmSpec = rvmSpec;
        this.specParam = rvmSpec.getParameters();
        this.refTrees = refTrees;

        RVMParameterSet indexingParameterSet = new RVMParameterSet();
        RVMParameterPairSet indexingRestrictedParameterSet = new RVMParameterPairSet();

        for (EventDefinition event : rvmSpec.getEvents()) {
            if (event.isEndObject() && event.getRVMParameters().size() != 0)
                endObjectParameters.addAll(event.getRVMParameters());
        }

        for (EventDefinition event : rvmSpec.getEvents()) {
            RVMParameters param = event.getRVMParametersOnSpec();

            indexingParameterSet.add(param);

            if (event.isEndObject()) {
                RVMParameter endParam = param.getParam(event.getEndObjectVar());
                RVMParameters endParams = new RVMParameters();
                if (endParam != null) {
                    endParams.add(endParam);
                }
                indexingParameterSet.add(endParams);
            }
        }

        if (rvmSpec.isGeneral()) {
            for (EventDefinition event : rvmSpec.getEvents()) {
                ArrayList<RVMonitorParameterPair> pairs = new ArrayList<RVMonitorParameterPair>();

                RVMParameters param = event.getRVMParametersOnSpec();
                RVMParameterSet enable = enableSet.getEnable(event.getId());

                for (RVMParameters enableEntity : enable) {
                    if (enableEntity.size() == 0 && !rvmSpec.hasNoParamEvent()) {
                        continue;
                    }

                    RVMParameters unionOfEnableEntityAndParam = RVMParameters
                            .unionSet(enableEntity, param);
                    unionOfEnableEntityAndParam = specParam
                            .sortParam(unionOfEnableEntityAndParam);

                    if (!enableEntity.contains(param)) {
                        RVMParameters intersectionOfEnableEntityAndParam = RVMParameters
                                .intersectionSet(enableEntity, param);
                        intersectionOfEnableEntityAndParam = specParam
                                .sortParam(intersectionOfEnableEntityAndParam);

                        RVMonitorParameterPair paramPair = new RVMonitorParameterPair(
                                intersectionOfEnableEntityAndParam,
                                enableEntity);
                        if (!param.contains(enableEntity)) {
                            indexingRestrictedParameterSet.add(paramPair);
                            indexingParameterSet
                            .add(unionOfEnableEntityAndParam);
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

        if (rvmSpec.isCentralized()) {
            for (RVMParameters param : indexingParameterSet) {
                if (param.size() == 1
                        && this.endObjectParameters.getParam(param.get(0)
                                .getName()) != null) {
                    throw new NotImplementedException();
                    /*
                     * IndexingTree indexingTree =
                     * DecentralizedIndexingTree.defineIndexingTree
                     * (rvmSpec.getName(), param, null, specParam, monitorSet,
                     * monitor, refTrees, rvmSpec.isPerThread(),
                     * rvmSpec.isGeneral()); indexingTrees.put(param,
                     * indexingTree);
                     */
                } else {
                    IndexingTreeInterface indexingTree = new IndexingTreeInterface(
                            rvmSpec.getName(), specParam, param, null);
                    /*
                     * IndexingTree indexingTree =
                     * CentralizedIndexingTree.defineIndexingTree
                     * (rvmSpec.getName(), param, null, specParam, monitorSet,
                     * monitor, refTrees, rvmSpec.isPerThread(),
                     * rvmSpec.isGeneral());
                     */
                    indexingTrees.put(param, indexingTree);
                }
            }

            if (rvmSpec.isGeneral()) {
                for (RVMonitorParameterPair paramPair : indexingRestrictedParameterSet) {
                    /*
                     * indexingTreesForCopy.put(paramPair,
                     * CentralizedIndexingTree
                     * .defineIndexingTree(rvmSpec.getName(),
                     * paramPair.getParam1(), paramPair.getParam2(), specParam,
                     * monitorSet, monitor, refTrees, rvmSpec.isPerThread(),
                     * rvmSpec.isGeneral()));
                     */
                    IndexingTreeInterface indexingTree = new IndexingTreeInterface(
                            rvmSpec.getName(), specParam,
                            paramPair.getParam1(), paramPair.getParam2());
                    indexingTreesForCopy.put(paramPair, indexingTree);
                }
            }

            monitorSet.feedIndexingTreeInterface(this);

            for (RVMParameters param : indexingParameterSet) {
                IndexingTreeInterface itf = this.indexingTrees.get(param);
                EventKind evttype = this.calculateEventType(param);
                boolean needsTimeTracking = rvmSpec.isGeneral();
                itf.initializeImplementation(rvmSpec.getName(), monitorSet,
                        monitor, evttype, needsTimeTracking);
            }

            for (RVMonitorParameterPair paramPair : indexingRestrictedParameterSet) {
                IndexingTreeInterface itf = this.indexingTreesForCopy
                        .get(paramPair);
                EventKind evttype = EventKind.AlwaysCreate;
                boolean needsTimeTracking = rvmSpec.isGeneral();
                itf.initializeImplementation(rvmSpec.getName(), monitorSet,
                        monitor, evttype, needsTimeTracking);
            }

            combineCentralIndexingTrees();
            combineRefTreesIntoIndexingTrees();

            monitorSet.feedIndexingTreeImplementation(this);
        } else {
            /*
             * TODO: Decentralized RefTree which does not require any mapping.
             *
             * for (RVMParameters param : indexingParameterSet) { IndexingTree
             * indexingTree =
             * DecentralizedIndexingTree.defineIndexingTree(rvmSpec.getName(),
             * param, null, specParam, monitorSet, monitor, refTrees,
             * rvmSpec.isPerThread(), rvmSpec.isGeneral());
             *
             * indexingTrees.put(param, indexingTree); } if
             * (rvmSpec.isGeneral()) { for (RVMonitorParameterPair paramPair :
             * indexingRestrictedParameterSet) { IndexingTree indexingTree =
             * DecentralizedIndexingTree.defineIndexingTree(rvmSpec.getName(),
             * paramPair.getParam1(), paramPair.getParam2(), specParam,
             * monitorSet, monitor, refTrees, rvmSpec.isPerThread(),
             * rvmSpec.isGeneral());
             *
             * indexingTreesForCopy.put(paramPair, indexingTree); } }
             */
            throw new NotImplementedException();
        }
    }

    private EventKind calculateEventType(RVMParameters treeParams) {
        int numCreationEvent = 0;
        int numNonCreationEvent = 0;

        for (EventDefinition event : this.rvmSpec.getEvents()) {
            RVMParameters param = event.getRVMParametersOnSpec();
            if (param.equals(treeParams)) {
                if (event.isStartEvent())
                    numCreationEvent++;
                else
                    numNonCreationEvent++;
            }
        }

        if (numCreationEvent > 0) {
            if (numNonCreationEvent == 0)
                return EventKind.AlwaysCreate;
            else
                return EventKind.MayCreate;
        }
        return EventKind.NeverCreate;
    }

    public TreeMap<RVMParameters, IndexingTreeInterface> getIndexingTrees() {
        return indexingTrees;
    }

    public TreeMap<RVMonitorParameterPair, IndexingTreeInterface> getIndexingTreesForCopy() {
        return indexingTreesForCopy;
    }

    public ArrayList<RVMonitorParameterPair> getCopyParamForEvent(
            EventDefinition e) {
        return mapEventToCopyParams.get(e);
    }

    protected void combineCentralIndexingTrees() {
        if (!rvmSpec.isCentralized())
            return;

        Set<IndexingTreeInterface> candidates = new HashSet<IndexingTreeInterface>();
        for (IndexingTreeInterface itf : this.indexingTrees.values()) {
            if (itf.isFullyFledgedTree())
                candidates.add(itf);
        }

        Map<IndexingTreeImplementation, List<IndexingTreeInterface>> impl2itfs = new HashMap<IndexingTreeImplementation, List<IndexingTreeInterface>>();
        for (IndexingTreeInterface itf : candidates) {
            List<IndexingTreeInterface> itfs = new ArrayList<IndexingTreeInterface>();
            itfs.add(itf);
            impl2itfs.put(itf.getImplementation(), itfs);
        }

        Set<IndexingTreeInterface> slaveitfs = new HashSet<IndexingTreeInterface>();
        for (IndexingTreeInterface itf1 : candidates) {
            if (slaveitfs.contains(itf1))
                continue;

            for (IndexingTreeInterface itf2 : candidates) {
                if (itf1 == itf2)
                    continue;

                if (itf2.subsumes(itf1)) {
                    IndexingTreeImplementation impl1 = itf1.getImplementation();
                    IndexingTreeImplementation impl2 = itf2.getImplementation();

                    IndexingTreeImplementation combined = IndexingTreeImplementation
                            .combine(impl2, impl1);

                    List<IndexingTreeInterface> itfs = new ArrayList<IndexingTreeInterface>();

                    for (IndexingTreeInterface i : impl2itfs.get(impl1)) {
                        i.switchImplementation(combined);
                        itfs.add(i);
                    }
                    impl2itfs.remove(impl1);

                    for (IndexingTreeInterface i : impl2itfs.get(impl2)) {
                        i.switchImplementation(combined);
                        itfs.add(i);
                    }
                    impl2itfs.remove(impl2);

                    impl2itfs.put(combined, itfs);
                    slaveitfs.add(itf1);
                    break;
                }
            }
        }
    }

    private void combineRefTreesIntoIndexingTrees() {
        if (rvmSpec.isPerThread())
            return;

        // It seems JavaMOP 3.0 only cares about most common cases.
        if (rvmSpec.isGeneral())
            return;

        // If weak-ref interning is disabled, GWRT does not exist.
        if (!Main.useWeakRefInterning)
            return;

        for (RVMParameters params : this.indexingTrees.keySet()) {
            if (params.size() == 1
                    && this.endObjectParameters.getParam(params.get(0)
                            .getName()) != null)
                continue;

            IndexingTreeInterface treeitf = indexingTrees.get(params);

            if (treeitf.isMasterTree() && params.size() == 1) {
                RVMParameter p = params.get(0);
                RefTree refTree = refTrees.get(p.getType().toString());

                if (refTree.generalProperties.size() == 0
                        && refTree.getHost() == null) {
                    refTree.setHost(treeitf);
                    treeitf.embedGWRT(refTree);
                }
            }
        }
    }

    @Override
    public String toString() {
        ICodeFormatter fmt = CodeFormatters.getDefault();

        for (IndexingTreeInterface indexingTree : indexingTrees.values())
            indexingTree.getCode(fmt);
        {
            Set<IndexingTreeImplementation> impls = this
                    .collectIndexingTreeImplementation(this.indexingTrees
                            .values());
            for (IndexingTreeImplementation impl : impls)
                impl.getCode(fmt);
        }

        for (IndexingTreeInterface indexingTree : indexingTreesForCopy.values())
            indexingTree.getCode(fmt);
        {
            Set<IndexingTreeImplementation> impls = this
                    .collectIndexingTreeImplementation(this.indexingTreesForCopy
                            .values());
            for (IndexingTreeImplementation impl : impls)
                impl.getCode(fmt);
        }

        return fmt.getCode();
    }

    public String reset() {
        return "";
    }

    private String getDescriptionCode(IndexingTreeImplementation tree) {
        String ret = "";

        CodeType type = tree.getCodeType();
        if (type instanceof CodeRVType.IndexingTree) {
            CodeMemberField field = tree.getField();
            ret += field.getName();
            ret += ".";
            ret += "setObservableObjectDescription(\"";
            ret += tree.getPrettyName();
            ret += "\");\n";
        }
        return ret;
    }

    public Set<IndexingTreeImplementation> collectIndexingTreeImplementation(
            Collection<IndexingTreeInterface> itfs) {
        Set<IndexingTreeImplementation> set = new HashSet<IndexingTreeImplementation>();

        for (IndexingTreeInterface itf : itfs)
            set.add(itf.getImplementation());

        return set;
    }

    public String getObservableObjectDescriptionSetCode() {
        String ret = "";

        {
            Set<IndexingTreeImplementation> impls = this
                    .collectIndexingTreeImplementation(this.indexingTrees
                            .values());
            for (IndexingTreeImplementation impl : impls)
                ret += this.getDescriptionCode(impl);
        }

        {
            Set<IndexingTreeImplementation> impls = this
                    .collectIndexingTreeImplementation(this.indexingTreesForCopy
                            .values());
            for (IndexingTreeImplementation impl : impls)
                ret += this.getDescriptionCode(impl);
        }

        return ret;
    }

    public String getCleanUpCode(String accvar) {
        String ret = "";

        {
            Set<IndexingTreeImplementation> impls = this
                    .collectIndexingTreeImplementation(this.indexingTrees
                            .values());
            for (IndexingTreeImplementation impl : impls)
                ret += this.getCleanUpCode(impl, accvar);
        }

        {
            Set<IndexingTreeImplementation> impls = this
                    .collectIndexingTreeImplementation(this.indexingTreesForCopy
                            .values());
            for (IndexingTreeImplementation impl : impls)
                ret += this.getCleanUpCode(impl, accvar);
        }

        return ret;
    }

    private String getCleanUpCode(IndexingTreeImplementation tree, String accvar) {
        String ret = "";

        CodeType type = tree.getCodeType();
        if (type instanceof CodeRVType.IndexingTree) {
            CodeMemberField field = tree.getField();
            ret += accvar;
            ret += " += ";
            ret += field.getName();
            ret += ".";
            ret += "cleanUpUnnecessaryMappings();\n";
        }
        return ret;
    }
}
