// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.specfiltering;

import javamop.util.Tool;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * This class handles the filtering of properties that are used for building
 * a javamop agent from the property-db. For now, the property-db refers to
 * the old google-code repository at https://code.google.com/p/annotated-java-api/
 * The plan is to change this so that it pulls from this repository instead:
 * https://github.com/runtimeverification/property-db.
 * Please see the comments in javamop/config/remote_server_addr.properties
 * for the various configuration options available.
 */
public class SpecFilter {

    private static final String SPEC_DIRECTORY = "properties";
    private static final String SPEC_DIRECTORY_COPY = "properties-copy";
    private static final String SEVERITY_PREFIX = " * @severity ";
    private static final String SERVER_SETTING= "remote_server_addr.properties";
    private final String url;
    private final String vcs;
    private final String omitFile;
    private final String configPath;
    public static String specDirPath;
    private final List<String> specsToOmit;
    private boolean cleanup;
    private Configuration serverConfig;
    private Configuration filterConfig;

    public SpecFilter() {
        serverConfig = new TextConfiguration(SERVER_SETTING);
        url = serverConfig.getServerSetting("PropertyDBURL");
        vcs = serverConfig.getServerSetting("PropertyDBVCS");
        filterConfig = new TextConfiguration(serverConfig.getServerSetting("FilterConf"));
        omitFile = serverConfig.getServerSetting("OmitFile");
        configPath = Tool.getConfigPath()+File.separator;
        specDirPath = SPEC_DIRECTORY_COPY+ File.separator + "annotated-java-api" +
                File.separator + "java";
        specsToOmit = getFilesToOmit();
        String cleanupOption = serverConfig.getServerSetting("PropertyDBCleanup");
        if (cleanupOption.equals("true")){
            this.cleanup = true;
        }
        downloadAllSpecs();
    }

    /**
     * Reads a list of .mop filenames which are to be excluded from agent generation.
     * This allows the user to omit some files even after filtering by severity level.
     *
     * @return List of .mop files to be omitted from the agent building process
     */
    private List<String> getFilesToOmit() {
        List<String> omitFiles = null;
        File omitConfig = new File(configPath + File.separator + omitFile);
        try {
            omitFiles = FileUtils.readLines(omitConfig, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return omitFiles;
    }

    /**
     * This method downloads the .mop properties from the given url,
     * using the given VSC tool.
     */
    private void downloadAllSpecs() {
        // use vcs and url to get the properties
        Path specPath = FileSystems.getDefault().getPath(SPEC_DIRECTORY);
        Path specCopyPath = FileSystems.getDefault().getPath(SPEC_DIRECTORY_COPY);

        if (Files.notExists(specPath)) {
            System.err.println("Downloading specs from " + url + " ...");
            if(runCommand(vcs, "clone", url, SPEC_DIRECTORY)){
                System.err.println("Done downloading specs.");
            } else{
                System.err.println("(Download Error): The specs could not be downloaded. " +
                        "Internet connection problems?");
            }
        }
        //make a copy of the specDirectory
        if (Files.exists(specCopyPath)) {
            try {
                Tool.deleteDirectory(specCopyPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            System.err.println("Copying Specs ...");
            FileUtils.copyDirectory(new File(SPEC_DIRECTORY), new File(SPEC_DIRECTORY_COPY), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Uses information from the spec_levels.properties file for filtering
     * out .mop files which are *NOT* to be used for agent generation. For
     * each package (key) listed in that file, the corresponding severity
     * level (value) is the highest severity level of spec files to be used.
     * To omit an entire package from use, simply comment it out in the config
     * file.
     *
     * @return The base name of the directory which contains the unfiltered
     *         .mop files
     */
    public String filter() {
        Properties filters = filterConfig.getAllSettings();
        //1. if a spec directory exists, but it has not been listed in the filter config,
        //   delete the entire contents of the directory
        File dir = new File(specDirPath);
        if (!dir.exists()){
            System.err.println("The directory does not exist: " + specDirPath);
        }
        File[] specDirs = dir.listFiles();
        if (specDirs != null) {
            for (File file : specDirs){
                if (!filters.stringPropertyNames().contains(file.getName())){
                    try {
                        Tool.deleteDirectory(file.toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        //2. for each directory that exists and which has been listed in the filter config,
        //   remove the specs which have lower severity level
        for (String packageName : filters.stringPropertyNames()){
            List<String> levelsToDelete = new ArrayList<String>();
            String highestLevel = filters.getProperty(packageName);
            if (highestLevel.equals("error")){
                levelsToDelete.add("warning");
                levelsToDelete.add("suggestion");
            } else if (highestLevel.equals("warning")){
                levelsToDelete.add("suggestion");
            } else {
                // This means we want all severity levels for the selected packages.
                // However we still need a dummy string so that the specs in the omitFile
                // get a chance to be excluded in this case. Any string but "error",
                // "warning" or "suggestion" is acceptable here.
                levelsToDelete.add("none");
            }

            for (String level : levelsToDelete){
                try {
                    filterPackage(packageName,level);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return specDirPath;
    }

    /**
     * For a single package, this method filters out
     *  1) all .mop files which have severity level lower than the one listed
     *     for the package in the spec_levels.properties file and
     *  2) any .mop files in this package which is also listed in the omit.txt file
     *
     * @param packageName The package from which .mop files are to be filtered
     * @param level  The highest severity level of .mop files that are allowed to remain
     * @throws Exception
     */
    private void filterPackage(String packageName, String level) throws Exception {
        File packageDir = new File(specDirPath + File.separator + packageName);
        Path path = FileSystems.getDefault().getPath(specDirPath, packageName);
        if (!packageDir.exists()) {
            throw new Exception(path.toString() +" does not exist!");
        }
        File [] specFiles = packageDir.listFiles();
        if (specFiles != null) {
            for (File specFile : specFiles){
                if ((FileUtils.readFileToString(specFile, Charset.defaultCharset()).contains(SEVERITY_PREFIX + level))
                        || specsToOmit.contains(specFile.getName()) ) {
                    Files.delete(specFile.toPath());
                }
            }
        }
    }

    /**
     * This method creates a new process and executes the command represented by
     * the <code>args</code> parameter.
     *
     * @param args a comma separated list of strings which represent
     *             the command to be executed.
     * @return a boolean value indicating whether the command was successfully
     *         run or not. Although process.waitfor() will return a non-zero
     *         integer value on failure, this method  "overrides" that and
     *         returns a boolean in order to make checks in client code simpler.
     */
    private boolean runCommand(String... args) {
        boolean success = false;
        ProcessBuilder builder  = new ProcessBuilder();
        builder.command(args);
        try {
            Process proc = builder.start();
            int ret = proc.waitFor();
            if (ret == 0){
                success = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    /**
     * removes the directories containing the .mop files after the agent
     * has been generated.
     */
    public void cleanup() {
        try {
            Tool.deleteDirectory(FileSystems.getDefault().getPath(SPEC_DIRECTORY));
            Tool.deleteDirectory(FileSystems.getDefault().getPath(SPEC_DIRECTORY_COPY));
        } catch (IOException e) {
            System.err.println("Could not delete the downloaded spec directory: "+SPEC_DIRECTORY);
            e.printStackTrace();
        }
    }

    /**
     * whether or not the directories containing the .mop files should
     * be deleted after agent generation.
     */
    public boolean isCleanup() {
        return cleanup;
    }
}