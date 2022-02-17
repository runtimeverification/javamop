package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event.advice;

import java.util.TreeMap;

import com.runtimeverification.rvmonitor.java.rvj.output.RVMVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMonitorStatistics;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.CombinedOutput;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event.itf.EventMethodBody;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.reftree.RefTree;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingDeclNew;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeInterface;
import com.runtimeverification.rvmonitor.java.rvj.output.monitor.SuffixMonitor;
import com.runtimeverification.rvmonitor.java.rvj.output.monitorset.MonitorSet;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.EventDefinition;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;
import com.runtimeverification.rvmonitor.util.RVMException;

public abstract class AdviceBody {
    protected final RVMonitorSpec rvmSpec;
    protected final EventDefinition event;
    protected final MonitorSet monitorSet;
    protected final SuffixMonitor monitorClass;
    protected final RVMVariable monitorName;
    protected final TreeMap<RVMParameters, IndexingTreeInterface> indexingTrees;
    protected final IndexingDeclNew indexingDecl;
    protected final TreeMap<String, RefTree> refTrees;

    protected final RVMonitorStatistics stat;

    protected final boolean isGeneral;
    protected final RVMParameters eventParams;

    protected final boolean isFullParam;
    protected final CombinedOutput output;

    public RVMParameters getEventParameters() {
        return this.eventParams;
    }

    public AdviceBody(RVMonitorSpec rvmSpec, EventDefinition event,
            CombinedOutput combinedOutput) {
        this.rvmSpec = rvmSpec;
        this.output = combinedOutput;
        this.event = event;
        this.eventParams = event.getRVMParametersOnSpec();
        this.monitorSet = combinedOutput.monitorSets.get(rvmSpec);
        this.monitorClass = combinedOutput.monitors.get(rvmSpec);
        this.monitorClass.setOutputName(combinedOutput.getName());
        this.monitorName = monitorClass.getOutermostName();
        this.indexingDecl = combinedOutput.indexingTreeManager
                .getIndexingDecl(rvmSpec);
        this.indexingTrees = indexingDecl.getIndexingTrees();
        this.stat = combinedOutput.statManager.getStat(rvmSpec);
        this.refTrees = combinedOutput.indexingTreeManager.refTrees;
        this.isGeneral = rvmSpec.isGeneral();
        this.isFullParam = eventParams.equals(rvmSpec.getParameters());
    }

    @Override
    public abstract String toString();

    public abstract void generateCode();

    public static AdviceBody createAdviceBody(RVMonitorSpec rvmSpec,
            EventDefinition event, CombinedOutput combinedOutput)
                    throws RVMException {
        // return new GeneralAdviceBody(rvmSpec, event, combinedOutput);
        return new EventMethodBody(rvmSpec, event, combinedOutput);
    }
}
