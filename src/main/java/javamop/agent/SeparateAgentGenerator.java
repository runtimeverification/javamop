// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.agent;

import javamop.JavaMOPAgentMain;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.SystemUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

/**
 * Handles generating a complete Java agent after .mop files have been processed into .rvm files
 * and .rvm files have been processed into .java files. Based on A. Cody Schuffelen, Qingzhou Luo
 * and Philip Daian 's AgentGenerator.
 *
 * @author Qingzhou and He (xiaoguoyi27@gmail.com)
 */
public final class SeparateAgentGenerator {

    private static final String manifest = "MANIFEST.MF";

    /**
     * Private to avoid instantiation.
     */
    private SeparateAgentGenerator() {

    }

    /**
     * Generate a JavaMOP agent. If {@code baseAspect} is null, a default base aspect will be used.
     *
     * @param outputDir   The place to put all the intermediate generated files in.
     * @param aspectname  Generates {@code aspectname}.jar.
     * @param agentAspect The aspect file to used in the agent
     * @param verbose     whether in verbose mode or not
     * @throws java.io.IOException If something goes wrong in the many filesystem operations.
     */
    public static void generate(final File outputDir, final String aspectname, File agentAspect,
                                File classDir, boolean verbose) throws IOException {

        if (!classOnClasspath("org.aspectj.runtime.reflect.JoinPointImpl")) {
            System.err.println("aspectjrt.jar is missing from the classpath. Halting.");
            return;
        }
        if (!classOnClasspath("org.aspectj.tools.ajc.Main")) {
            System.err.println("aspectjtools.jar is missing from the classpath. Halting.");
            return;
        }
        if (!classOnClasspath("org.aspectj.weaver.Advice")) {
            System.err.println("aspectjweaver.jar is missing from the classpath. Halting.");
            return;
        }

        final String baseClasspath = getClasspath();

        // Step 1: Prepare the directory from which the agent will be built
        final File agentDir = Files.createTempDirectory(outputDir.toPath(), "agent-jar").toFile();
        agentDir.deleteOnExit();


        // Step 2: Compile the generated AJC File (allMonitorAspect.aj)
        // Change aspect name
        String completeClassPath = baseClasspath + File.pathSeparator + classDir.getAbsolutePath();

        if (SystemUtils.IS_OS_WINDOWS) {
            completeClassPath = "\"" + completeClassPath + "\"";
        }

        final int ajcReturn = runCommandDir(outputDir, verbose, "java", "-cp", completeClassPath,
                "org.aspectj.tools.ajc.Main", "-1.6", "-d", agentDir.getAbsolutePath(),
                "-outxml", agentAspect.getAbsolutePath());

        if(ajcReturn != 0) {
            System.err.println("(ajc) Failed to compile agent.");
            System.exit(ajcReturn);
        }

        final File metaInf = new File(agentDir, "META-INF");
        final boolean mkdirMetaInfReturn = (metaInf.exists() && metaInf.isDirectory()) || metaInf.mkdir();
        if (!mkdirMetaInfReturn) {
            System.err.println("(mkdir) Failed to create META-INF");
            return;
        }

        final File aopAjc = new File(agentDir.getAbsolutePath() + File.separator
                + "META-INF" + File.separator + "aop-ajc.xml");
        if (!aopAjc.exists()) {
            System.err.println("(ajc) Failed to produce aop-ajc.xml");
            return;
        }

        // Step 3: set options for Load-Time Weaving in the aop-ajc.xml file
        setWeaverOptions(aopAjc);

        // Also need to copy all the .class files from classDir to outputDir
        FileUtils.copyDirectory(classDir, agentDir, new OrFileFilter(DirectoryFileFilter.INSTANCE,
                new SuffixFileFilter(".class")));

        if (!JavaMOPAgentMain.excludeJars) {
            //extract the absolute paths for these two jars from java classpath
            //running "mvn package", or similar, would set this java classpath appropriately
            String weaverJarPath = getJarLocation(baseClasspath, "aspectjweaver");
            String rvMonitorRTJarPath = getJarLocation(baseClasspath, "rv-monitor-rt");

            //get the actual jar name from the absolute path
            String weaverJarName = null;
            String rvmRTJarName = null;
            if (rvMonitorRTJarPath != null && weaverJarPath != null) {
                weaverJarName = getJarName(weaverJarPath);
                rvmRTJarName = getJarName(rvMonitorRTJarPath);
            } else {
                System.err.println("(missing jars) Could not find aspectjweaver or rvmonitorrt "
                        + "in the \"java.class.path\" property. Did you run \"mvn package\"? ");
            }

            //make references so that these files can be referred to later
            File actualWeaverFile = new File(agentDir, weaverJarName);
            File actualRTFile = new File(agentDir, rvmRTJarName);

            // copy in the needed jar files
            copyFile(new File(weaverJarPath), actualWeaverFile);
            copyFile(new File(rvMonitorRTJarPath), actualRTFile);

            //extract aspectjweaver.jar and rvmonitorrt.jar (since their content will
            //be packaged with the agent.jar)
            int extractReturn = runCommandDir(agentDir, verbose, "jar", "xvf", weaverJarName);
            if (extractReturn != 0) {
                System.err.println("(jar) Failed to extract the AspectJ weaver jar");
                return;
            }

            extractReturn = runCommandDir(agentDir, verbose, "jar", "xvf", rvmRTJarName);
            if (extractReturn != 0) {
                System.err.println("(jar) Failed to extract the rvmonitorrt jar");
                return;
            }

            //remove extracted jars to make agent lighter weight
            if (!actualWeaverFile.delete()) {
                System.err.println("(delete) Failed to delete weaver jar; generated jar will "
                        + "have a bigger size than normal");
            }

            if (!actualRTFile.delete()) {
                System.err.println("(delete) Failed to delete rvmonitorrt jar; generated jar will"
                        + " have a bigger size than normal");
            }
        }

        // # Step 4: copy in the correct MANIFEST FILE
        final File jarManifest = new File(metaInf, manifest);
        writeAgentManifest(jarManifest);


        // # Step 5: Step make the java agent jar
        final int jarReturn = runCommandDir(new File("."), verbose, "jar", "cmf", jarManifest.toString(), aspectname
                + ".jar", "-C", agentDir.toString(), ".");
        if (jarReturn != 0) {
            System.err.println("(jar) Failed to produce final jar");
            return;
        }

        System.out.println(aspectname + ".jar is generated.");

    }


