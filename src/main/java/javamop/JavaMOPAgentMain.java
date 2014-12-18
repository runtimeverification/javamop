// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import javamop.agent.SeparateAgentGenerator;
import javamop.util.Tool;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Main class for generating agent for JavaMOP
 */
public class JavaMOPAgentMain {

    public static JavaMOPAgentOptions options;

    public static boolean excludeJars = false;

    public static String agentName = null;

    public static File baseAspect = null;

    public static File agentAspect = null;

    public static File classDir = null;

    public static File outputDir = null;

    public static void main (String[] args) {
        options = new JavaMOPAgentOptions();
        JCommander jc;
        try {
            jc = new JCommander(options, args);
        } catch (ParameterException pe) {
            System.out.println(pe.getMessage());
            return;
        }
        jc.setProgramName("javamopagent");

        boolean createdTempOutput = false;
        try {
            if (options.outputDir == null) {
                options.outputDir =
                        Files.createTempDirectory(new File(".").toPath(), "output").toFile();
                options.outputDir.deleteOnExit();
                createdTempOutput = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        handleOptions(options, args, jc);

        // Generate agent with SeparateAgentGenerator
        try {
            SeparateAgentGenerator.generate(JavaMOPAgentMain.outputDir, JavaMOPAgentMain.agentName,
                    JavaMOPAgentMain.baseAspect, JavaMOPAgentMain.agentAspect, JavaMOPAgentMain.classDir, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (createdTempOutput) {
            try {
                Tool.deleteDirectory(options.outputDir.toPath());
            } catch(IOException e) {
                e.printStackTrace();
                System.err.println("Failed to remove temporary files.");
            }
        }

    }

    /**
     * This method sets some field and other (dependent) options, based on the
     * flags that the user called JavaMOP with.
     *
     * @param options  The object holding the options that the user called
     *                 JavaMOP with
     */
    private static void handleOptions(JavaMOPAgentOptions options, String[] args, JCommander jc) {
        if (args.length == 0 || options.files.size() < 2){
            jc.usage();
            System.exit(1);
        }

        JavaMOPAgentMain.excludeJars = options.excludeJars;
        JavaMOPAgentMain.baseAspect = options.baseAspect;
        JavaMOPAgentMain.agentName = options.agentName;
        if (JavaMOPAgentMain.agentName == null) {
            JavaMOPAgentMain.agentName = "agent";
        }
        JavaMOPAgentMain.outputDir = options.outputDir;

        for (int j = 0; j < args.length; j++) {
            if("-baseaspect".equals(args[j])) {
                 j++;
            } else if ("-n".equals(args[j]) || "-agentname".equals(args[j])) {
                j++;
            } else if ("-d".equals(args[j])) {
                j++;
            } else if (args[j].endsWith(".aj")) {
                JavaMOPAgentMain.agentAspect = new File(args[j]);
            } else if (!"-excludeJars".equals(args[j])){
                // class directory
                JavaMOPAgentMain.classDir = new File(args[j]);
            }
        }

    }
}
