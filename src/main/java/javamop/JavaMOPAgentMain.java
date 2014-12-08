// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import java.io.File;

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

        handleOptions(options, args, jc);


    }

    /**
     * This method sets some field and other (dependent) options, based on the
     * flags that the user called JavaMOP with.
     *
     * @param options  The object holding the options that the user called
     *                 JavaMOP with
     */
    private static void handleOptions(JavaMOPAgentOptions options, String[] args, JCommander jc) {
        if (args.length == 0){
            jc.usage();
            System.exit(1);
        }

        JavaMOPAgentMain.excludeJars = options.excludeJars;
        JavaMOPAgentMain.baseAspect = options.baseAspect;
        JavaMOPAgentMain.agentName = options.agentName;

    }
}
