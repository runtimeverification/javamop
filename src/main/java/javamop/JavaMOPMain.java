// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
/**
 * @author fengchen, Dongyun Jin, Patrick Meredith, Michael Ilseman
 * <p/>
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

package javamop;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import javamop.output.MOPProcessor;
import javamop.parser.SpecExtractor;
import javamop.parser.ast.MOPSpecFile;
import javamop.specfiltering.SpecFilter;
import javamop.util.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Entry point class for the JavaMOP program.
 */
public final class JavaMOPMain {

    public static JavaMOPOptions options;

    /**
     * Private to prevent instantiation.
     */
    private JavaMOPMain() {

    }

    public static boolean specifiedAJName = false;
    public static boolean isJarFile = false;
    public static String jarFilePath = null;

    public static boolean empty_advicebody = false;

    private static final List<String[]> listFilePairs = new ArrayList<String[]>();
    private static final List<String> listRVMFiles = new ArrayList<String>();

    private static final String RVM_FILE_SUFFIX = ".rvm";
    private static final String AJ_FILE_SUFFIX = "MonitorAspect.aj";

    /**
     * Because the JavaMOP's main method may be invoked multiple times to handle
     * multiple input files, we need to reset the static variables at the beginning
     * of execution.
     */
    private static void init() {
//        specifiedAJName = false;
//        isJarFile = false;
//        jarFilePath = null;
//        empty_advicebody = false;
        listFilePairs.clear();
        listRVMFiles.clear();
    }

    /**
     * Determine where to place the JavaMOP output files, if it is not decided elsewhere. If all
     * the parameter files are in the same directory, use that directory. Otherwise, use the
     * directory that JavaMOP is executing in.
     *
     * @param specFiles The specifications the program is being run on.
     * @return The directory to place output files in.
     * @throws javamop.util.MOPException If something goes wrong finding the file locations.
     */
    static private File getTargetDir(ArrayList<File> specFiles) throws MOPException {
        if (options.outputDir != null) {
            return options.outputDir;
        }

        boolean sameDir = true;
        File parentFile = null;

        for (File file : specFiles) {
            if (parentFile == null) {
                parentFile = file.getAbsoluteFile().getParentFile();
            } else {
                if (file.getAbsoluteFile().getParentFile().equals(parentFile)) {
                    continue;
                } else {
                    sameDir = false;
                    break;
                }
            }
        }

        if (sameDir) {
            return parentFile;
        } else {
            return new File(".");
        }
    }

    /**
     * Process a specification file to generate an aspectj file. The path argument should be an
     * existing specification file name. The location argument should contain the original file
     * name, But it may have a different directory.
     *
     * @param file     an absolute path of a specification file
     * @param location an absolute path for result file
     */
    public static void processSpecFile(File file, String location) throws MOPException, IOException {
        MOPNameSpace.init();
        MOPSpecFile spec = SpecExtractor.parse(file);

        if (options.aspectname == null) {
            options.aspectname = Tool.getFileName(file.getAbsolutePath());
        }

        MOPProcessor processor = new MOPProcessor(options.aspectname);

        // We shouldn't use the name bound to the -n option for renaming the the .mop 
        // file to .rvm before passing to rv-monitor.
        // the input file to rv-monitor is the .mop file, whose extension has been replaced by .rvm
        // NOTE: If we separate rv-monitor from JavaMOP completely, we neeed to revisit this.
        writeFile(processor.generateRVFile(spec), location, RVM_FILE_SUFFIX, null);

        writeFile(processor.generateAJFile(spec), location, AJ_FILE_SUFFIX, options.aspectname);
    }