    /**
     * Generate a JavaMOP agent. If {@code baseAspect} is null, a default base aspect will be used.
     *
     * @param outputDir   The place to put all the intermediate generated files in.
     * @param aspectname  Generates {@code aspectname}.jar.
     * @param agentAspect The aspect file to used in the agent
     * @param verbose     whether in verbose mode or not
     * @throws java.io.IOException If something goes wrong in the many filesystem operations.
     */
    public static void eMOPGenerate(final File outputDir, final String aspectname, File emopAspectDir,
                                File classDir, boolean verbose) throws IOException {

        if (!classOnClasspath("org.aspectj.runtime.reflect.JoinPointImpl")) {
            System.err.println("aspectjrt.jar is missing from the classpath. Halting.");
            return;
        }
        if (!classOnClasspath("org.aspectj.tools.ajc.Main")) {
            System.err.println("aspectjtools.jar is missing from the classpath. Halting.");
            return;
        }
        if (!classOnClasspath("org.aspectj.weaver.Advice")) {
            System.err.println("aspectjweaver.jar is missing from the classpath. Halting.");
            return;
        }

        final String baseClasspath = getClasspath();

        // Step 1: Prepare the directory from which the agent will be built
        final File agentDir = Files.createTempDirectory(outputDir.toPath(), "agent-jar").toFile();
        agentDir.deleteOnExit();


        // Step 2: Compile the generated AJC File (allMonitorAspect.aj)
        // Change aspect name
        String completeClassPath = baseClasspath + File.pathSeparator + classDir.getAbsolutePath();

        if (SystemUtils.IS_OS_WINDOWS) {
            completeClassPath = "\"" + completeClassPath + "\"";
        }

        final int ajcReturn = runCommandDir(outputDir, verbose, "java", "-cp", completeClassPath,
                "org.aspectj.tools.ajc.Main", "-1.6", "-d", agentDir.getAbsolutePath(),
                "-outxml", emopAspectDir.getAbsolutePath() + "/*.aj");

        if(ajcReturn != 0) {
            System.err.println("(ajc) Failed to compile agent.");
            System.exit(ajcReturn);
        }

        final File metaInf = new File(agentDir, "META-INF");
        final boolean mkdirMetaInfReturn = (metaInf.exists() && metaInf.isDirectory()) || metaInf.mkdir();
        if (!mkdirMetaInfReturn) {
            System.err.println("(mkdir) Failed to create META-INF");
            return;
        }

        final File aopAjc = new File(agentDir.getAbsolutePath() + File.separator
                + "META-INF" + File.separator + "aop-ajc.xml");
        if (!aopAjc.exists()) {
            System.err.println("(ajc) Failed to produce aop-ajc.xml");
            return;
        }

        // Step 3: set options for Load-Time Weaving in the aop-ajc.xml file
        setWeaverOptions(aopAjc);

        // Also need to copy all the .class files from classDir to outputDir
        FileUtils.copyDirectory(classDir, agentDir, new OrFileFilter(DirectoryFileFilter.INSTANCE,
                new SuffixFileFilter(".class")));

        if (!JavaMOPAgentMain.excludeJars) {
            //extract the absolute paths for these two jars from java classpath
            //running "mvn package", or similar, would set this java classpath appropriately
            String weaverJarPath = getJarLocation(baseClasspath, "aspectjweaver");
            String rvMonitorRTJarPath = getJarLocation(baseClasspath, "rv-monitor-rt");

            //get the actual jar name from the absolute path
            String weaverJarName = null;
            String rvmRTJarName = null;
            if (rvMonitorRTJarPath != null && weaverJarPath != null) {
                weaverJarName = getJarName(weaverJarPath);
                rvmRTJarName = getJarName(rvMonitorRTJarPath);
            } else {
                System.err.println("(missing jars) Could not find aspectjweaver or rvmonitorrt "
                        + "in the \"java.class.path\" property. Did you run \"mvn package\"? ");
            }

            //make references so that these files can be referred to later
            File actualWeaverFile = new File(agentDir, weaverJarName);
            File actualRTFile = new File(agentDir, rvmRTJarName);

            // copy in the needed jar files
            copyFile(new File(weaverJarPath), actualWeaverFile);
            copyFile(new File(rvMonitorRTJarPath), actualRTFile);

            //extract aspectjweaver.jar and rvmonitorrt.jar (since their content will
            //be packaged with the agent.jar)
            int extractReturn = runCommandDir(agentDir, verbose, "jar", "xvf", weaverJarName);
            if (extractReturn != 0) {
                System.err.println("(jar) Failed to extract the AspectJ weaver jar");
                return;
            }

            extractReturn = runCommandDir(agentDir, verbose, "jar", "xvf", rvmRTJarName);
            if (extractReturn != 0) {
                System.err.println("(jar) Failed to extract the rvmonitorrt jar");
                return;
            }

            //remove extracted jars to make agent lighter weight
            if (!actualWeaverFile.delete()) {
                System.err.println("(delete) Failed to delete weaver jar; generated jar will "
                        + "have a bigger size than normal");
            }

            if (!actualRTFile.delete()) {
                System.err.println("(delete) Failed to delete rvmonitorrt jar; generated jar will"
                        + " have a bigger size than normal");
            }
        }

        // # Step 4: copy in the correct MANIFEST FILE
        final File jarManifest = new File(metaInf, manifest);
        writeAgentManifest(jarManifest);


        // # Step 5: Step make the java agent jar
        final int jarReturn = runCommandDir(new File("."), verbose, "jar", "cmf", jarManifest.toString(), aspectname
                + ".jar", "-C", agentDir.toString(), ".");
        if (jarReturn != 0) {
            System.err.println("(jar) Failed to produce final jar");
            return;
        }

        System.out.println(aspectname + ".jar is generated.");

    }






