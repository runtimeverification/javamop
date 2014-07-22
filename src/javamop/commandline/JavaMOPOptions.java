package javamop.commandline;

import com.beust.jcommander.Parameter;

import java.io.File;

public class JavaMOPOptions {
    @Parameter(names="-d",description = "Directory in which to store the output", converter = FileConverter.class)
    private File outputDir;

    @Parameter(names="-debug",description = "Print verbose error messages")
    public boolean debug = false;

    @Parameter(names="-noopt1",description = "Disable set optimization")
    public boolean noopt1 = false;

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

    @Parameter(names="-showevents",description = "show every event/handler occurrence")
    public boolean showevents = false;

    @Parameter(names="-showhandlers",description = "show every handler occurrence")
    public boolean showhandlers = false;

    @Parameter(names="-noadvicebody",description = "A Good description is needed here")
    public boolean noadvicebody = false;

    @Parameter(names="-translate2RV",description = "A Good description is needed here", arity = 1)
    public boolean translate2RV = true;


    @Parameter(names="-merge",description = "A Good description is needed here")
    public boolean merge = false;

    @Parameter(names="-inline",description = "A Good description is needed here")
    public boolean inline = false;


    @Parameter(names="-scalable",description = "A Good description is needed here")
    public boolean scalable = false;

    @Parameter(names="-keepRVFiles",description = "A Good description is needed here")
    public boolean keepRVFiles = false;

    @Parameter(names="-generateAgent",description = "Generate an agent from the given .mop files")
    public boolean generateAgent = false;

    @Parameter(names="-baseAspect",description = "Optionally provide a BaseAspect.aj file for use " +
            "in generating an agent", converter = FileConverter.class)
    public String baseAspect;
}
