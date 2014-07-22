/**
 * @author fengchen, Dongyun Jin, Patrick Meredith, Michael Ilseman
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

package javamop;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.runtimeverification.rvmonitor.java.rvj.Main;
import javamop.commandline.JavaMOPOptions;
import javamop.parser.ast.MOPSpecFile;
import javamop.util.Tool;
import javamop.util.AJFileCombiner;

/**
 * Entry point class for the JavaMOP program.
 */
public final class JavaMOPMain {

    private static JavaMOPOptions options;

    /**
     * Private to prevent instantiation.
     */
    private JavaMOPMain() {

    }

//    private static File outputDir = null;
//    public static boolean debug = false;
//    public static boolean noopt1 = false;
//    public static boolean toJavaLib = false;
//    public static boolean statistics = false;
//    public static boolean statistics2 = false;
//    public static String aspectname = null;
    public static boolean specifiedAJName = false;
    public static boolean isJarFile = false;
    public static String jarFilePath = null;
    
    public static final int NONE = 0;
    public static final int HANDLERS = 1;
    public static final int EVENTS = 2;
    public static int logLevel = NONE;
    
//    public static boolean dacapo = false;
//    public static boolean dacapo2 = false;
//    public static boolean silent = false;
    public static boolean empty_advicebody = false;
//    public static boolean translate2RV = true;
//
//    public static boolean merge = false;
//    public static boolean inline = false;
//
//    public static boolean scalable = false;
//    public static boolean keepRVFiles = false;
//
//    public static boolean generateAgent = false;
//    public static File baseAspect = null;

    private static final List<String []> listFilePairs = new ArrayList<String []>();
    private static final List<String> listRVMFiles = new ArrayList<String>();
    
