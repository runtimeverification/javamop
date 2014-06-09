package javamoptestsuite;

import java.io.File;

import javamop.util.StreamGobbler;

public class ProgramCompileFunctor implements TestCaseFunctor {
    public FunctorResult apply(TestCase testCase) {
        FunctorResult ret = new FunctorResult();
        
        for (TestCaseProgDir testCaseProg : testCase.testing_programs) {
            ret.addSubCase(testCaseProg.dirName);
            
            for (File javaFile : testCaseProg.javaFiles) {
                String javaFilePath = null;
                try {
                    javaFilePath = javaFile.getCanonicalPath();
                } catch (Exception e) {
                    ret.addStdErr(testCaseProg.dirName, e.getMessage());
                    ret.success = false;
                    break;
                }
                
                if (!javaFile.exists()) {
                    ret.addStdErr(testCaseProg.dirName, "  [ERROR] test program does not exist.");
                    ret.success = false;
                    break;
                }
                
                String origDirPath = testCase.basepath + File.separator + testCase.path + File.separator + testCaseProg.dirName;
                String monitoredDirPath = testCase.basepath + File.separator + testCase.path + File.separator + testCaseProg.dirName
                + "_MOP";
                
                File origDir = new File(origDirPath);
                if (!origDir.exists()) {
                    boolean status = origDir.mkdirs();
                    if (!status) {
                        ret.addStdErr(testCaseProg.dirName, "  [ERROR] original directory " + origDirPath
                        + "does not exist and it could not be created either.");
                        ret.success = false;
                        continue;
                    }
                }
                File monitoredDir = new File(monitoredDirPath);
                if (!monitoredDir.exists()) {
                    boolean status = monitoredDir.mkdirs();
                    if (!status) {
                        ret.addStdErr(testCaseProg.dirName, "  [ERROR] _MOP directory " + monitoredDir
                        + "does not exist and it could not be created either.");
                        ret.success = false;
                        continue;
                    }
                }
                
                String[] cmdarray = { "javac", "-cp", origDirPath, "-Xlint:unchecked", "-d", origDirPath, javaFilePath };
                
                try {
                    Process child;
                    String output = "";
                    
                    if (Main.Debug)
                        System.out.println("ProgramCompile breakpoint 1");
                    
                    child = Runtime.getRuntime().exec(cmdarray, null);
                    
                    if (Main.Debug)
                        System.out.println("ProgramCompile breakpoint 2");
                    
                    StreamGobbler errorGobbler = new StreamGobbler(child.getErrorStream());
                    StreamGobbler outputGobbler = new StreamGobbler(child.getInputStream());
                    
                    errorGobbler.start();
                    outputGobbler.start();
                    
                    //child.waitFor();
                    
                    outputGobbler.join();
                    errorGobbler.join();
                    
                    if (Main.Debug)
                        System.out.println("ProgramCompile breakpoint 3");
                    
                    ret.addStdOut(testCaseProg.dirName, outputGobbler.text);
                    ret.addStdErr(testCaseProg.dirName, errorGobbler.text);
                    
                    if (Main.Debug)
                        System.out.println("ProgramCompile breakpoint 4");
                    
                    if (output.indexOf("error") == -1) {
                        String origianlClassPath = javaFile.getCanonicalPath().substring(0, javaFile.getCanonicalPath().length() - 5)
                        + ".class";
                String monitoredClassPath = monitoredDirPath + File.separator
                + javaFile.getName().substring(0, javaFile.getName().length() - 5) + ".class";
                
                if(new File(origianlClassPath).exists())
                    Main.copy(origianlClassPath, monitoredClassPath);
                    } else {
                        ret.success = false;
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ret.addStdErr(testCaseProg.dirName, e.getMessage());
                    ret.success = false;
                    break;
                }
            }
            
            // maybe we should copy directories under the origDirPath to the monitoredDirPath
            
        }
        
        return ret;
    }
}
