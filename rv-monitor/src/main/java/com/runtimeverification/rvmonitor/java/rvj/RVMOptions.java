package com.runtimeverification.rvmonitor.java.rvj;

import com.beust.jcommander.Parameter;
import javamop.JavaMOPOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RVMOptions {
    // Any stand-alone command line argument which is not required for a '-x' option
    // Examples: in `rv-monitor a.rvm`, files = [a.rvm]
    //           in ` rv-monitor -n test a.rvm b.rvm, files = [a.rvm, b.rvm]
    @Parameter(description = "Files")
    public List<String> files = new ArrayList<>();

    @Parameter(names={"-h","-help"}, description = "Show this help message.", help = true)
    public boolean help = false;

    @Parameter(names="-d",description = "Directory in which to store the output.",
            converter = JavaMOPOptions.FileConverter.class)
    public File outputDir;

    @Parameter(names={"-v","-verbose"}, description = "Enable verbose output.")
    public boolean verbose = false;

    @Parameter(names="-debug",description = "Print verbose error messages.")
    public boolean debug = false;

    @Parameter(names="-disableEnableSet",description = "Disable the enable set optimization.")
    public boolean disableEnableSet = false;

    @Parameter(names={"-s","-statistics"}, description = "Count events and monitors.")
    public boolean statistics = false;

    @Parameter(names={"-n","-name"},description = "Use the given name instead of source file name.")
    public String name;

    @Parameter(names="-noadvicebody",description = "Enabling this causes RV-Monitor not to put " +
            "the monitoring-related routine inside the corresponding advice body. This can be " +
            "used count the number of times that each event fired.")
    public boolean noadvicebody = false;

    @Parameter(names="-merge",description = "This option enables RV-Monitor to use the name of the combined .aj file" +
            " that JavaMOP produced")
    public boolean merge = false;

    @Parameter(names="-inline",description = "If -inline is disabled, the monitoring-related " +
            "routine (which is printed in adviceBody()) is put inside of the corresponding " +
            "advice. If it's enabled, the routine is promoted to a Java method and this method " +
            "is invoked by the corresponding advice.")
    public boolean inline = false;

    @Parameter(names={"-finegrainedlock"},description = "Use fine-grained lock for internal data structure. " +
             "See paper details - RV-Monitor: Efficient Parametric Runtime Verification with Simultaneous Properties")
    public boolean finegrainedlock;

    @Parameter(names={"-weakrefinterning"},description = "Use WeakReference interning in indexing trees. " +
             "See paper details - RV-Monitor: Efficient Parametric Runtime Verification with Simultaneous Properties")
    public boolean weakrefinterning;

    @Parameter(names={"-partitionedset"},description = "Use monitor sets that are partitioned into a list of arrays of " +
             "monitors. This optimization is based on the fact that when an event occurs, only certain monitors are " +
             "affected and all the other monitors do not make any transition. Even when set, the optimization will only " +
             "work if *all* these conditions are met by a spec: (1) each starting event carries all parameters; (2) " +
             "there are exactly two parameters; (3) non-creation events carry only one parameter; (4) there is at least " +
             "one event that carries each parameter only.")
    public boolean partitionedset;

    @Parameter(names={"-atomicmonitor"},description = "Experimental(?)")
    public boolean atomicmonitor;

    @Parameter(names={"-version"},description = "Display RV-Monitor version information.")
    public boolean version;

    @Parameter(names={"-internalBehaviorObserving"},description = "Track some internals of the monitoring process" +
            " such as traces, or monitoring steps.")
    public boolean internalBehaviorObserving;

    @Parameter(names={"-collectAllTraces"},description = "Collect all the steps that are taken during runtime " +
            "monitoring, including indexing lookups, monitor creations, monitor cloning, etc.")
    public boolean collectAllTraces;

    @Parameter(names={"-collectAllMonitorSteps"},description = "Collect all traces that all monitors observe." +
            "If set, -internalBehaviorObserving is also set.")
    public boolean collectAllMonitorSteps;

    @Parameter(names={"-collectUniqueTraces"},description = "Collect traces that are unique among all monitors. " +
            "Currently, this option merely post-processes all traces, so it will be more expensive than " +
            "-collectAllTraces alone. If set, -internalBehaviorObserving is also set.")
    public boolean collectUniqueTraces;

    @Parameter(names={"-computeUniqueTraceStats"},description = "Compute stats about the unique traces, such as the" +
            "frequencies and the lengths of the traces. If set, -collectUniqueTraces and -internalBehaviorObserving" +
            " are also set.")
    public boolean computeUniqueTraceStats;

    @Parameter(names={"-trackEventLocations"},description = "Collect code locations where events are triggered. " +
            "Use only if traces are also being collected.")
    public boolean trackEventLocations;

    @Parameter(names={"-storeEventLocationMapFile"},description = "Store a file that maps symbolic events to concrete ones" +
            "when traces are being collected and event locations are being tracked. If set, -trackEventLocations " +
            "is also set.")
    public boolean storeEventLocationMapFile;

    @Parameter(names="-artifactsDir",
            description = "Directory in which to store a .traces directory that, in turn, contains trace-related files",
            converter = JavaMOPOptions.FileConverter.class)
    public File artifactsDir = new File(System.getProperty("java.io.tmpdir"));

    public boolean isJarFile;

    public String jarFilePath;

    public boolean suppressActivator;

    public boolean generateVoidMethods;

    public boolean stripUnusedParameterInMonitor = true;

    public boolean eliminatePresumablyRemnantCode;

}
