// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
/**
 * @author fengchen, Dongyun Jin, Patrick Meredith, Michael Ilseman
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

package javamop;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.runtimeverification.rvmonitor.java.rvj.Main;
import javamop.commandline.JavaMOPOptions;
import javamop.commandline.SpecFilter;

import javamop.parser.ParserServiceImpl;

import javamop.parser.ast.MOPSpecFile;
import javamop.util.FileCombiner;
import javamop.util.Tool;

/**
 * Entry point class for the JavaMOP program.
 */
public final class JavaMOPMain {

    public static JavaMOPOptions options;

    private static ParserService PARSERSERVICE;

    /**
     * Private to prevent instantiation.
     */
    private JavaMOPMain() {
        //get an instance of ParserServiceImpl.
        //that is the entry point for using the services provided by parser component.
        PARSERSERVICE=new ParserServiceImpl();
    }

    public static boolean specifiedAJName = false;
    public static boolean isJarFile = false;
    public static String jarFilePath = null;
    
    public static final int NONE = 0;

    public static boolean empty_advicebody = false;

    public static boolean inline = false;

    private static final List<String []> listFilePairs = new ArrayList<String []>();
    private static final List<String> listRVMFiles = new ArrayList<String>();

    private static final String RVM_FILE_SUFFIX = ".rvm";
    private static final String AJ_FILE_SUFFIX = "MonitorAspect.aj";
    
