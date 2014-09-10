package javamop;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import java.nio.channels.FileChannel;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;

import java.nio.file.attribute.BasicFileAttributes;

import java.util.Arrays;
import java.util.List;

/**
 * Handles generating a complete Java agent after .mop files have been processed into .rvm files
 * and .rvm files have been processed into .java files. Based on Owolabi's build-agent.sh.
 * @author A. Cody Schuffelen
 */
public final class GenerateAgent {
    
    private static final String manifest = "MANIFEST.MF";
    
    /**
     * Private to avoid instantiation.
     */
    private GenerateAgent() {
        
    }
    
    /**
     * Generate a JavaMOP agent. If {@code baseAspect} is null, a default base aspect will be used.
     * @param outputDir The place to put all the intermediate generated files in.
     * @param aspectname Generates {@code aspectname}.jar.
     * @param baseAspect The aspect file to combine with the generated aspects.
     * @throws IOException If something goes wrong in the many filesystem operations.
     */
    public static void generate(final File outputDir, final String aspectname,
            File baseAspect) throws IOException {
        
        if(!classOnClasspath("org.aspectj.runtime.reflect.JoinPointImpl")) {
            System.err.println("aspectjrt.jar is missing from the classpath. Halting.");
            return;
        }
        if(!classOnClasspath("org.aspectj.tools.ajc.Main")) {
            System.err.println("aspectjtools.jar is missing from the classpath. Halting.");
            return;
        }
        if(!classOnClasspath("org.aspectj.weaver.Advice")) {
            System.err.println("aspectjweaver.jar is missing from the classpath. Halting.");
            return;
        }
        
        final String ajOutDir = outputDir.getAbsolutePath();
        final String baseClasspath = getClasspath();
        
        // Step 10: Compile the generated Java File (allRuntimeMonitor.java)
        String generatedJavaFileName = aspectname + "RuntimeMonitor.java";
        if (JavaMOPMain.options.usedb){
            //sed -i 's/javamoprt/com\.runtimeverification\.rvmonitor\.java\.rt/g' $GENERATED_AJ
            File generatedJava = new File(outputDir.getName()+ File.separator + generatedJavaFileName);
            String lines = FileUtils.readFileToString(generatedJava, Charset.defaultCharset());
            lines = lines.replaceAll("javamoprt","com.runtimeverification.rvmonitor.java.rt");
            lines = lines.replaceAll("MOPLogging","RVMLogging");
            FileUtils.write(generatedJava,lines);
        }

        final int javacReturn = runCommandDir(outputDir, "javac", "-d", ".",
            "-cp", baseClasspath, generatedJavaFileName);
        if(javacReturn != 0) {
            System.err.println("(javac) Failed to compile agent.");
            return;
        }

        if(baseAspect == null) {
            baseAspect = new File(outputDir, "BaseAspect.aj");
        }
        if(!"BaseAspect.aj".equals(baseAspect.getName())) {
            throw new IOException("For now, --baseaspect files should be called BaseAspect.aj");
        }
        if(!baseAspect.exists()) {
            final boolean success = baseAspect.createNewFile();
            if(success) {
                writeBaseAspect(baseAspect);
            } else {
                System.err.println("Unable to write BaseAspect.aj.");
                return;
            }
        }

        // Step 11: Compile the generated AJC File (allMonitorAspect.aj)
        final int ajcReturn = runCommandDir(outputDir, "java", "-cp", baseClasspath,
            "org.aspectj.tools.ajc.Main", "-1.6", "-d", ajOutDir, "-outxml",
            baseAspect.getAbsolutePath(), aspectname + "MonitorAspect.aj");
        /*
        if(ajcReturn != 0) {
            System.err.println("(ajc) Failed to compile agent.");
            System.exit(ajcReturn);
        }*/
        final File aopAjc = new File(ajOutDir + File.separator + "META-INF" + File.separator +
            "aop-ajc.xml");
        if(!aopAjc.exists()) {
            System.err.println("(ajc) Failed to produce aop-ajc.xml");
            return;
        }

        // Step 12: suppress aspectJ warnings
        suppress_warnings(aopAjc);

        // Step 13: Prepare the directory from which the agent will be built
        final File agentDir = Files.createTempDirectory(outputDir.toPath(), "agent-jar").toFile();
        agentDir.deleteOnExit();
        try {
            // Copy in the postprocessed xml file
            final File metaInf = new File(agentDir, "META-INF");
            final boolean mkdirMetaInfReturn = (metaInf.exists() && metaInf.isDirectory()) ||
                metaInf.mkdir();
            if(!mkdirMetaInfReturn) {
                System.err.println("(mkdir) Failed to create META-INF");
                return;
            }
            copyFile(aopAjc, new File(metaInf, "aop-ajc.xml"));
            
            // directory to hold compiled library files
            // Copy in all the .class files for all the monitor libraries
            new File(outputDir, "mop").renameTo(new File(agentDir, "mop"));
            
            // # Step 14: copy in the correct MANIFEST FILE
            final File jarManifest = new File(metaInf, manifest);
            writeAgentManifest(jarManifest);
            
            // # Step 15: Stepmake the java agent jar
            final int jarReturn = runCommandDir(new File("."), "jar", "cmf", jarManifest.toString(), 
                aspectname + ".jar", "-C", agentDir.toString(), ".");
            if(jarReturn != 0) {
                System.err.println("(jar) Failed to produce final jar");
                return;
            }
            
            System.out.println(aspectname + ".jar is generated.");
        } finally {
            deleteDirectory(agentDir.toPath());
        }
    }

