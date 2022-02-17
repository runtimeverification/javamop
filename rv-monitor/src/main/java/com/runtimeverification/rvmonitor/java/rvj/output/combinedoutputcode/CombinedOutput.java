package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.java.rvj.output.EnableSet;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeFormatters;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event.EventManager;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.IndexingTreeManager;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingDeclNew;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeInterface;
import com.runtimeverification.rvmonitor.java.rvj.output.monitor.BaseMonitor;
import com.runtimeverification.rvmonitor.java.rvj.output.monitor.Monitor;
import com.runtimeverification.rvmonitor.java.rvj.output.monitor.SuffixMonitor;
import com.runtimeverification.rvmonitor.java.rvj.output.monitorset.MonitorSet;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.RVMSpecFile;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.EventDefinition;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameterSet;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;
import com.runtimeverification.rvmonitor.util.RVMException;

public class CombinedOutput {
    private final String name;
    public final TreeMap<RVMonitorSpec, MonitorSet> monitorSets;
    public final TreeMap<RVMonitorSpec, SuffixMonitor> monitors;
    public final TreeMap<RVMonitorSpec, EnableSet> enableSets;
    public HashMap<RVMonitorSpec, HashSet<RVMParameter>> setOfParametersForDisable;

    final RVMVariable mapManager;
    private final RuntimeServiceManager runtimeServiceManager;

    final List<RVMonitorSpec> specs;
    public final RVMonitorStatManager statManager;
    public final LockManager lockManager;
    public final TimestampManager timestampManager;
    public final ActivatorManager activatorsManager;
    public final IndexingTreeManager indexingTreeManager;
    public final EventManager eventManager;

    private boolean has__ACTIVITY = false;
    private boolean isCodeGenerated = false;

    public InternalBehaviorObservableCodeGenerator getInternalBehaviorObservableGenerator() {
        return this.runtimeServiceManager.getObserver();
    }

    public CombinedOutput(String name, RVMSpecFile rvmSpecFile,
            TreeMap<RVMonitorSpec, MonitorSet> monitorSets,
            TreeMap<RVMonitorSpec, SuffixMonitor> monitors,
            TreeMap<RVMonitorSpec, EnableSet> enableSets) throws RVMException {
        this.name = name + "RuntimeMonitor";
        this.monitorSets = monitorSets;
        this.monitors = monitors;
        this.enableSets = enableSets;
        this.runtimeServiceManager = new RuntimeServiceManager();

        this.specs = rvmSpecFile.getSpecs();
        for (RVMonitorSpec spec : specs) {
            if (spec.has__ACTIVITY())
                has__ACTIVITY = true;
        }
        this.statManager = new RVMonitorStatManager(name, this.specs);
        this.lockManager = new LockManager(name, this.specs);
        this.timestampManager = new TimestampManager(name, this.specs);
        this.activatorsManager = new ActivatorManager(name, this.specs);
        this.indexingTreeManager = new IndexingTreeManager(name, this.specs,
                this.monitorSets, this.monitors, this.enableSets);

        collectDisableParameters(rvmSpecFile.getSpecs());

        this.eventManager = new EventManager(name, this.specs, this);

        this.mapManager = new RVMVariable(name + "MapManager");
    }

    public void collectDisableParameters(List<RVMonitorSpec> specs) {
        this.setOfParametersForDisable = new HashMap<RVMonitorSpec, HashSet<RVMParameter>>();
        for (RVMonitorSpec spec : specs) {
            HashSet<RVMParameter> parametersForDisable = new HashSet<RVMParameter>();

            for (EventDefinition event : spec.getEvents()) {
                RVMParameters eventParams = event.getRVMParametersOnSpec();
                RVMParameterSet enable = enableSets.get(spec).getEnable(
                        event.getId());

                for (RVMParameters enableEntity : enable) {
                    if (enableEntity.size() == 0 && !spec.hasNoParamEvent())
                        continue;
                    if (enableEntity.contains(eventParams))
                        continue;

                    RVMParameters unionOfEnableEntityAndParam = RVMParameters
                            .unionSet(enableEntity, eventParams);

                    for (RVMParameter p : unionOfEnableEntityAndParam) {
                        if (!enableEntity.contains(p)) {
                            parametersForDisable.add(p);
                        }
                    }
                }
            }

            this.setOfParametersForDisable.put(spec, parametersForDisable);
        }
    }

    public String getName() {
        return name;
    }

    public String initCache() {
        String ret = "";

        for (RVMonitorSpec spec : specs) {
            IndexingDeclNew decl = indexingTreeManager.getIndexingDecl(spec);

            for (IndexingTreeInterface tree : decl.getIndexingTrees().values()) {
                if (tree.getCache() != null) {
                    // The following is no longer needed.
                    // ret += tree.getCache().init();
                }
            }
        }

        return ret;
    }

    public String categoryVarsDecl() {
        boolean skipEvent = false;
        Set<RVMVariable> categoryVars = new HashSet<RVMVariable>();
        for (RVMonitorSpec rvmSpec : this.specs) {
            if (rvmSpec.has__SKIP()) {
                skipEvent = true;
            }
            MonitorSet monitorSet = monitorSets.get(rvmSpec);
            Monitor monitorClass = monitors.get(rvmSpec);
            categoryVars.addAll(monitorSet.getCategoryVars());
            categoryVars.addAll(monitorClass.getCategoryVars());
        }
        String ret = "";
        if (!Main.eliminatePresumablyRemnantCode) {
            for (RVMVariable variable : categoryVars) {
                ret += "private static boolean "
                        + BaseMonitor.getNiceVariable(variable) + " = "
                        + "false;\n";
            }
        }
        if (skipEvent) {
            ret += "public static boolean " + BaseMonitor.skipEvent
                    + " = false;" + "\n";
        }
        return ret;
    }

    public void generateCode() {
        if (!this.isCodeGenerated) {
            this.eventManager.generateCode();
        }

        this.isCodeGenerated = true;
    }

    @Override
    public String toString() {
        this.generateCode();

        String ret = "";

        ret += this.statManager.statClass();

        ret += "public final class "
                + this.name
                + " implements com.runtimeverification.rvmonitor.java.rt.RVMObject {\n";

        ret += categoryVarsDecl();

        ret += "private static com.runtimeverification.rvmonitor.java.rt.map.RVMMapManager "
                + mapManager + ";\n";

        ret += this.statManager.fieldDecl2();

        // constructor
        ret += "static {\n";

        ret += this.eventManager.printConstructor();

        ret += mapManager
                + " = "
                + "new com.runtimeverification.rvmonitor.java.rt.map.RVMMapManager();\n";
        ret += mapManager + ".start();\n";

        ret += this.statManager.constructor();

        // ret += constructor();
        // ret += initCache();

        ret += "}\n";
        ret += "\n";

        // ret += this.statManager.fieldDecl();

        ret += this.lockManager.decl();

        ret += this.timestampManager.decl();

        ret += this.activatorsManager.decl();

        ret += this.indexingTreeManager.decl();

        {
            ICodeFormatter fmt = CodeFormatters.getDefault();
            this.runtimeServiceManager.getCode(fmt);
            ret += fmt.getCode();
        }

        ret += this.eventManager.advices();

        if (this.has__ACTIVITY) {
            ret += "public static void onCreateActivity(Activity a) {\n";
            for (Monitor m : monitors.values()) {
                if (m.has__ACTIVITY()) {
                    ret += m.getOutermostName() + "." + m.getActivityName()
                            + " = a;\n";
                }
            }
            ret += "}\n";
            ret += "\n";
        }

        ret += "}\n";

        return ret;
    }
}