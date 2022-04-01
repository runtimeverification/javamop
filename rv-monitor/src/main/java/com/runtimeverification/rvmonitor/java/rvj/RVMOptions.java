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

    @Parameter(names={"-finegrainedlock"},description = "Use fine-grained lock for internal data structure.")
    public boolean finegrainedlock;

    @Parameter(names={"-weakrefinterning"},description = "Use WeakReference interning in indexing trees.")
    public boolean weakrefinterning;

    @Parameter(names={"-partitionedset"},description = "Experimental(?)")
    public boolean partitionedset;

    @Parameter(names={"-atomicmonitor"},description = "Experimental(?)")
    public boolean atomicmonitor;

    @Parameter(names={"-version"},description = "Display RV-Monitor version information.")
    public boolean version;

    public boolean isJarFile;

    public String jarFilePath;

    public boolean suppressActivator;

    public boolean generateVoidMethods;

    public boolean stripUnusedParameterInMonitor = true;

    public boolean internalBehaviorObserving;

    public boolean eliminatePresumablyRemnantCode;

}
