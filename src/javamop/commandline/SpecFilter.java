package javamop.commandline;

import javamop.Configuration;
import javamop.GenerateAgent;
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
 * This class handles the filtering of properties that are used for building a javamop agent
 */
public class SpecFilter {

    public static final String SPEC_DIRECTORY = "annotated-java-api";
    public static final String SPEC_DIRECTORY_COPY = "annotated-java-api-copy";
    public static final String SEVERITY_PREFIX = " * @severity ";
    private final String url;
    private final String vcs;
    private final String filterConfig;
    private final String omitFile;
    private final String configPath;
    public static String specDirPath;
    private List<String> specsToOmit;
    private boolean cleanup;

    public SpecFilter() {
        url = Configuration.getServerSetting("PropertyDBURL");
        vcs = Configuration.getServerSetting("PropertyDBVCS");
        filterConfig = Configuration.getServerSetting("FilterConf");
        omitFile = Configuration.getServerSetting("OmitFile");
        configPath = Tool.getConfigPath()+File.separator;
        specDirPath = SPEC_DIRECTORY_COPY+ File.separator + "properties" + File.separator + "java";
        specsToOmit = getFilesToOmit();
        String cleanupOption = Configuration.getServerSetting("PropertyDBCleanup");
        if (cleanupOption.equals("true")){
            this.cleanup = true;
        }
        downloadAllSpecs();
    }

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

    public String filter() {
        Properties filters = Configuration.getSettingFile(filterConfig);
        //1. if a spec directory exists, but it has not been listed in the filter config,
        //   delete the entire contents of the directory
        File dir = new File(specDirPath);
        if (!dir.exists()){
            System.err.println("The directory does not exist: " + specDirPath);
        }
        for (File file : dir.listFiles()){
            if (!filters.stringPropertyNames().contains(file.getName())){
                try {
                    GenerateAgent.deleteDirectory(file.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
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

    public void cleanup() {
        try {
            GenerateAgent.deleteDirectory(FileSystems.getDefault().getPath(SPEC_DIRECTORY));
            GenerateAgent.deleteDirectory(FileSystems.getDefault().getPath(SPEC_DIRECTORY_COPY));
        } catch (IOException e) {
            System.err.println("Could not delete the downloaded spec directory: "+SPEC_DIRECTORY);
            e.printStackTrace();
        }
    }

    private void filterPackage(String packageName, String level) throws Exception {
        File packageDir = new File(specDirPath + File.separator + packageName);
        Path path = FileSystems.getDefault().getPath(specDirPath, packageName);
        if (!packageDir.exists()) {
            throw new Exception(path.toString() +" does not exist!");
        }
        File [] specFiles = packageDir.listFiles();
        for (File specFile : specFiles){
            if ((FileUtils.readFileToString(specFile, Charset.defaultCharset()).contains(SEVERITY_PREFIX + level))
                    || specsToOmit.contains(specFile.getName()) ) {
                Files.delete(specFile.toPath());
            }
        }
    }

    private void downloadAllSpecs() {
        // use vcs and url to get the properties
        Path specPath = FileSystems.getDefault().getPath(SPEC_DIRECTORY);
        Path specCopyPath = FileSystems.getDefault().getPath(SPEC_DIRECTORY_COPY);

        if (Files.notExists(specPath)) {
            System.err.println("Downloading specs from " + url + " ...");
            runCommand(vcs, "clone", url, SPEC_DIRECTORY);
            System.err.println("Done downloading specs.");
        }
        //copy make a copy of the specDirectory
        if (Files.exists(specCopyPath)) {
            try {
                GenerateAgent.deleteDirectory(specCopyPath);
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

    private boolean runCommand(String... args) {
        boolean success = false;
        ProcessBuilder builder  = new ProcessBuilder();
        builder.command(args);
        try {
            Process proc = builder.start();
            proc.waitFor();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    public boolean isCleanup() {
        return cleanup;
    }
}
