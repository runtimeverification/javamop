// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Command-line options available for controlling JavaMOPAgent
 */
public class JavaMOPAgentOptions {

    @Parameter(description = "Files")
    public List<String> files = new ArrayList<String>();

    @Parameter(names={"-n","-agentname"},description = "Use the given agent name instead of " +
            "agent.jar.")
    public String agentName;

    @Parameter(names="-excludeJars",description = "By default, rv-monitor-rt.jar and aspectweaver.jar will be" +
            " included in the generated agent. This option enables users to exclude those jars from the generated" +
            " agent, so users can resolve the dependency themselves when running the agent.")
    public boolean excludeJars = false;

    @Parameter(names="-d",description = "Directory in which to store the output.",
            converter = FileConverter.class)
    public File outputDir;

    @Parameter(names={"-emop"}, description = "Aspects dir for emop.", converter = FileConverter.class)
    public File aspectDir;

    @Parameter(names={"-m", "-makeVerboseAgent"},description = "When set to \"true\", the generated agent will run in" +
            " verbose mode, i.e., AspectJ will print more information on Load-Time Weaving via the -verbose and " +
            "-showWeaveInfo options")
    public boolean makeVerboseAgent;

    @Parameter(names={"-h","-help"}, description = "Show this help message.", help = true)
    private boolean help = false;

    @Parameter(names={"-v","-verbose"}, description = "Enable verbose output.")
    public boolean verbose = false;

    public static class FileConverter implements IStringConverter<File> {
        @Override
        public File convert(String value) {
            return new File(value);
        }
    }
}