    /**
     * Process multiple specification files, either each to a corresponding RVM/AJ file or merging
     * all of them together into two combined RVM/AJ files.
     *
     * @param specFiles All the specifications to consider.
     * @throws MOPException If something goes wrong in conversion.
     */
    public static void processMultipleFiles(ArrayList<File> specFiles) throws MOPException, IOException {
        String aspectName;

        if (options.outputDir == null) {
            options.outputDir = getTargetDir(specFiles);
        }

        if (options.aspectname != null) {
            aspectName = options.aspectname;
        } else {
            if (specFiles.size() == 1) {
                aspectName = Tool.getFileName(specFiles.get(0).getAbsolutePath());
                if(options.emop) {
                    int suffixNumber = 0;
                    // generate auto name like 'MultiMonitorAspect.aj'
                    File aspectFile;
                    do {
                        suffixNumber++;
                        aspectFile = new File(options.outputDir.getAbsolutePath() + File.separator +
                            "JavaMOPAgent" + suffixNumber + AJ_FILE_SUFFIX);
                    } while (aspectFile.exists());
                    String mergeName = "MultiSpec_" + suffixNumber;
                    options.aspectname = mergeName;
                } else {
                    options.aspectname = aspectName;
                }
            } else {
                int suffixNumber = 0;
                // generate auto name like 'MultiMonitorAspect.aj'
                File aspectFile;
                do {
                    suffixNumber++;
                    aspectFile = new File(options.outputDir.getAbsolutePath() + File.separator +
                            "JavaMOPAgent" + suffixNumber + AJ_FILE_SUFFIX);
                } while (aspectFile.exists());
                aspectName = "MultiSpec_" + suffixNumber;
                options.aspectname = aspectName;
            }
        }

        MOPProcessor processor = new MOPProcessor(aspectName);
        MOPNameSpace.init();
        ArrayList<MOPSpecFile> specs = new ArrayList<MOPSpecFile>();
        for (File file : specFiles) {
            //System.out.println(file);
            MOPSpecFile spec = SpecExtractor.parse(file);
            // -n option does not make sense when input are multiple files
            writeFile(processor.generateRVFile(spec), file.getAbsolutePath(), RVM_FILE_SUFFIX, null);
            specs.add(spec);
        }
        MOPSpecFile combinedSpec = FileCombiner.combineSpecFiles(specs);
        writeCombinedAspectFile(processor.generateAJFile(combinedSpec), aspectName);
    }

