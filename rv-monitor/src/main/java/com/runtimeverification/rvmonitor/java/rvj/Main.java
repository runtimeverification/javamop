/**
 * @author fengchen, Dongyun Jin, Patrick Meredith, Michael Ilseman
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

package com.runtimeverification.rvmonitor.java.rvj;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.runtimeverification.rvmonitor.java.rvj.logicclient.LogicRepositoryConnector;
import com.runtimeverification.rvmonitor.java.rvj.output.CodeGenerationOption;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.RVMSpecFile;
import com.runtimeverification.rvmonitor.util.RVMException;
import com.runtimeverification.rvmonitor.util.Tool;

public class Main {
    public static RVMOptions options;

    /**
     * The target directory for outputting the results produced from some
     * specification files. If outputDir is already set, use that. If
     * the input files are all in the same directory, return that directory.
     * Otherwise, return the current directory.
     *
     * @param specFiles
     *            The specification files used in the input.
     * @return The place to put the output files.
     */
    static private File getTargetDir(ArrayList<File> specFiles) {
        if (options.outputDir != null) {
            return options.outputDir;
        }

        boolean sameDir = true;
        File parentFile = null;

        for (File file : specFiles) {
            if (parentFile == null) {
                parentFile = file.getAbsoluteFile().getParentFile();
            } else {
                if (!file.getAbsoluteFile().getParentFile().equals(parentFile)) {
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
     * Process a specification file to generate a runtime monitor file. The file
     * argument should be an initialized file object. The location argument
     * should contain the original file name, But it may have a different
     * directory.
     *
     * @param file
     *            a File object containing the specification file
     */
    public static void processSpecFile(File file)
            throws RVMException {
        RVMNameSpace.init();
        String specStr = SpecExtractor.process(file);
        RVMSpecFile spec = SpecExtractor.parse(specStr);

        if (options.outputDir == null) {
            ArrayList<File> specList = new ArrayList<>();
            specList.add(file);
            options.outputDir = getTargetDir(specList);
        }

        String outputName = options.name == null ? Tool.getFileName(file
                .getAbsolutePath()) : options.name;

        RVMProcessor processor = new RVMProcessor(outputName);

        String output = processor.process(spec);
        writeCombinedOutputFile(output, outputName);
    }

    /**
     * Aggregate and process multiple specification files to generate a runtime
     * monitor file.
     *
     * @param specFiles
     *            All the file objects used to construct the monitor object.
     */
    public static void processMultipleFiles(ArrayList<File> specFiles)
            throws RVMException {
        String outputName;

        if (options.outputDir == null) {
            options.outputDir = getTargetDir(specFiles);
        }

        if (options.name != null) {
            outputName = options.name;
        } else {
            if (specFiles.size() == 1) {
                outputName = Tool.getFileName(specFiles.get(0)
                        .getAbsolutePath());
            } else {
                int suffixNumber = 0;
                // generate auto name like 'MultiMonitorApsect.aj'

                File outputFile;
                do {
                    suffixNumber++;
                    outputFile = new File(options.outputDir.getAbsolutePath()
                            + File.separator + "MultiSpec_" + suffixNumber
                            + "RuntimeMonitor.java");
                } while (outputFile.exists());

                outputName = "MultiSpec_" + suffixNumber;
            }
        }

        RVMNameSpace.init();
        ArrayList<RVMSpecFile> specs = new ArrayList<>();
        for (File file : specFiles) {
            String specStr = SpecExtractor.process(file);
            RVMSpecFile spec = SpecExtractor.parse(specStr);

            specs.add(spec);
        }
        RVMSpecFile combinedSpec = SpecCombiner.process(specs);

        RVMProcessor processor = new RVMProcessor(outputName);
        String output = processor.process(combinedSpec);

        writeCombinedOutputFile(output, outputName);
    }

    /**
     * Write an output file with the given content and name.
     *
     * @param outputContent
     *            The text to write into the file.
     * @param outputName
     *            The name of the output being written.
     */
    protected static void writeCombinedOutputFile(String outputContent,
            String outputName) throws RVMException {
        if (outputContent == null || outputContent.length() == 0)
            return;

        try {
            FileWriter f = new FileWriter(options.outputDir.getAbsolutePath()
                    + File.separator + outputName + "RuntimeMonitor.java");
            f.write(outputContent);
            f.close();
        } catch (Exception e) {
            throw new RVMException(e.getMessage());
        }
        System.out.println(" " + outputName
                + "RuntimeMonitor.java is generated");
    }

    /**
     * Reformat a path to deal with platform-specific oddities.
     *
     * @param path
     *            The path to clean up.
     * @return A cleaned up path.
     */
    public static String polishPath(String path) {
        if (path.indexOf("%20") > 0)
            path = path.replaceAll("%20", " ");

        return path;
    }

    /**
     * Filter to Java and RVM files and construct their full paths.
     *
     * @param files
     *            An array of files that might be involved.
     * @param path
     *            The directory to look for the files in.
     * @return A filtered list of relevant files with full paths.
     */
    public static ArrayList<File> collectFiles(String[] files, String path)
            throws RVMException {
        ArrayList<File> ret = new ArrayList<>();

        for (String file : files) {
            String fPath = path.length() == 0 ? file : path + File.separator
                    + file;
            File f = new File(fPath);

            if (!f.exists()) {
                throw new RVMException("[Error] Target file, " + file
                        + ", doesn't exist!");
            } else if (f.isDirectory()) {
                ret.addAll(collectFiles(f.list(), f.getAbsolutePath()));
            } else {
                if (Tool.isSpecFile(file)) {
                    ret.add(f);
                } else if (Tool.isJavaFile(file)) {
                    ret.add(f);
                } else {
                    // Just ignore it, so we can go arbitrary deep with
                    // directories.
                    /*
                     * throw new RVMException("Unrecognized file type! The RV "
                     * + "Monitor specification file should have .rvm as" +
                     * " the extension.");
                     */
                }
            }
        }

        return ret;
    }

    /**
     * Process an array of files at a base path.
     *
     * @param files
     *            All the files to consider when producing output.
     * @param path
     *            The base path of the files.
     */
    public static void process(String[] files, String path) throws RVMException {
        ArrayList<File> specFiles = collectFiles(files, path);

        if (options.name != null && files.length > 1) {
            options.merge = true;
        }

        if (options.merge) {
            System.out.println("-Processing " + specFiles.size()
                    + " specification(s)");
            processMultipleFiles(specFiles);
        } else {
            for (File file : specFiles) {
                System.out.println("-Processing " + file.getPath());
                processSpecFile(file);
            }
        }
    }

    /**
     * Process a semicolon-separated list of files.
     *
     * @param arg
     *            A list of files, separated by semicolons.
     */
    public static void process(String arg) throws RVMException {
        if (options.outputDir != null && !options.outputDir.exists())
            throw new RVMException("The output directory, "
                    + options.outputDir.getPath() + " does not exist.");

        process(arg.split(";"), "");
    }

    public static void process(List<String> files) throws RVMException {
        System.out.println("BBBBBBBB: " + files);
        process(files.toArray(files.toArray(new String[0])), "");
    }


    /**
     * Print the command line options (extended version) usable with Java
     * RV-Monitor.
     */
    public static void print_help_ext() {
        System.out
        .println("Usage: java [-cp rv_monitor_classpath] com.runtimeverification.rvmonitor.java.rvj.Main [-options] files");
        System.out.println("");
        System.out.println(" Options enabled by default are prefixed with '+'");
        System.out.println("    -h --help\t\t\t  print this help message");
        System.out
        .println("    --version\t\t\t  display RV-Monitor version information");
        System.out.println("    -v | --verbose\t\t  enable verbose output");
        System.out.println("    --debug\t\t\t  enable verbose error message");
        System.out.println();

        System.out
        .println("    -d <output path>\t\t  select directory to store output files");
        System.out
        .println("    -n <name>\t\t\t  use the given class name instead of source code name");
        System.out.println();

        System.out
        .println("    -s | --statistics\t\t  generate monitor with statistics");
        System.out
        .println("    --noopt1\t\t\t  don't use the enable set optimization");
        System.out.println();

        System.out
        .println("    --finegrainedlock\t\t  use fine-grained lock for internal data structure");
        System.out
        .println("    --weakrefinterning\t\t  use WeakReference interning in indexing trees");
        System.out.println();

    }

    /**
     * Print the command line options usable with Java RV-Monitor.
     */
    public static void print_help() {
        System.out
        .println("Usage: java [-cp rv_monitor_classpath] com.runtimeverification.rvmonitor.java.rvj.Main [-options] files");
        System.out.println("\n");
        System.out.println("    -h --help\t\t\t  print this help message\n");
        System.out
        .println("    --version\t\t\t  display RV-Monitor version information\n");
        System.out.println("    -v | --verbose\t\t  enable verbose output\n");
        System.out.println("    --debug\t\t\t  enable verbose error message");
        System.out.println();
    }

    /**
     * Run Java RV-Monitor on some files.
     *
     * @param args
     *            The command-line arguments.
     */
    public static void main(String[] args) {

        options = new RVMOptions();
        JCommander jc;
        try {
            jc = new JCommander(options, args);
        } catch (ParameterException pe) {
            System.out.println(pe.getMessage());
            return;
        }
        jc.setProgramName("rv-monitor");

        handleOptions(options, args, jc);
        ClassLoader loader = Main.class.getClassLoader();
        String mainClassPath = loader.getResource(
                        "com/runtimeverification/rvmonitor/java/rvj/Main.class")
                .toString();
        if (mainClassPath
                .endsWith(".jar!/com/runtimeverification/rvmonitor/java/rvj/Main.class")
                && mainClassPath.startsWith("jar:")) {
            options.isJarFile = true;

            options.jarFilePath = mainClassPath
                    .substring(
                            "jar:file:".length(),
                            mainClassPath.length()
                                    - "!/com/runtimeverification/rvmonitor/java/rvj/Main.class"
                                    .length());
            options.jarFilePath = polishPath(options.jarFilePath);
        }

        CodeGenerationOption.initialize();

        try {
            process(options.files);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (options.debug)
                e.printStackTrace();
        }
    }

    private static void handleOptions(RVMOptions options, String[] args, JCommander jc) {
        if (args.length == 0 || (options.files.size() == 0)) {
            jc.usage();
            System.exit(1);
        }

        if (options.verbose) {
            LogicRepositoryConnector.verbose = true;
            RVMProcessor.verbose = true;
        }

        if (options.version) {
            Tool.printVersionMessage();
            System.exit(0);
        }

        if (options.help || options.files.size() == 0) {
            if (options.verbose) {
                print_help_ext();
            } else {
                print_help();
            }
        }
    }
}
