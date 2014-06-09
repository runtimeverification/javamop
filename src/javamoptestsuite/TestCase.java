package javamoptestsuite;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class TestCase {
    String basepath;
    String path;
    
    ArrayList<TestCaseSpec> specFiles = new ArrayList<TestCaseSpec>();
    ArrayList<TestCaseProgDir> testing_programs = new ArrayList<TestCaseProgDir>();
    
    boolean statusOK = true;
    boolean doneTesting = false;
    
    public TestCase(String basepath) throws Exception{
        this(basepath, "");
    }
    
    public TestCase(String basepath, String path) throws Exception{
        this.basepath = basepath;
        this.path = path;
        
        File directory = new File(basepath + "/" + path);
        ArrayList<String> specFilePaths = collectSpecFiles(directory);
        ArrayList<String> errFilePaths = collectErrFiles(directory);
        
        for(String specFileName : specFilePaths){
            TestCaseSpec testCaseSpec = new TestCaseSpec(this, specFileName);
            if(errFilePaths.contains(testCaseSpec.err_filename))
                testCaseSpec.hasErrorFile = true;
            this.specFiles.add(testCaseSpec);
        }
        
        testing_programs = this.collectProgDirs(directory);
    }
    
    private ArrayList<String> collectErrFiles(File directory){
        return collectFiles(directory, "err");
    }
    
    private ArrayList<String> collectSpecFiles(File directory){
        return collectFiles(directory, "mop");
    }
    
    private ArrayList<String> collectFiles(File directory, String ext){
        ArrayList<String> ret = new ArrayList<String>();
        
        for (File file : directory.listFiles()) {
            if (!file.isDirectory()) {
                if (file.getName().endsWith("." + ext)) {
                    String name = file.getName();
                    ret.add(name);
                }
            }
        }
        return ret;
    }
    
    private ArrayList<TestCaseProgDir> collectProgDirs(File directory) throws Exception{
        ArrayList<TestCaseProgDir> ret = new ArrayList<TestCaseProgDir>();
        
        for (File file : directory.listFiles()) {
            if (file.isDirectory() && !file.getName().endsWith("_MOP")&& !file.getName().startsWith(".svn")) {
                TestCaseProgDir subDir = new TestCaseProgDir(this, file.getName());
                ret.add(subDir);
            }
        }
        return ret;
    }
    
}