    /**
     * Write AspectJ code to a file.
     *
     * @param aspectContent The AspectJ code to write.
     * @param aspectName    The name of the aspect, used to determine the file.
     * @throws MOPException If something goes wrong writing the file.
     */
    protected static void writeCombinedAspectFile(String aspectContent, String aspectName)
            throws MOPException {
        if (aspectContent == null || aspectContent.length() == 0)
            return;

        final String path = options.outputDir.getAbsolutePath() + File.separator +
                aspectName + AJ_FILE_SUFFIX;
        FileWriter f = null;
        try {
            f = new FileWriter(path);
            f.write(aspectContent);
        } catch (Exception e) {
            throw new MOPException("Failed to write Combined Aspect", e);
        } finally {
            if (f != null) {
                try {
                    f.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        System.out.println(" " + aspectName + AJ_FILE_SUFFIX + " is generated");
    }

    /**
     * Write any sort of content to a file.
     *
     * @param content  The text to write into the file.
     * @param location The file to write into.
     * @param suffix   The new file extension to use for the file.
     * @param name     Name for the generated file. If null, use the same name as the input file
     * @throws MOPException If something goes wrong in generating the file.
     */
    protected static void writeFile(String content, String location, String suffix, String name)
            throws MOPException {
        if (content == null || content.length() == 0)
            return;

        int i = location.lastIndexOf(File.separator);
        String filePath = location.substring(0, i + 1) +
                (name == null ? Tool.getFileName(location) : name) + suffix;
        FileWriter f = null;
        try {
            f = new FileWriter(filePath);
            f.write(content);
        } catch (Exception e) {
            throw new MOPException(e);
        } finally {
            if (f != null) {
                try {
                    f.close();
                } catch (IOException ioe) {
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
     *
     * @param files Path array. Files are added directly, directories are searched recursively.
     * @param path  A common prefix for all the paths in {@code files}.
     */
    public static ArrayList<File> collectFiles(String[] files, String path) throws MOPException, IOException {
        ArrayList<File> ret = new ArrayList<File>();

        for (String file : files) {
            String fPath = path.length() == 0 ? file : path + File.separator + file;
            File f = new File(fPath);

            if (!f.exists()) {
                throw new MOPException("[Error] Target file, " + file + ", doesn't exsit!");
            } else if (f.isDirectory()) {
                if (options.outputDir != null) {
                    Path tarOutputDirPath = options.outputDir.toPath();
                    Files.walkFileTree(f.toPath(), new MOPFileVisitor(tarOutputDirPath));
                } else {
                    ret.addAll(collectFiles(f.list(), f.getAbsolutePath()));
                }
            } else {
                if (Tool.isSpecFile(file)) {
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
     *
     * @param files Array of MOP file and directory paths.
     * @param path  Common prefix to all the paths in {@code files}.
     */
    public static void process(String[] files, String path) throws MOPException, IOException {
        ArrayList<File> specFiles = collectFiles(files, path);
        if (options.aspectname != null && files.length > 1) {
            options.merge = true;
        }

        init();
        //The spec files in directories have already been handled, so reset env
        // to avoid deleting the same file twice...

        //ensure every spec file has a valid name (according to Java's identifier naming convention)
        areValidNames(specFiles);

        if (options.merge || options.emop) {
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

    //If any input mop file does not have a valid name, then exit.
    private static void areValidNames(ArrayList<File> files) {
        for (int i = 0; i < files.size(); i++) {
            String curName = Tool.getFileName(files.get(i).getPath());
            if (!curName.matches("[a-zA-Z_][A-Za-z0-9_]*")) {
                System.err.println("The mop name " + curName + " is not valid!");
                System.err.println("A valid mop name should respect Java's naming convention, " +
                        "described by the regular expression (enclosed inside the double quotes)" +
                        " \"[a-zA-Z_][A-Za-z0-9_]*\"");
                System.exit(1);
            }
        }
    }

    /**
     * Handle one or multiple input files and produce .rvm files.
     *
     * @param files a list of file names.
     */
    public static void process(List<String> files) throws MOPException, IOException {
        if (options.outputDir != null && !options.outputDir.exists())
            throw new MOPException("The output directory, " + options.outputDir.getPath() +
                    " does not exist.");

        process(files.toArray(new String[0]), "");
    }

    /**
     * Initialize JavaMOP with the given command-line parameters, process the given MOP files
     * into RVM files, run RV-Monitor on the MOP files, and optionally postprocess the output.
     *
     * @param args Configuration options and input files for JavaMOP.
     */
    public static void main(String[] args) {
        init();

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
        // Generate .rvm files and .aj files
        try {
            process(options.files);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (options.debug)
                e.printStackTrace();
        }
    }

    /**
     * This method will cleans up temporary files used during agent generation
     *
     * @param tempOutput temporary directory used to hold agent generation artifacts
     * @param filter     a SpecFilter which holds, among other things, the directory
     *                   where specs from property-db are stored
     */
    private static void cleanup(boolean tempOutput, SpecFilter filter) {
        if (tempOutput) {
            try {
                Tool.deleteDirectory(options.outputDir.toPath());
            } catch (IOException e) {
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
     * @param options The object holding the options that the user called
     *                JavaMOP with
     */
    private static void handleOptions(JavaMOPOptions options, String[] args, JCommander jc) {
        if (args.length == 0 || (options.files.size() == 0)) {
            jc.usage();
            System.exit(1);
        }

        if (options.verbose) {
            MOPProcessor.verbose = true;
        }

        if (options.aspectname != null) {
            JavaMOPMain.specifiedAJName = true;
        }

        if (options.noadvicebody) {
            JavaMOPMain.empty_advicebody = true;
        }
    }
}
