package javamop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.channels.FileChannel;

import java.nio.file.Files;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;

import java.nio.file.attribute.BasicFileAttributes;

import java.util.Arrays;

public class GenerateAgent {
    
    public static void generate(File outputDir, String aspectname) throws IOException {
        // Step 9: Move all the jars used to a single location
        final String jarBase = new File("lib").getAbsolutePath() + File.separator;
        final String ajToolsJar = jarBase + "aspectjtools.jar";
        final String ajRtJar = jarBase + "aspectjrt.jar";
        final String rtJar = jarBase + "rt.jar";
        final String weaverJar = "aspectjweaver.jar";
        final String ajWeaverJar = jarBase + weaverJar;
        final String ajOutDir = outputDir.getAbsolutePath();
        final String baseAspectFile = jarBase + "BaseAspect.aj";
        final String manifest = "MANIFEST.MF";
        
        // Step 10: Compile the generated Java File (allRuntimeMonitor.java)
        final int javacReturn = runCommandDir(outputDir, "javac", "-d", ".",
            "-cp", ajRtJar + ":" + rtJar, aspectname + "RuntimeMonitor.java");
        if(javacReturn != 0) {
            System.err.println("(javac) Failed to compile agent.");
            return;
        }
        
        // Step 11: Compile the generated AJC File (allMonitorAspect.aj)
        final int ajcReturn = runCommandDir(outputDir, "java",
            "-cp", ajToolsJar + ":" + rtJar + ":" + ajRtJar + ":.", "org.aspectj.tools.ajc.Main",
            "-1.6", "-d", ajOutDir, "-outxml", baseAspectFile, aspectname + "MonitorAspect.aj");
        /*
        if(ajcReturn != 0) {
            System.err.println("(ajc) Failed to compile agent.");
            System.exit(ajcReturn);
        }*/
        final File aopAjc = new File(ajOutDir + File.separator + "META-INF" + File.separator +
            "aop-ajc.xml");
        if(aopAjc.exists()) {}
        else {
            System.err.println("(ajc) Failed to produce aop-ajc.xml");
            return;
        }
        // Step 13: Prepare the directory from which the agent will be built
        final File agentDir = Files.createTempDirectory(outputDir.toPath(), "agent-jar").toFile();
        agentDir.deleteOnExit();
        try {
            // copy in the needed jar files
            copyFile(new File(ajWeaverJar), new File(agentDir, weaverJar));
            copyFile(new File(rtJar), new File(agentDir, "rt.jar"));
            
            // Extract the relevant java files
            String[] toExtract = { weaverJar, "rt.jar" };
            for(String jar : toExtract) {
                int extractReturn = runCommandDir(agentDir, "jar", "xvf", jar);
                if(extractReturn != 0) {
                    System.err.println("(jar) Failed to extract " + jar);
                    return;
                }
            }
            
            // Copy in the postprocessed xml file
            final File metaInf = new File(agentDir, "META-INF");
            final boolean mkdirMetaInfReturn = (metaInf.exists() && metaInf.isDirectory()) ||
                metaInf.mkdir();
            if(mkdirMetaInfReturn) {}
            else {
                System.err.println("(mkdir) Failed to create META-INF");
                return;
            }
            copyFile(aopAjc, new File(metaInf, "aop-ajc.xml"));
            
            // directory to hold compiled library files
            
            /*
            final File mopDir = new File(agentDir, "mop");
            final boolean mkdirMopReturn = mopDir.mkdir();
            if(mkdirMopReturn) {}
            else {
                System.err.println("(mkdir) Failed to create mop");
                return;
            }*/
            
            // # Copy in all the .class files for all the monitor libraries
            new File(outputDir, "mop").renameTo(new File(agentDir, "mop"));
            
            // # Step 14: copy in the correct MANIFEST FILE
            final File jarManifest = new File(metaInf, manifest);
            copyFile(new File(jarBase + manifest), jarManifest);
            
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
    
    private static int runCommandDir(File dir, String... args) throws IOException {
        try {
            if(MOPProcessor.verbose) { // -v
                System.out.println(dir.toString() + ": " + Arrays.asList(args).toString());
            }
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(args).directory(dir);
            if(MOPProcessor.verbose) { // -v
                builder.inheritIO();
            }
            Process proc = builder.start();
            return proc.waitFor();
        } catch(InterruptedException ie) {
            ie.printStackTrace();
            return -1;
        }
    }
    
    private static void copyFile(File sourceFile, File destFile) throws IOException {
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
    
    public static void deleteDirectory(Path path) throws IOException {
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
}