    /**
     * Takes an absolute path to a jar file and returns only the filename of the jar file
     *
     * @param pathToJar An absolute path to the jar file
     * @return Filename of the jar
     */
    private static String getJarName(String pathToJar) {
        File file = new File(pathToJar);
        return file.getName();
    }

    /**
     * Takes the entire "java.class.path" system property and extracts one of the jars in the path
     *
     * @param baseClasspath A string representation of the "java.class.path" system property
     * @param key           A partial or complete name for the jar to be extracted from the path
     * @return Absolute path to a jar whose name (partially) matches the key, or null if no
     * match is found
     */
    private static String getJarLocation(String baseClasspath, String key) {
        String[] jars = baseClasspath.split(File.pathSeparator);
        String value = null;
        for (int i = 0; i < jars.length; i++) {
            if (jars[i].contains(key)) {
                // assuming the jar occurs only once. This may be a problem if the user has some jar
                // with similar name in their classpath already
                value = jars[i];
                break;
            }
        }

        return value;
    }

    /**
     * Add a line to the aop-ajc.xml file in order to set options for the load-time weaving
     *
     * @param aopAjc An object reference to the aop-ajc.xml file
     */
    private static void setWeaverOptions(File aopAjc) {
        try {
            List<String> lines = FileUtils.readLines(aopAjc, Charsets.UTF_8);
            int index = lines.indexOf("</aspects>") + 1;
            String makeVerboseAgentOptions = " -verbose -showWeaveInfo";
            String suppressWarningOptions = "-nowarn -Xlint:ignore";
            String weaverOptions = "<weaver options=\"" + suppressWarningOptions;
            weaverOptions = JavaMOPAgentMain.makeVerboseAgent ? weaverOptions + makeVerboseAgentOptions : weaverOptions;
            weaverOptions += "\"></weaver>";
            lines.add(index, weaverOptions);
            FileUtils.writeLines(aopAjc, lines);
        } catch (IOException e) {
            System.err.println("(ajc) There was a problem reading aop-ajc.xml");
            e.printStackTrace();
        }
    }

