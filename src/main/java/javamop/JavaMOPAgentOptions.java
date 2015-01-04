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

    public static class FileConverter implements IStringConverter<File> {
        @Override
        public File convert(String value) {
            return new File(value);
        }
    }
}