    /**
     * Determine where to place the JavaMOP output files, if it is not decided elsewhere. If all
     * the parameter files are in the same directory, use that directory. Otherwise, use the
     * directory that JavaMOP is executing in.
     * @param specFiles The specifications the program is being run on.
     * @return The directory to place output files in.
     * @throws MOPException If something goes wrong finding the file locations.
     */
    static private File getTargetDir(ArrayList<File> specFiles) throws MOPException{
        if(options.outputDir != null){
            return options.outputDir;
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
     * @param file
     *            an absolute path of a specification file
     * @param location
     *            an absolute path for result file
     */
    public static void processJavaFile(File file, String location) throws MOPException {
        MOPNameSpace.init();
        String specStr = SpecExtractor.process(file);
        MOPSpecFile spec =  SpecExtractor.parse(specStr);
        
        if (options.aspectname == null) {
            options.aspectname = Tool.getFileName(file.getAbsolutePath());
        }
        MOPProcessor processor = new MOPProcessor(options.aspectname);
        
        String aspect = processor.process(spec);
        writeFile(aspect, location, "MonitorAspect.aj");
    }
    
    /**
     * Process a specification file to generate an aspectj file. The path argument should be an 
     * existing specification file name. The location argument should contain the original file 
     * name, But it may have a different directory.
     * 
     * @param file
     *            an absolute path of a specification file
     * @param location
     *            an absolute path for result file
     */
    public static void processSpecFile(File file, String location) throws MOPException {
        MOPNameSpace.init();
        String specStr = SpecExtractor.process(file);
        MOPSpecFile spec =  SpecExtractor.parse(specStr);
        
        if (options.aspectname == null) {
            options.aspectname = Tool.getFileName(file.getAbsolutePath());
        }
        
        MOPProcessor processor = new MOPProcessor(options.aspectname);
        
        String output = processor.process(spec);
        
        if (options.translate2RV) {
            writeFile(processor.translate2RV(spec), file.getAbsolutePath(), ".rvm");
        }
        
        if (options.toJavaLib) {
            writeFile(output, location, "JavaLibMonitor.java");
        } else {
            writeFile(output, location, "MonitorAspect.aj");
        }
    }
    
    /**
     * Process multiple specification files, either each to a corresponding RVM/AJ file or merging
     * all of them together into two combined RVM/AJ files.
     * @param specFiles All the specifications to consider.
     * @throws MOPException If something goes wrong in conversion.
     */
    public static void processMultipleFiles(ArrayList<File> specFiles) throws MOPException {
        String aspectName;
        
        if(options.outputDir == null){
            options.outputDir = getTargetDir(specFiles);
        }
        
        if(options.aspectname != null) {
            aspectName = options.aspectname;
        } else {
            if(specFiles.size() == 1) {
                aspectName = Tool.getFileName(specFiles.get(0).getAbsolutePath());
            } else {
                int suffixNumber = 0;
                // generate auto name like 'MultiMonitorApsect.aj'
                
                File aspectFile;
                do{
                    suffixNumber++;
                    aspectFile = new File(options.outputDir.getAbsolutePath() + File.separator +
                        "MultiSpec_" + suffixNumber + "MonitorAspect.aj");
                } while(aspectFile.exists());
                
                aspectName = "MultiSpec_" + suffixNumber;
            }
            options.aspectname = aspectName;
        }
        MOPProcessor processor = new MOPProcessor(aspectName);
        MOPNameSpace.init();
        ArrayList<MOPSpecFile> specs = new ArrayList<MOPSpecFile>();
        for(File file : specFiles){
            //System.out.println(file);
            String specStr = SpecExtractor.process(file);
            MOPSpecFile spec =  SpecExtractor.parse(specStr);
            if (options.translate2RV) {
                writeFile(processor.translate2RV(spec), file.getAbsolutePath(), ".rvm");
            }
            specs.add(spec);
        }
        MOPSpecFile combinedSpec = SpecCombiner.process(specs);
        String output = processor.process(combinedSpec);
        writeCombinedAspectFile(output, aspectName);
    }
    
    /**
     * Write Java source code to a file.
     * @param javaContent The Java source.
     * @param location The location of the file to write it to.
     * @throws MOPException If something goes wrong writing the file.
     */
    protected static void writeJavaFile(String javaContent, String location) throws MOPException {
        if ((javaContent == null) || (javaContent.length() == 0))
            throw new MOPException("Nothing to write as a java file");
        if (!Tool.isJavaFile(location))
            throw new MOPException(location + "should be a Java file!");

        FileWriter f = null;
        try {
            f = new FileWriter(location);
            f.write(javaContent);
        } catch (Exception e) {
            throw new MOPException("Failed to write Java file", e);
        } finally {
            if(f != null) {
                try {
                    f.close();
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    /**
     * Write AspectJ code to a file.
     * @param aspectContent The AspectJ code to write.
     * @param aspectName The name of the aspect, used to determine the file.
     * @throws MOPException If something goes wrong writing the file.
     */
    protected static void writeCombinedAspectFile(String aspectContent, String aspectName) 
            throws MOPException {
        if (aspectContent == null || aspectContent.length() == 0)
            return;
        
        final String path = options.outputDir.getAbsolutePath() + File.separator + aspectName + "MonitorAspect.aj";
        FileWriter f = null;
        try {
            f = new FileWriter(path);
            f.write(aspectContent);
        } catch (Exception e) {
            throw new MOPException("Failed to write Combined Aspect", e);
        } finally {
            if(f != null) {
                try {
                    System.out.println("Closing " + path);
                    f.close();
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        System.out.println(" " + aspectName + "MonitorAspect.aj is generated");
    }
    
    /**
     * Write any sort of content to a file.
     * @param content The text to write into the file.
     * @param location The file to write into.
     * @param suffix The new file extension to use for the file.
     * @throws MOPException If something goes wrong in generating the file.
     */
    protected static void writeFile(String content, String location, String suffix)
            throws MOPException {
        if (content == null || content.length() == 0)
            return;
        
        int i = location.lastIndexOf(File.separator);
        String filePath = location.substring(0, i + 1) + Tool.getFileName(location) + suffix;
        FileWriter f = null;
        try {
            f = new FileWriter(filePath);
            f.write(content);
        } catch (Exception e) {
            throw new MOPException(e);
        } finally {
            if(f != null) {
                try {
                    f.close();
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        if (suffix.equals(".rvm")) {
            listRVMFiles.add(filePath);
        }
        System.out.println(" " + Tool.getFileName(location) + suffix + " is generated");
    }
    
    /**
     * Aggregate MOP files and recursively search directories for more MOP files.
     * @param files Path array. Files are added directly, directories are searched recursively.
     * @param path A common prefix for all the paths in {@code files}.
     */
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
    
    /**
     * Parse all MOP files into one or more RVM files.
     * @param files Array of MOP file and directory paths.
     * @param path Common prefix to all the paths in {@code files}.
     */
    public static void process(String[] files, String path) throws MOPException {
        ArrayList<File> specFiles = collectFiles(files, path);
        
        if(options.aspectname != null && files.length > 1){
            options.merge = true;
        }
        
        if (options.merge) {
            System.out.println("-Processing " + specFiles.size()
            + " specification(s)");
            processMultipleFiles(specFiles);
            String javaFile = options.outputDir.getAbsolutePath() + File.separator
            + options.aspectname + "RuntimeMonitor.java";
            String ajFile = options.outputDir.getAbsolutePath() + File.separator
            + options.aspectname + "MonitorAspect.aj";
            String combinerArgs[] = new String[2];
            combinerArgs[0] = javaFile;
            combinerArgs[1] = ajFile;
            
            listFilePairs.add(combinerArgs);
            
        } else {
            for (File file : specFiles) {
                boolean needResetAspectName = options.aspectname == null;
                String location = options.outputDir == null ? file.getAbsolutePath() :
                    options.outputDir.getAbsolutePath() + File.separator + file.getName();
                System.out.println("-Processing " + file.getPath());
                if (Tool.isSpecFile(file.getName())) {
                    processSpecFile(file, location);
                } else if (Tool.isJavaFile(file.getName())) {
                    processJavaFile(file, location);
                }
                
                File combineDir = options.outputDir == null ? file.getAbsoluteFile()
                .getParentFile() : options.outputDir;
                
                String javaFile = combineDir.getAbsolutePath() + File.separator
                + options.aspectname + "RuntimeMonitor.java";
                String ajFile = combineDir.getAbsolutePath() + File.separator
                + options.aspectname + "MonitorAspect.aj";
                String combinerArgs[] = new String[2];
                combinerArgs[0] = javaFile;
                combinerArgs[1] = ajFile;
                
                listFilePairs.add(combinerArgs);
                
                if (needResetAspectName) {
                    options.aspectname = null;
                }
            }
        }
    }
    
    /**
     * Handle one or multiple input files and produce .rvm files.
     * @param files a list of file names.
     */
    public static void process(List<String> files) throws MOPException {
        if(options.outputDir != null && !options.outputDir.exists())
            throw new MOPException("The output directory, " + options.outputDir.getPath() +
                " does not exist.");
        
        process(files.toArray(new String[0]), "");
    }
    
    // PM
    /**
     * Print command-line options available for controlling JavaMOP, and through it RV-Monitor.
     */
//    public static void print_help() {
//        System.out.println("Usage: java [-cp javmaop_classpath] javamop.JavaMOPMain [-options] " +
//            "files");
//        System.out.println("");
//        System.out.println("where options include:");
//        System.out.println(" Options enabled by default are prefixed with \'+\'");
//        System.out.println("    -h -help\t\t\t  print this help message");
//        System.out.println("    -v | -verbose\t\t  enable verbose output");
//        System.out.println("    -debug\t\t\t  enable verbose error message");
//        System.out.println();
//
//        System.out.println("    -local\t\t\t+ use local logic engine");
//        System.out.println("    -remote\t\t\t  use default remote logic engine");
//        System.out.println("\t\t\t\t  " + Configuration.getServerAddr());
//        System.out.println("\t\t\t\t  (You can change the default address");
//        System.out.println("\t\t\t\t   in javamop/config/remote_server_addr.properties)");
//        System.out.println("    -remote:<server address>\t  use remote logic engine");
//        System.out.println();
//
//        System.out.println("    -d <output path>\t\t  select directory to store output files");
//        System.out.println("    -n | -aspectname <aspect name>\t  use the given aspect name " +
//            "instead of source code name");
//        System.out.println();
//
//        System.out.println("    -showevents\t\t\t  show every event/handler occurrence");
//        System.out.println("    -showhandlers\t\t\t  show every handler occurrence");
//        System.out.println();
//
//        System.out.println("    -s | -statistics\t\t  generate monitor with statistics");
//        System.out.println("    -noopt1\t\t\t  don't use the enable set optimization");
//        System.out.println("    -javalib\t\t\t  generate a java library rather than an " +
//            "AspectJ file");
//        System.out.println();
//
//        System.out.println("    -aspect:\"<command line>\"\t  compile the result right after " +
//            "it is generated");
//        System.out.println();
//    }
    
    /**
     * Initialize JavaMOP with the given command-line parameters, process the given MOP files
     * into RVM files, run RV-Monitor on the MOP files, and optionally postprocess the output.
     * @param args Configuration options and input files for JavaMOP.
     */
    public static void main(String[] args) {
        options = new JavaMOPOptions();
        JCommander jc = new JCommander(options,args);

        if (args.length == 0 || options.files.size() == 0){
            jc.usage();
            System.exit(1);
        }

        ClassLoader loader = JavaMOPMain.class.getClassLoader();
        String mainClassPath = loader.getResource("javamop/JavaMOPMain.class").toString();
        if (mainClassPath.endsWith(".jar!/javamop/JavaMOPMain.class") && 
                mainClassPath.startsWith("jar:")) {
            isJarFile = true;
            
            jarFilePath = mainClassPath.substring("jar:file:".length(), mainClassPath.length() - 
                "!/javamop/JavaMOPMain.class".length());
            jarFilePath = Tool.polishPath(jarFilePath);
        }
        
//        int i = 0;
//        String files = "";
        
//        while (i < args.length) {
//            if (args[i].compareTo("-h") == 0 || args[i].compareTo("-help") == 0) {
//                print_help();
//                return;
//            }
//
//            if (args[i].compareTo("-d") == 0) {
//                i++;
//                outputDir = new File(args[i]);
//            } else if (args[i].compareTo("-local") == 0) {
//            } else if (args[i].compareTo("-remote") == 0) {
//            } else if (args[i].startsWith("-remote:")) {
//            } else if (args[i].compareTo("-v") == 0 || args[i].compareTo("-verbose") == 0) {
//                MOPProcessor.verbose = true;
//            } else if (args[i].compareTo("-javalib") == 0) {
//                toJavaLib = true;
//            } else if (args[i].compareTo("-debug") == 0) {
//                JavaMOPMain.debug = true;
//            } else if (args[i].compareTo("-noopt1") == 0) {
//                JavaMOPMain.noopt1 = true;
//            } else if (args[i].compareTo("-s") == 0 || args[i].compareTo("-statistics") == 0) {
//                JavaMOPMain.statistics = true;
//            } else if (args[i].compareTo("-s2") == 0 || args[i].compareTo("-statistics2") == 0) {
//                JavaMOPMain.statistics2 = true;
//            } else if (args[i].compareTo("-n") == 0 || args[i].compareTo("-aspectname") == 0) {
//                i++;
//                JavaMOPMain.aspectname = args[i];
//                JavaMOPMain.specifiedAJName = true;
//            } else if (args[i].compareTo("-showhandlers") == 0) {
//                if (JavaMOPMain.logLevel < JavaMOPMain.HANDLERS)
//                    JavaMOPMain.logLevel = JavaMOPMain.HANDLERS;
//            } else if (args[i].compareTo("-showevents") == 0) {
//                if (JavaMOPMain.logLevel < JavaMOPMain.EVENTS)
//                    JavaMOPMain.logLevel = JavaMOPMain.EVENTS;
//            } else if (args[i].compareTo("-dacapo") == 0) {
//                JavaMOPMain.dacapo = true;
//            } else if (args[i].compareTo("-dacapo2") == 0) {
//                JavaMOPMain.dacapo2 = true;
//            } else if (args[i].compareTo("-silent") == 0) {
//                JavaMOPMain.silent = true;
//            } else if (args[i].compareTo("-merge") == 0) {
//                JavaMOPMain.merge = true;
//            } else if (args[i].compareTo("-inline") == 0) {
//                JavaMOPMain.inline = true;
//            } else if (args[i].compareTo("-noadvicebody") == 0) {
//                JavaMOPMain.empty_advicebody = true;
//            } else if (args[i].compareTo("-scalable") == 0) {
//                JavaMOPMain.scalable = true;
//            } else if (args[i].compareTo("-translate2RV") == 0) {
//                JavaMOPMain.translate2RV = true;
//            } else if (args[i].compareTo("-keepRVFiles") == 0) {
//                JavaMOPMain.keepRVFiles = true;
//            } else if("--agent".equals(args[i])) {
//                JavaMOPMain.merge = true;
//                JavaMOPMain.generateAgent = true;
//                JavaMOPMain.keepRVFiles = true;
//            } else if("--baseaspect".equals(args[i])) {
//                i++;
//                JavaMOPMain.baseAspect = new File(args[i]);
//            } else {
//                if (files.length() != 0)
//                    files += ";";
//                files += args[i];
//            }
//            ++i;
//        }
        
//        if (files.length() == 0) {
//            print_help();
//            return;
//        }
        
        boolean tempOutput = options.generateAgent && options.outputDir == null;

        if(tempOutput) {
            tempOutput = true;
            try {
                options.outputDir = Files.createTempDirectory(new File(".").toPath(), "output").toFile();
                options.outputDir.deleteOnExit();
            } catch(IOException ioe) {
                ioe.printStackTrace();
                options.generateAgent = false;
            }
        }


        // Generate .rvm files and .aj files
        try {
            process(options.files);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (options.debug)
                e.printStackTrace();
        }
        
        // replace mop with rvm and call rv-monitor
        List<String> rvArgs = new ArrayList<String>();
        for (int j = 0; j < args.length; j++) {
            if (args[j].compareTo("-keepRVFiles") == 0) {
                // Don't pass keepRVFiles to rvmonitor\
            } else if("--agent".equals(args[j])) {
                rvArgs.add("-merge");
            } else if("--baseaspect".equals(args[j])) {
                j++;
            } else {
                rvArgs.add(args[j].replaceAll("\\.mop", "\\.rvm"));
            }
        }
        if(tempOutput) {
            rvArgs.add("-d");
            rvArgs.add(options.outputDir.getAbsolutePath());
        }
        
        Main.main(rvArgs.toArray(new String[0]));

        if(options.generateAgent) {
            try {
                GenerateAgent.generate(options.outputDir, options.aspectname,
                    options.baseAspect);
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
                if (!options.keepRVFiles) {
                    boolean deleted = javaFile.delete();
                    if (!deleted) {
                        System.err.println("Failed to delete java file: "
                        + filePair[0]);
                    }
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        
        for (String rvmFilePath : listRVMFiles) {
            File rvmFile = new File(rvmFilePath);
            try {
                if (!options.keepRVFiles) {
                    boolean deleted = rvmFile.delete();
                    if (!deleted) {
                        System.err.println("Failed to delete java file: " + rvmFilePath);
                    }
                }
                
            } catch (SecurityException e) {
                  e.printStackTrace();
            }
        }
        
        if(tempOutput) {
            try {
                GenerateAgent.deleteDirectory(options.outputDir.toPath());
            } catch(IOException e) {
                e.printStackTrace();
                System.err.println("Failed to remove temporary files.");
            }
        }
    }
}