    /**
     * Run a command in a directory. Passes the output of the run commands through if the program
     * is in verbose mode. Blocks until the command finishes, then gives the return code.
     *
     * @param dir     The directory to run the command in.
     * @param verbose whether in verbose mode or not
     * @param args    The program to run and its arguments.
     * @return The return code of the program.
     */
    private static int runCommandDir(final File dir, boolean verbose, final String... args) throws IOException {
        try {
            if (verbose) { // -v
                System.out.println(dir.toString() + ": " + Arrays.asList(args).toString());
            }
            final ProcessBuilder builder = new ProcessBuilder();
            builder.command(args).directory(dir);
            if (verbose) { // -v
                builder.inheritIO();
            } else {
                builder.redirectErrorStream(true);
            }
            final Process proc = builder.start();

            // If the output stream does not get consumed, when the buffer of the subprocess
            // is full it will get blocked. This fixed issue #37:
            // https://github.com/runtimeverification/javamop/issues/37
            if (!verbose) {
                // Consume output/error stream
                final StringWriter writer = new StringWriter();
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            IOUtils.copy(proc.getInputStream(), writer);
                        } catch (IOException e) {
                            System.err.println("Exception in reading subprocess output: " + e.getMessage());
                        }
                    }
                }).start();
            }

            return proc.waitFor();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            return -1;
        }
    }

    /**
     * Copy the contents of one file to another.
     *
     * @param sourceFile The file to take the contents from.
     * @param destFile   The file to output to. Created if it does not already exist.
     * @throws java.io.IOException If something goes wrong copying the file.
     */
    private static void copyFile(final File sourceFile, final File destFile) throws IOException {
        // http://www.javalobby.org/java/forums/t17036.html
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {

            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    public static void main(String[] args) {

    }

    /**
     * The system classpath.
     *
     * @return The classpath, separated in a platform-dependent manner.
     */
    private static String getClasspath() {
        return System.getProperty("java.class.path") + File.pathSeparator + ".";
    }

    /**
     * Test if a class is present on the system classpath.
     *
     * @param name The full class name, including packages.
     * @return If the class {@code name} is on the classpath.
     */
    private static boolean classOnClasspath(String name) {
        try {
            Class.forName(name, false, SeparateAgentGenerator.class.getClassLoader());
            return true;
        } catch (ExceptionInInitializerError eiie) {
            throw new RuntimeException("Class initializer for " + name + " should not have run.", eiie);
        } catch (ClassNotFoundException cnfe) {
            return false;
        } catch (LinkageError le) {
            throw new RuntimeException("Class " + name + " is on the classpath, but is outdated.", le);
        }
    }

    /**
     * Write the agent manifest to a file. It extracts the locations of aspectjweaver.jar and
     * rvmonitorrt.jar from the classpath that is used when JavaMOP is run.
     *
     * @param f The file to write the manifest to.
     * @throws java.io.IOException If something goes wrong in writing the file.
     */
    private static void writeAgentManifest(final File f) throws IOException {
        final PrintWriter writer = new PrintWriter(f);
        try {
            writer.println("Manifest-Version: 1.0");
            writer.println("Name: org/aspectj/weaver/");
            writer.println("Specification-Title: AspectJ Weaver Classes");
            writer.println("Specification-Version: DEVELOPMENT");
            writer.println("Specification-Vendor: aspectj.org");
            writer.println("Implementation-Title: org.aspectj.weaver");
            writer.println("Implementation-Version: DEVELOPMENT");
            writer.println("Implementation-Vendor: aspectj.org");
            writer.println("Premain-Class: org.aspectj.weaver.loadtime.Agent");
            writer.println("Can-Redefine-Classes: true");
            /*
             * Jar files can have at most 72 characters per line, so this splits it by jar file
             * into many lines.

            writer.println("Boot-Class-Path: " +
                getClasspath().replace(File.pathSeparator, System.lineSeparator() + " "));
                        */
            writer.flush();
        } finally {
            writer.close();
        }
    }

    /**
     * Write the base aspect to a file.
     *
     * @param f The file to write to.
     * @throws java.io.IOException If something goes wrong in writing the file.
     */
    private static void writeBaseAspect(final File f) throws IOException {
        final PrintWriter writer = new PrintWriter(f);
        try {
            writer.println("package mop;");
            writer.println("public aspect BaseAspect {");
            writer.println("    pointcut notwithin() :");
            writer.println("    !within(sun..*) &&");
            writer.println("    !within(java..*) &&");
            writer.println("    !within(javax..*) &&");
            writer.println("    !within(com.sun..*) &&");
            writer.println("    !within(org.dacapo.harness..*) &&");
            writer.println("    !within(org.apache.commons..*) &&");
            writer.println("    !within(org.apache.geronimo..*) &&");
            writer.println("    !within(net.sf.cglib..*) &&");
            writer.println("    !within(mop..*) &&");
            writer.println("    !within(javamoprt..*) &&");
            writer.println("    !within(rvmonitorrt..*) &&");
            writer.println("    !within(com.runtimeverification..*);");
            writer.println("}");
            writer.flush();
        } finally {
            writer.close();
        }
    }
}
