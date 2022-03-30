package com.runtimeverification.rvmonitor.java.rvj;

import com.beust.jcommander.Parameter;
import javamop.JavaMOPOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RVMOptions {
    // Any stand-alone command line argument which is not required for a '-x' option
    // Examples: in `rv-monitor a.rvm`, files = [a.rvm]
    //           in ` rv-monitor -n test rvm a.mop, files = [rvm, a.mop]
    @Parameter(description = "Files")
    public List<String> files = new ArrayList<String>();

    @Parameter(names={"-h","-help"}, description = "Show this help message.", help = true)
    public boolean help = false;

    @Parameter(names="-d",description = "Directory in which to store the output.",
            converter = JavaMOPOptions.FileConverter.class)
    public File outputDir;

//    @Parameter(names="-",description = "Directory in which to store the output.",
//            converter = JavaMOPOptions.FileConverter.class)
//    public String outputName;

    @Parameter(names={"-v","-verbose"}, description = "Enable verbose output.")
    public boolean verbose = false;

    @Parameter(names="-debug",description = "Print verbose error messages.")
    public boolean debug = false;

    @Parameter(names="-noopt1",description = "TBD")
    public boolean noopt1 = false;

    @Parameter(names={"-s","-statistics"}, description = "Count events and monitors.")
    public boolean statistics = false;

    @Parameter(names={"-n","-name"},description = "Use the given name instead of source file name.")
    public String name;

    @Parameter(names={"-silent"},description = "TBD")
    public boolean silent;

    @Parameter(names={"-merge"},description = "TBD")
    public boolean merge;

    @Parameter(names={"-inline"},description = "TBD")
    public boolean inline;

    @Parameter(names={"-noadvicebody"},description = "TBD")
    public boolean noadvicebody;

    @Parameter(names={"-internalbehavior"},description = "TBD")
    public boolean internalbehavior;

    @Parameter(names={"-finegrainedlock"},description = "TBD")
    public boolean finegrainedlock;

    @Parameter(names={"-nofinegrainedlock"},description = "TBD")
    public boolean nofinegrainedlock;

    @Parameter(names={"-weakrefinterning"},description = "TBD")
    public boolean weakrefinterning;

    @Parameter(names={"-noweakrefinterning"},description = "TBD")
    public boolean noweakrefinterning;

    @Parameter(names={"-partitionedset"},description = "TBD")
    public boolean partitionedset;

    @Parameter(names={"-nopartitionedset"},description = "TBD")
    public boolean nopartitionedset;

    @Parameter(names={"-atomicmonitor"},description = "TBD")
    public boolean atomicmonitor;

    @Parameter(names={"-noatomicmonitor"},description = "TBD")
    public boolean noatomicmonitor;

    @Parameter(names={"-version"},description = "TBD")
    public boolean version;

    public boolean isJarFile;

    public String jarFilePath;

    public boolean suppressActivator;

    public boolean generateVoidMethods;

    public boolean stripUnusedParameterInMonitor = true;

    public boolean internalBehaviorObserving;

    public boolean eliminatePresumablyRemnantCode;

}