    private static void suppress_warnings(File aopAjc) {
        try {
            List<String> lines = FileUtils.readLines(aopAjc, Charsets.UTF_8);
            int index = lines.indexOf("</aspects>") + 1;
            lines.add(index, "<weaver options=\"-nowarn -Xlint:ignore\"></weaver>");
            FileUtils.writeLines(aopAjc,lines);
        } catch (IOException e) {
            System.err.println("(ajc) There was a problem reading aop-ajc.xml");
            e.printStackTrace();
        }
    }

    /**
     * Run a command in a directory. Passes the output of the run commands through if the program
     * is in verbose mode. Blocks until the command finishes, then gives the return code.
     * @param dir The directory to run the command in.
     * @param args The program to run and its arguments.
     * @return The return code of the program.
     */
    private static int runCommandDir(final File dir, final String... args) throws IOException {
        try {
            if(MOPProcessor.verbose) { // -v
                System.out.println(dir.toString() + ": " + Arrays.asList(args).toString());
            }
            final ProcessBuilder builder = new ProcessBuilder();
            builder.command(args).directory(dir);
            if(MOPProcessor.verbose) { // -v
                builder.inheritIO();
            }
            final Process proc = builder.start();
            return proc.waitFor();
        } catch(InterruptedException ie) {
            ie.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Copy the contents of one file to another.
     * @param sourceFile The file to take the contents from.
     * @param destFile The file to output to. Created if it does not already exist.
     * @throws IOException If something goes wrong copying the file.
     */
    private static void copyFile(final File sourceFile, final File destFile) throws IOException {
        // http://www.javalobby.org/java/forums/t17036.html
        if(!destFile.exists()) {
            destFile.createNewFile();
        }
        
        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }
    
    /**
     * Delete a directory and all its contents. Every file has to be individually deleted, since
     * there is no built-in java function to delete an entire directory and its contents
     * recursively.
     * @param path The path of the directory to delete.
     * @throws IOException If it cannot traverse the directories or the files cannot be deleted.
     */
    public static void deleteDirectory(final Path path) throws IOException {
        // http://stackoverflow.com/a/8685959
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) 
                    throws IOException {
                // try to delete the file anyway, even if its attributes
                // could not be read, since delete-only access is
                // theoretically possible
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) 
                    throws IOException {
                if (exc == null) {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                } else {
                    // directory iteration failed; propagate exception
                    throw exc;
                }
            }
        });
    }
    
    /**
     * The system classpath.
     * @return The classpath, separated in a platform-dependent manner.
     */
    private static String getClasspath() {
        return System.getProperty("java.class.path") + File.pathSeparator + ".";
    }
    
    /**
     * Test if a class is present on the system classpath.
     * @param name The full class name, including packages.
     * @return If the class {@code name} is on the classpath.
     */
    private static boolean classOnClasspath(String name) {
        try {
            Class.forName(name, false, GenerateAgent.class.getClassLoader());
            return true;
        } catch(ExceptionInInitializerError eiie) {
            throw new RuntimeException(
                "Class initializer for " + name + " should not have run.", eiie);
        } catch(ClassNotFoundException cnfe) {
            return false;
        } catch(LinkageError le) {
            throw new RuntimeException(
                "Class " + name + " is on the classpath, but is outdated.", le);
        }
    }

    /**
     * Write the agent manifest to a file. It extracts the locations of aspectjweaver.jar and
     * rvmonitorrt.jar from the classpath that is used when JavaMOP is run.
     * @param f The file to write the manifest to.
     * @throws IOException If something goes wrong in writing the file.
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
     * @param f The file to write to.
     * @throws IOException If something goes wrong in writing the file.
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
