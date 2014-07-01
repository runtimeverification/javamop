/**
 * @author fengchen, Dongyun Jin, Patrick Meredith, Michael Ilseman
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

package javamop;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.channels.FileChannel;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import javamop.parser.ast.MOPSpecFile;
import javamop.util.Tool;
import javamop.util.AJFileCombiner;

class JavaFileFilter implements FilenameFilter {
    public boolean accept(File dir, String name) {
        return name.endsWith(".java");
    }
}

class MOPFileFilter implements FilenameFilter {
    public boolean accept(File dir, String name) {
        return name.endsWith(".mop");
    }
}

public class JavaMOPMain {
    
    private static File outputDir = null;
    public static boolean debug = false;
    public static boolean noopt1 = false;
    public static boolean toJavaLib = false;
    public static boolean statistics = false;
    public static boolean statistics2 = false;
    public static String aspectname = null;
    public static boolean specifiedAJName = false;
    public static boolean isJarFile = false;
    public static String jarFilePath = null;
    
    public static final int NONE = 0;
    public static final int HANDLERS = 1;
    public static final int EVENTS = 2;
    public static int logLevel = NONE;
    
    public static boolean dacapo = false;
    public static boolean dacapo2 = false;
    public static boolean silent = false;
    public static boolean empty_advicebody = false;
    public static boolean translate2RV = true;
    
    public static boolean merge = false;
    public static boolean inline = false;
    
    public static boolean scalable = false;
    public static boolean keepRVFiles = false;
    
    private static final List<String []> listFilePairs = new ArrayList<String []>();
    private static final List<String> listRVMFiles = new ArrayList<String>();
    
    static private File getTargetDir(ArrayList<File> specFiles) throws MOPException{
        if(JavaMOPMain.outputDir != null){
            return outputDir;
        }
        
        boolean sameDir = true;
        File parentFile = null;
        
        for(File file : specFiles){
            if(parentFile == null){
                parentFile = file.getAbsoluteFile().getParentFile();
            } else {
                if(file.getAbsoluteFile().getParentFile().equals(parentFile)){
                    continue;
                } else {
                    sameDir = false;
                    break;
                }
            }
        }
        
        if(sameDir){
            return parentFile;
        } else {
            return new File(".");
        }
    }
    
    
    /**
     * Process a java file including mop annotations to generate an aspectj file. The path 
     * argument should be an existing java file name. The location argument should contain the 
     * original file name, But it may have a different directory.
     * 
     * @param path
     *            an absolute path of a specification file
     * @param location
     *            an absolute path for result file
     */
    public static void processJavaFile(File file, String location) throws MOPException {
        MOPNameSpace.init();
        String specStr = SpecExtractor.process(file);
        MOPSpecFile spec =  SpecExtractor.parse(specStr);
        
        if (JavaMOPMain.aspectname == null) {
            JavaMOPMain.aspectname = Tool.getFileName(file.getAbsolutePath());
        }
        MOPProcessor processor = new MOPProcessor(JavaMOPMain.aspectname);
        
        String aspect = processor.process(spec);
        writeFile(aspect, location, "MonitorAspect.aj");
    }
    
    /**
     * Process a specification file to generate an aspectj file. The path argument should be an 
     * existing specification file name. The location argument should contain the original file 
     * name, But it may have a different directory.
     * 
     * @param path
     *            an absolute path of a specification file
     * @param location
     *            an absolute path for result file
     */
    public static void processSpecFile(File file, String location) throws MOPException {
        MOPNameSpace.init();
        String specStr = SpecExtractor.process(file);
        MOPSpecFile spec =  SpecExtractor.parse(specStr);
        
        if (JavaMOPMain.aspectname == null) {
            JavaMOPMain.aspectname = Tool.getFileName(file.getAbsolutePath());
        }
        
        MOPProcessor processor = new MOPProcessor(JavaMOPMain.aspectname);
        
        String output = processor.process(spec);
        
        if (translate2RV) {
            writeFile(processor.translate2RV(spec), file.getAbsolutePath(), ".rvm");
        }
        
        if (toJavaLib) {
            writeFile(output, location, "JavaLibMonitor.java");
        } else {
            writeFile(output, location, "MonitorAspect.aj");
        }
    }
    
    public static void processMultipleFiles(ArrayList<File> specFiles) throws MOPException {
        String aspectName;
        
        if(outputDir == null){
            outputDir = getTargetDir(specFiles);
        }
        
        if(JavaMOPMain.aspectname != null) {
            aspectName = JavaMOPMain.aspectname;
        } else {
            if(specFiles.size() == 1) {
                aspectName = Tool.getFileName(specFiles.get(0).getAbsolutePath());
            } else {
                int suffixNumber = 0;
                // generate auto name like 'MultiMonitorApsect.aj'
                
                File aspectFile;
                do{
                    suffixNumber++;
                    aspectFile = new File(outputDir.getAbsolutePath() + File.separator + 
                        "MultiSpec_" + suffixNumber + "MonitorAspect.aj");
                } while(aspectFile.exists());
                
                aspectName = "MultiSpec_" + suffixNumber;
            }
            JavaMOPMain.aspectname = aspectName;
        }
        MOPProcessor processor = new MOPProcessor(aspectName);
        MOPNameSpace.init();
        ArrayList<MOPSpecFile> specs = new ArrayList<MOPSpecFile>();
        for(File file : specFiles){
            //System.out.println(file);
            String specStr = SpecExtractor.process(file);
            MOPSpecFile spec =  SpecExtractor.parse(specStr);
            if (translate2RV) {
                writeFile(processor.translate2RV(spec), file.getAbsolutePath(), ".rvm");
            }
            specs.add(spec);
        }
        MOPSpecFile combinedSpec = SpecCombiner.process(specs);
        String output = processor.process(combinedSpec);
        writeCombinedAspectFile(output, aspectName);
    }
    
    protected static void writeJavaFile(String javaContent, String location) throws MOPException {
        if ((javaContent == null) || (javaContent.length() == 0))
            throw new MOPException("Nothing to write as a java file");
        if (!Tool.isJavaFile(location))
            throw new MOPException(location + "should be a Java file!");
        
        try {
            FileWriter f = new FileWriter(location);
            f.write(javaContent);
            f.close();
        } catch (Exception e) {
            throw new MOPException(e.getMessage());
        }
    }
    
    protected static void writeCombinedAspectFile(String aspectContent, String aspectName) 
            throws MOPException {
        if (aspectContent == null || aspectContent.length() == 0)
            return;
        
        try {
            FileWriter f = new FileWriter(outputDir.getAbsolutePath() + File.separator + 
                aspectName + "MonitorAspect.aj");
            f.write(aspectContent);
            f.close();
        } catch (Exception e) {
            throw new MOPException(e.getMessage());
        }
        System.out.println(" " + aspectName + "MonitorAspect.aj is generated");
    }
    
    protected static void writeFile(String content, String location, String suffix) 
            throws MOPException {
        if (content == null || content.length() == 0)
            return;
        
        int i = location.lastIndexOf(File.separator);
        String filePath = ""; 
        try {
            filePath = location.substring(0, i + 1) + Tool.getFileName(location) + suffix;
            FileWriter f = new FileWriter(filePath);
            f.write(content);
            f.close();
        } catch (Exception e) {
            throw new MOPException(e.getMessage());
        }
        if (suffix.equals(".rvm")) {
            listRVMFiles.add(filePath);
        }
        System.out.println(" " + Tool.getFileName(location) + suffix + " is generated");
    }
    
    // PM
    protected static void writePluginOutputFile(String pluginOutput, String location) 
            throws MOPException {
        int i = location.lastIndexOf(File.separator);
        
        try {
            FileWriter f = new FileWriter(location.substring(0, i + 1) + 
                Tool.getFileName(location) + "PluginOutput.txt");
            f.write(pluginOutput);
            f.close();
        } catch (Exception e) {
            throw new MOPException(e.getMessage());
        }
        System.out.println(" " + Tool.getFileName(location) + "PluginOutput.txt is generated");
    }
    
    public static String polishPath(String path) {
        if (path.indexOf("%20") > 0)
            path = path.replaceAll("%20", " ");
        
        return path;
    }
    
    public static ArrayList<File> collectFiles(String[] files, String path) throws MOPException {
        ArrayList<File> ret = new ArrayList<File>();
        
        for (String file : files) {
            String fPath = path.length() == 0 ? file : path + File.separator + file;
            File f = new File(fPath);
            
            if (!f.exists()) {
                throw new MOPException("[Error] Target file, " + file + ", doesn't exsit!");
            } else if (f.isDirectory()) {
                ret.addAll(collectFiles(f.list(), f.getAbsolutePath()));
            } else {
                if (Tool.isSpecFile(file)) {
                    ret.add(f);
                } else if (Tool.isJavaFile(file)) {
                    ret.add(f);
                } else {
                    //System.err.println("Ignoring " + file);
                    /*throw new MOPException("Unrecognized file type! The JavaMOP specification " +
                        "file should have .mop as the extension.");*/
                }
            }
        }
        
        return ret;
    }
    
    public static void process(String[] files, String path) throws MOPException {
        ArrayList<File> specFiles = collectFiles(files, path);
        
        if(JavaMOPMain.aspectname != null && files.length > 1){
            JavaMOPMain.merge = true;
        }
        
        if (JavaMOPMain.merge) {
            System.out.println("-Processing " + specFiles.size()
            + " specification(s)");
            processMultipleFiles(specFiles);
            String javaFile = outputDir.getAbsolutePath() + File.separator
            + JavaMOPMain.aspectname + "RuntimeMonitor.java";
            String ajFile = outputDir.getAbsolutePath() + File.separator
            + JavaMOPMain.aspectname + "MonitorAspect.aj";
            String combinerArgs[] = new String[2];
            combinerArgs[0] = javaFile;
            combinerArgs[1] = ajFile;
            
            listFilePairs.add(combinerArgs);
            
        } else {
            for (File file : specFiles) {
                boolean needResetAspectName = JavaMOPMain.aspectname == null;
                String location = outputDir == null ? file.getAbsolutePath() : 
                    outputDir.getAbsolutePath() + File.separator + file.getName();
                System.out.println("-Processing " + file.getPath());
                if (Tool.isSpecFile(file.getName())) {
                    processSpecFile(file, location);
                } else if (Tool.isJavaFile(file.getName())) {
                    processJavaFile(file, location);
                }
                
                File combineDir = outputDir == null ? file.getAbsoluteFile()
                .getParentFile() : outputDir;
                
                String javaFile = combineDir.getAbsolutePath() + File.separator
                + JavaMOPMain.aspectname + "RuntimeMonitor.java";
                String ajFile = combineDir.getAbsolutePath() + File.separator
                + JavaMOPMain.aspectname + "MonitorAspect.aj";
                String combinerArgs[] = new String[2];
                combinerArgs[0] = javaFile;
                combinerArgs[1] = ajFile;
                
                listFilePairs.add(combinerArgs);
                
                if (needResetAspectName) {
                    JavaMOPMain.aspectname = null;
                }
            }
        }
    }
    
    public static void process(String arg) throws MOPException {
        if(outputDir != null && !outputDir.exists())
            throw new MOPException("The output directory, " + outputDir.getPath() + 
                " does not exist.");
        
        process(arg.split(";"), "");
    }
    
    // PM
    public static void print_help() {
        System.out.println("Usage: java [-cp javmaop_classpath] javamop.JavaMOPMain [-options] " +
            "files");
        System.out.println("");
        System.out.println("where options include:");
        System.out.println(" Options enabled by default are prefixed with \'+\'");
        System.out.println("    -h -help\t\t\t  print this help message");
        System.out.println("    -v | -verbose\t\t  enable verbose output");
        System.out.println("    -debug\t\t\t  enable verbose error message");
        System.out.println();
        
        System.out.println("    -local\t\t\t+ use local logic engine");
        System.out.println("    -remote\t\t\t  use default remote logic engine");
        System.out.println("\t\t\t\t  " + Configuration.getServerAddr());
        System.out.println("\t\t\t\t  (You can change the default address");
        System.out.println("\t\t\t\t   in javamop/config/remote_server_addr.properties)");
        System.out.println("    -remote:<server address>\t  use remote logic engine");
        System.out.println();
        
        System.out.println("    -d <output path>\t\t  select directory to store output files");
        System.out.println("    -n | -aspectname <aspect name>\t  use the given aspect name " +
            "instead of source code name");
        System.out.println();
        
        System.out.println("    -showevents\t\t\t  show every event/handler occurrence");
        System.out.println("    -showhandlers\t\t\t  show every handler occurrence");
        System.out.println();
        
        System.out.println("    -s | -statistics\t\t  generate monitor with statistics");
        System.out.println("    -noopt1\t\t\t  don't use the enable set optimization");
        System.out.println("    -javalib\t\t\t  generate a java library rather than an " +
            "AspectJ file");
        System.out.println();
        
        System.out.println("    -aspect:\"<command line>\"\t  compile the result right after " +
            "it is generated");
        System.out.println();
    }
    
    public static void main(String[] args) {
        ClassLoader loader = JavaMOPMain.class.getClassLoader();
        String mainClassPath = loader.getResource("javamop/JavaMOPMain.class").toString();
        if (mainClassPath.endsWith(".jar!/javamop/JavaMOPMain.class") && 
                mainClassPath.startsWith("jar:")) {
            isJarFile = true;
            
            jarFilePath = mainClassPath.substring("jar:file:".length(), mainClassPath.length() - 
                "!/javamop/JavaMOPMain.class".length());
            jarFilePath = polishPath(jarFilePath);
        }
        
        boolean generateAgent = false;
        
        int i = 0;
        String files = "";
        
        while (i < args.length) {
            if (args[i].compareTo("-h") == 0 || args[i].compareTo("-help") == 0) {
                print_help();
                return;
            }
            
            if (args[i].compareTo("-d") == 0) {
                i++;
                outputDir = new File(args[i]);
            } else if (args[i].compareTo("-local") == 0) {
            } else if (args[i].compareTo("-remote") == 0) {
            } else if (args[i].startsWith("-remote:")) {
            } else if (args[i].compareTo("-v") == 0 || args[i].compareTo("-verbose") == 0) {
                MOPProcessor.verbose = true;
            } else if (args[i].compareTo("-javalib") == 0) {
                toJavaLib = true;
            } else if (args[i].compareTo("-debug") == 0) {
                JavaMOPMain.debug = true;
            } else if (args[i].compareTo("-noopt1") == 0) {
                JavaMOPMain.noopt1 = true;
            } else if (args[i].compareTo("-s") == 0 || args[i].compareTo("-statistics") == 0) {
                JavaMOPMain.statistics = true;
            } else if (args[i].compareTo("-s2") == 0 || args[i].compareTo("-statistics2") == 0) {
                JavaMOPMain.statistics2 = true;
            } else if (args[i].compareTo("-n") == 0 || args[i].compareTo("-aspectname") == 0) {
                i++;
                JavaMOPMain.aspectname = args[i];
                JavaMOPMain.specifiedAJName = true;
            } else if (args[i].compareTo("-showhandlers") == 0) {
                if (JavaMOPMain.logLevel < JavaMOPMain.HANDLERS)
                    JavaMOPMain.logLevel = JavaMOPMain.HANDLERS;
            } else if (args[i].compareTo("-showevents") == 0) {
                if (JavaMOPMain.logLevel < JavaMOPMain.EVENTS)
                    JavaMOPMain.logLevel = JavaMOPMain.EVENTS;
            } else if (args[i].compareTo("-dacapo") == 0) {
                JavaMOPMain.dacapo = true;
            } else if (args[i].compareTo("-dacapo2") == 0) {
                JavaMOPMain.dacapo2 = true;
            } else if (args[i].compareTo("-silent") == 0) {
                JavaMOPMain.silent = true;
            } else if (args[i].compareTo("-merge") == 0) {
                JavaMOPMain.merge = true;
            } else if (args[i].compareTo("-inline") == 0) {
                JavaMOPMain.inline = true;
            } else if (args[i].compareTo("-noadvicebody") == 0) {
                JavaMOPMain.empty_advicebody = true;
            } else if (args[i].compareTo("-scalable") == 0) {
                JavaMOPMain.scalable = true;
            } else if (args[i].compareTo("-translate2RV") == 0) {
                JavaMOPMain.translate2RV = true;
            } else if (args[i].compareTo("-keepRVFiles") == 0) {
                JavaMOPMain.keepRVFiles = true;
            } else if("--agent".equals(args[i])) {
                merge = true;
                generateAgent = true;
                keepRVFiles = true;
            } else {
                if (files.length() != 0)
                    files += ";";
                files += args[i];
            }
            ++i;
        }
        
        if (files.length() == 0) {
            print_help();
            return;
        }
        
        boolean tempOutput = false;
        
        if(generateAgent && outputDir == null) {
            tempOutput = true;
            try {
                outputDir = Files.createTempDirectory(new File(".").toPath(), "output").toFile();
                outputDir.deleteOnExit();
            } catch(IOException ioe) {
                ioe.printStackTrace();
                generateAgent = false;
            }
        }
        
        
        // Generate .rvm files and .aj files
        try {
            process(files); 
        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (JavaMOPMain.debug)
                e.printStackTrace();
        }
        
        // replace mop with rvm and call rv-monitor
        int length = args.length;
        
        if (JavaMOPMain.keepRVFiles) {
            //length--;
        }
        List<String> rvArgs = new ArrayList<String>();
        int p = 0;
        for (int j = 0; j < args.length; j++) {
            if (args[j].compareTo("-keepRVFiles") == 0) {
                // Don't pass keepRVFiles to rvmonitor
                rvArgs.add("");
            } else if("--agent".equals(args[j])) {
                rvArgs.add("");
            } else {
                rvArgs.add(args[j].replaceAll("\\.mop", "\\.rvm"));
            }
        }
        if(tempOutput) {
            rvArgs.add("-d");
            rvArgs.add(outputDir.getAbsolutePath());
        }
        
        Main.main(rvArgs.toArray(new String[0]));
        
        if(generateAgent) {
            try {
                generateJavaAgent();
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
        }
        
        // Call AJFileCombiner here to combine these two
        // TODO
        for (String[] filePair : listFilePairs) {
            AJFileCombiner.main(filePair);
            File javaFile = new File(filePair[0]);
            try {
                if (!JavaMOPMain.keepRVFiles) {
                    boolean deleted = javaFile.delete();
                    if (!deleted) {
                        System.err.println("Failed to delete java file: "
                        + filePair[0]);
                    }
                }
            } catch (Exception e) {
                
            }
        }
        
        for (String rvmFilePath : listRVMFiles) {
            File rvmFile = new File(rvmFilePath);
            try {               
                if (!JavaMOPMain.keepRVFiles) {
                    boolean deleted = rvmFile.delete();
                    if (!deleted) {
                        System.err.println("Failed to delete java file: "
                        + rvmFilePath);
                    }
                }
                
            } catch (Exception e) {
                
            }
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
    
    private static void generateJavaAgent() throws IOException {
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
        }
        
        System.out.println(aspectname + ".jar is generated.");
    }
}
