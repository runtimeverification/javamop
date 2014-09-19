// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.commandline;

import com.beust.jcommander.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Command-line options available for controlling JavaMOP, and through it RV-Monitor.
 */
public class JavaMOPOptions {
    @Parameter(description = "Files")
    public List<String> files = new ArrayList<String>();

    @Parameter(names="-d",description = "Directory in which to store the output.",
            converter = FileConverter.class)
    public File outputDir;

    @Parameter(names="-debug",description = "Print verbose error messages.")
    public boolean debug = false;

    @Parameter(names="-javalib",description = "Generate a Java library instead of an AspectJ file.")
    public boolean toJavaLib = false;

    @Parameter(names={"-s","-statistics"},description = "This is for counting events and/or " +
            "monitors.")
    public boolean statistics = false;

    @Parameter(names={"-v","-verbose"}, description = "Enable verbose output.")
    public boolean verbose = false;

    @Parameter(names={"-s2","statistics2"},description = "This is for counting events and/or " +
            "monitors.")
    public boolean statistics2 = false;

    @Parameter(names={"-n","-aspectname"},description = "Use the given aspect name instead of " +
            "source code name.")
    public String aspectname;

    @Parameter(names="-dacapo",description = "is for adding pointcuts, such as," +
            " !within(dacapo.test.*) -- automatically. Otherwise, user will have to add these " +
            "pointcuts manually for each specification.")
    public boolean dacapo = false;

    @Parameter(names="-noadvicebody",description = "Enabling this causes JavaMOP not to put " +
            "the monitoring-related routine inside the corresponding advice body. This can be " +
            "used count the number of fired events for each event.")
    public boolean noadvicebody = false;

    @Parameter(names="-translate2RV",description = "When this is enabled, JavaMOP generates a " +
            ".rvm file from an .mop file, and invokes RV-Monitor to process the .rvm file.")
    public boolean translate2RV = true;

    @Parameter(names="-merge",description = "By default, one .aj file is generated for each " +
            "JavaMOP specification. This option enables JavaMOP to generate a combined .aj file" +
            " for monitoring multiple specifications simultaneously. This should be used with" +
            " -n, so that the merged monitor has a name specified by the user.")
    public boolean merge = false;

    @Parameter(names="-inline",description = "If -inline is disabled, the monitoring-related " +
            "routine (which is printed in adviceBody()) is put inside of the corresponding " +
            "advice. If it's enabled, the routine is promoted to a Java method and this method " +
            "is invoked by the corresponding advice.")
    public boolean inline = false;

    @Parameter(names="-keepRVFiles",description = "By default, JavaMOP deletes the generated" +
            " .java file after combining the generated libraries with the associated pointcuts. " +
            "This is because the user should not have to care about this intermediate file. " +
            "If this option is enabled, however, the file is preserved.")
    public boolean keepRVFiles = false;

    @Parameter(names="-agent",description = "Generate an agent from the given .mop files.")
    public boolean generateAgent = false;

    @Parameter(names="-baseaspect",description = "Optionally provide a BaseAspect.aj file " +
            "for use in generating an agent.", converter = FileConverter.class)
    public File baseAspect;

    @Parameter(names={"-h","-help"}, description = "Show this help message.", help = true)
    private boolean help = false;

    @Parameter(names = "-usedb", description = "Use only the property database for building " +
            "an Agent. Setting this option without setting the '-agent' option will result in " +
            "an Exception. For a list config files used with this option, please see the " +
            "javamop/config/remote_server_addr.properties file.")
    public boolean usedb = false;
}
