// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.commandline;

import com.beust.jcommander.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Command-line options available for controlling JavaMOP, and through it RV-Monitor.
 * TODO(OwolabiL): Add correct/better descriptions of options after consulting with others
 */
public class JavaMOPOptions {
    @Parameter(description = "Files")
    public List<String> files = new ArrayList<String>();

    @Parameter(names="-d",description = "Directory in which to store the output",
            converter = FileConverter.class)
    public File outputDir;

    @Parameter(names="-debug",description = "Print verbose error messages")
    public boolean debug = false;

    @Parameter(names="-javalib",description = "Generate a Java library instead of an AspectJ file")
    public boolean toJavaLib = false;

    @Parameter(names={"-s","-statistics"},description = "generate monitor with statistics")
    public boolean statistics = false;

    @Parameter(names={"-v","-verbose"}, description = "Enable verbose output")
    public boolean verbose = false;

    @Parameter(names={"-s2","statistics2"},description = "generate monitor with statistics")
    public boolean statistics2 = false;

    @Parameter(names={"-n","-aspectname"},description = "Use the gievn aspect name instead of " +
            "source code name")
    public String aspectname;

    @Parameter(names="-dacapo",description = "A Good description is needed here")
    public boolean dacapo = false;

    @Parameter(names="-dacapo2",description = "A Good description is needed here")
    public boolean dacapo2 = false;

    @Parameter(names="-silent",description = "A Good description is needed here")
    public boolean silent = false;

    @Parameter(names="-noadvicebody",description = "A Good description is needed here")
    public boolean noadvicebody = false;

    @Parameter(names="-translate2RV",description = "A Good description is needed here")
    public boolean translate2RV = true;

    @Parameter(names="-merge",description = "A Good description is needed here")
    public boolean merge = false;

    @Parameter(names="-inline",description = "A Good description is needed here")
    public boolean inline = false;

    @Parameter(names="-keepRVFiles",description = "A Good description is needed here")
    public boolean keepRVFiles = false;

    @Parameter(names="-agent",description = "Generate an agent from the given .mop files")
    public boolean generateAgent = false;

    @Parameter(names="-baseaspect",description = "Optionally provide a BaseAspect.aj file " +
            "for use in generating an agent", converter = FileConverter.class)
    public File baseAspect;

    @Parameter(names={"-h","-help"}, description = "Show this help message", help = true)
    private boolean help = false;

    @Parameter(names = "-usedb", description = "Use only the property database for building an Agent. " +
            "Setting this option without setting the '-agent' option will result in an Exception. For " +
            "a list config files used with this option, please see the " +
            "javamop/config/remote_server_addr.properties file")
    public boolean usedb = false;
}