    /**
     * Determine where to place the JavaMOP output files, if it is not decided elsewhere. If all
     * the parameter files are in the same directory, use that directory. Otherwise, use the
     * directory that JavaMOP is executing in.
     * @param specFiles The specifications the program is being run on.
     * @return The directory to place output files in.
     * @throws javamop.parser.MOPException If something goes wrong finding the file locations.
     */
    static private File getTargetDir(ArrayList<File> specFiles) throws ParserService.MOPExceptionImpl {
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
    public static void processJavaFile(File file, String location) throws ParserService.MOPExceptionImpl {
        MOPNameSpace.init();
        String specStr = PARSERSERVICE.process(file);
        MOPSpecFile spec = PARSERSERVICE.parse2MOPSpecFile(specStr);
        
        if (options.aspectname == null) {
            options.aspectname = Tool.getFileName(file.getAbsolutePath());
        }

        PARSERSERVICE.setUpMOPProcessor(options.aspectname);
        
        writeFile(PARSERSERVICE.generateAJFile(spec), location, AJ_FILE_SUFFIX);
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
    public static void processSpecFile(File file, String location) throws ParserService.MOPExceptionImpl {
        MOPNameSpace.init();
        String specStr = PARSERSERVICE.process(file);
        MOPSpecFile spec =  PARSERSERVICE.parse2MOPSpecFile(specStr);
        
        if (options.aspectname == null) {
            options.aspectname = Tool.getFileName(file.getAbsolutePath());
        }

        PARSERSERVICE.setUpMOPProcessor(options.aspectname);
        
        writeFile(PARSERSERVICE.generateRVFile(spec), file.getAbsolutePath(), RVM_FILE_SUFFIX);

        writeFile(PARSERSERVICE.generateAJFile(spec), location, AJ_FILE_SUFFIX);
    }
    
    /**
     * Process multiple specification files, either each to a corresponding RVM/AJ file or merging
     * all of them together into two combined RVM/AJ files.
     * @param specFiles All the specifications to consider.
     * @throws javamop.ParserService.MOPExceptionImpl If something goes wrong in conversion.
     */
    public static void processMultipleFiles(ArrayList<File> specFiles) throws ParserService.MOPExceptionImpl {
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
                        "MultiSpec_" + suffixNumber + AJ_FILE_SUFFIX);
                } while(aspectFile.exists());
                aspectName = "MultiSpec_" + suffixNumber;
            }
            options.aspectname = aspectName;
        }

        PARSERSERVICE.setUpMOPProcessor(aspectName);
        MOPNameSpace.init();
        ArrayList<MOPSpecFile> specs = new ArrayList<MOPSpecFile>();
        for(File file : specFiles){
            //System.out.println(file);
            String specStr = PARSERSERVICE.process(file);
            MOPSpecFile spec =  PARSERSERVICE.parse2MOPSpecFile(specStr);
            writeFile(PARSERSERVICE.generateRVFile(spec), file.getAbsolutePath(), RVM_FILE_SUFFIX);
            specs.add(spec);
        }
        MOPSpecFile combinedSpec = FileCombiner.combineSpecFiles(specs);
        writeCombinedAspectFile(PARSERSERVICE.generateAJFile(combinedSpec), aspectName);
    }

    /**
     * Write AspectJ code to a file.
     * @param aspectContent The AspectJ code to write.
     * @param aspectName The name of the aspect, used to determine the file.
     * @throws javamop.ParserService.MOPExceptionImpl If something goes wrong writing the file.
     */
    protected static void writeCombinedAspectFile(String aspectContent, String aspectName) 
            throws ParserService.MOPExceptionImpl {
        if (aspectContent == null || aspectContent.length() == 0)
            return;
        
        final String path = options.outputDir.getAbsolutePath() + File.separator +
                aspectName + AJ_FILE_SUFFIX;
        FileWriter f = null;
        try {
            f = new FileWriter(path);
            f.write(aspectContent);
        } catch (Exception e) {
            throw PARSERSERVICE.generateMOPException("Failed to write Combined Aspect", e);
        } finally {
            if(f != null) {
                try {
                    f.close();
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        System.out.println(" " + aspectName + AJ_FILE_SUFFIX+ " is generated");
    }
    
    /**
     * Write any sort of content to a file.
     * @param content The text to write into the file.
     * @param location The file to write into.
     * @param suffix The new file extension to use for the file.
     * @throws javamop.ParserService.MOPExceptionImpl If something goes wrong in generating the file.
     */
    protected static void writeFile(String content, String location, String suffix)
            throws ParserService.MOPExceptionImpl {
        if (content == null || content.length() == 0)
            return;
        
        int i = location.lastIndexOf(File.separator);
        String filePath = location.substring(0, i + 1) + Tool.getFileName(location) + suffix;
        FileWriter f = null;
        try {
            f = new FileWriter(filePath);
            f.write(content);
        } catch (Exception e) {
            throw PARSERSERVICE.generateMOPException(e);
        } finally {
            if(f != null) {
                try {
                    f.close();
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        if (suffix.equals(RVM_FILE_SUFFIX)) {
            listRVMFiles.add(filePath);
        }
        System.out.println(" " + Tool.getFileName(location) + suffix + " is generated");
    }
    
    /**
     * Aggregate MOP files and recursively search directories for more MOP files.
     * @param files Path array. Files are added directly, directories are searched recursively.
     * @param path A common prefix for all the paths in {@code files}.
     */
    public static ArrayList<File> collectFiles(String[] files, String path) throws ParserService.MOPExceptionImpl {
        ArrayList<File> ret = new ArrayList<File>();
        
        for (String file : files) {
            String fPath = path.length() == 0 ? file : path + File.separator + file;
            File f = new File(fPath);
            
            if (!f.exists()) {
                throw PARSERSERVICE.generateMOPException("[Error] Target file, " + file + ", doesn't exsit!");
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
    public static void process(String[] files, String path) throws ParserService.MOPExceptionImpl {
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
            + options.aspectname + AJ_FILE_SUFFIX;
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
                + options.aspectname + AJ_FILE_SUFFIX;
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
    public static void process(List<String> files) throws ParserService.MOPExceptionImpl {
        if(options.outputDir != null && !options.outputDir.exists())
            throw PARSERSERVICE.generateMOPException("The output directory, " + options.outputDir.getPath() +
                " does not exist.");
        
        process(files.toArray(new String[0]), "");
    }
    
    /**
     * Initialize JavaMOP with the given command-line parameters, process the given MOP files
     * into RVM files, run RV-Monitor on the MOP files, and optionally postprocess the output.
     * @param args Configuration options and input files for JavaMOP.
     */
    public static void main(String[] args) {
        options = new JavaMOPOptions();
        JCommander jc;
        try {
            jc = new JCommander(options, args);
        } catch (ParameterException pe) {
            System.out.println(pe.getMessage());
            return;
        }
        jc.setProgramName("javamop");

        handleOptions(options, args, jc);

        ClassLoader loader = JavaMOPMain.class.getClassLoader();
        String mainClassPath = loader.getResource("javamop/JavaMOPMain.class").toString();
        if (mainClassPath.endsWith(".jar!/javamop/JavaMOPMain.class") && 
                mainClassPath.startsWith("jar:")) {
            isJarFile = true;
            
            jarFilePath = mainClassPath.substring("jar:file:".length(), mainClassPath.length() - 
                "!/javamop/JavaMOPMain.class".length());
            jarFilePath = Tool.polishPath(jarFilePath);
        }

        boolean tempOutput = options.generateAgent && options.outputDir == null;

        if(tempOutput) {
            // this line is redundant
//            tempOutput = true;
            try {
                options.outputDir =
                        Files.createTempDirectory(new File(".").toPath(), "output").toFile();
                options.outputDir.deleteOnExit();
            } catch(IOException ioe) {
                ioe.printStackTrace();
                options.generateAgent = false;
            }
        }

        SpecFilter filter = null;
        if (options.usedb) {
            try {
                filter = new SpecFilter();
                options.files = new ArrayList<String>();
                options.files.add(filter.filter());
            } catch (Exception e) {
                e.printStackTrace();
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
            if (args[j].compareTo("-keepRVFiles") == 0 || args[j].compareTo("-usedb") == 0) {
                // Don't pass keepRVFiles to rvmonitor\
            } else if("-agent".equals(args[j])) {
                rvArgs.add("-merge");
            } else if("-baseaspect".equals(args[j])) {
                j++;
            } else {
                rvArgs.add(args[j].replaceAll("\\.mop", "\\.rvm"));
            }
        }
        if(tempOutput) {
            rvArgs.add("-d");
            rvArgs.add(options.outputDir.getAbsolutePath());
        }

        if (options.usedb){
            rvArgs.add(SpecFilter.specDirPath);
        }
        
        Main.main(rvArgs.toArray(new String[0]));

        if(options.generateAgent) {
            try {
                AgentGenerator.generate(options.outputDir, options.aspectname,
                        options.baseAspect);
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
        }

        // Call FileCombiner here to combine these two
        for (String[] filePair : listFilePairs) {
            FileCombiner.combineAJFiles(filePair);
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

        cleanup(tempOutput, filter);
    }

    /**
     * This method will cleans up temporary files used during agent generation
     * @param tempOutput temporary directory used to hold agent generation artifacts
     * @param filter  a SpecFilter which holds, among other things, the directory
     *                where specs from property-db are stored
     */
    private static void cleanup(boolean tempOutput, SpecFilter filter) {
        if(tempOutput) {
            try {
                AgentGenerator.deleteDirectory(options.outputDir.toPath());
            } catch(IOException e) {
                e.printStackTrace();
                System.err.println("Failed to remove temporary files.");
            }
        }

        if (filter != null && filter.isCleanup()) {
            filter.cleanup();
        }
    }

    /**
     * This method sets some field and other (dependent) options, based on the
     * flags that the user called JavaMOP with.
     *
     * @param options  The object holding the options that the user called
     *                 JavaMOP with
     */
    private static void handleOptions(JavaMOPOptions options, String[] args, JCommander jc) {
        if (args.length == 0 || (options.files.size() == 0 && !options.usedb)){
            jc.usage();
            System.exit(1);
        }

        if (options.verbose) {
            PARSERSERVICE.setMOPProcessorToVerboseMode();
        }

        if (options.aspectname != null) {
            JavaMOPMain.specifiedAJName = true;
        }

        if (options.generateAgent) {
            options.merge = true;
            options.keepRVFiles = true;
        }

        if (options.noadvicebody){
            JavaMOPMain.empty_advicebody = true;
        }

        if ((options.usedb) && !options.generateAgent){
            throw new IllegalArgumentException("The \"-usedb\" option should only be set in " +
                    "conjunction with the \"-agent\" option (which was not set in this case)");
        }
    }
}