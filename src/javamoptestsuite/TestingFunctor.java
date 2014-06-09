package javamoptestsuite;

import java.io.*;
import java.lang.reflect.*;

import javamop.util.StreamGobbler;

public class TestingFunctor implements TestCaseFunctor {
    
    public String removeEmptyLines(String input) {
        String output = "";
        String[] lines = input.split("\n");
        
        for (String line : lines) {
            
            boolean flag = false;
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) != ' ' && line.charAt(i) != '\r') {
                    flag = true;
                    break;
                }
            }
            
            if (flag) {
                if (line.charAt(line.length() - 1) == '\r')
                    line = line.substring(0, line.length() - 1);
                output += line + "\n";
            }
        }
        return output;
    }
    
    public boolean compareOutput(String output, File outputFile) throws Exception {
        FileReader rd = new FileReader(outputFile);
        
        char[] buf = new char[(int) outputFile.length()];
        rd.read(buf);
        String outputFromFile = new String(buf);
        
        String str_1 = removeEmptyLines(output);
        String str_2 = removeEmptyLines(outputFromFile);
        
        if (str_1.compareTo(str_2) == 0)
            return true;
        else
            return false;
    }
    
    public FunctorResult apply(TestCase testCase) {
        FunctorResult ret = new FunctorResult();
        
        String ajcrtlibPath = "";
        String os = System.getProperty("os.name");
        if (os.toLowerCase().contains("windows")) {
            if (Main.ajcPath.compareTo("ajc") == 0) {
                Main.ajcPath = "ajc.bat";
            } else if (Main.ajcPath.compareTo("ajc.bat") != 0){
                ajcrtlibPath = new File(new File(Main.ajcPath).getParent()).getParent() + "\\lib\\aspectjrt.jar";
            }
        } else {
            if (Main.ajcPath.compareTo("ajc") != 0) {
                ajcrtlibPath = new File(new File(Main.ajcPath).getParent()).getParent() + "/lib/aspectjrt.jar";
            }
        }
        
        String javamoprtLibPath = null;
        if (Main.isJarFile)
            javamoprtLibPath = new File(Main.jarFilePath).getParent() + "/rt.jar";
        else
            javamoprtLibPath = Main.javamopDir + "/lib/rt.jar";
        
        String classpath = System.getenv("CLASSPATH");
        
        if (classpath == null || classpath.length() == 0) {
            classpath = "";
        }
        
        for (TestCaseProgDir testCaseProg : testCase.testing_programs) {
            ret.addSubCase(testCaseProg.dirName);
            
            String origDirPath = testCase.basepath + File.separator + testCase.path + File.separator + testCaseProg.dirName;
            String monitoredDirPath = testCase.basepath + File.separator + testCase.path + File.separator + testCaseProg.dirName
            + "_MOP";
            
            TestClassLoader loader = new TestClassLoader(origDirPath);
            
            for(File outputFile : testCaseProg.outputFiles){
                String name = outputFile.getName().substring(0, outputFile.getName().length() - ".output".length()); 
                
                Class testClass;
                Class[] parameterTypes = { String[].class };
                try {
                    testClass = loader.findClass(name);
                } catch (Exception e) {
                    ret.addStdErr(testCaseProg.dirName, "  [ERR]" + e.getMessage());
                    ret.success = false;
                    continue;
                } catch (Error err) {
                    ret.addStdErr(testCaseProg.dirName, "  [ERR]" + err.getMessage());
                    ret.success = false;
                    continue;
                }
                
                try {
                    Method testMain = testClass.getDeclaredMethod("main", parameterTypes);
                    if (!Modifier.isPublic(testMain.getModifiers()) || !Modifier.isStatic(testMain.getModifiers()))
                        continue;
                } catch (Exception e) {
                    continue;
                }
                
                String[] cmdarray;
                if (os.toLowerCase().contains("windows")) {
                    String[] cmdarray2 = { "java", "-Xmx10M", "-cp", ".;" + classpath + ";" + ajcrtlibPath + ";" + monitoredDirPath + ";" + javamoprtLibPath,
                        name };
                        cmdarray = cmdarray2;
                } else {
                    String[] cmdarray2 = { "java", "-Xmx10M", "-cp", ".:" + classpath + ":" + monitoredDirPath + ":" + javamoprtLibPath + ":" + ajcrtlibPath,
                        name };
                        cmdarray = cmdarray2;
                        
                }
                
                try {
                    Process child;
                    
                    if (Main.Debug)
                        System.out.println("Testing breakpoint 1");
                    
                    child = Runtime.getRuntime().exec(cmdarray, null);
                    
                    if (Main.Debug)
                        System.out.println("Testing breakpoint 2");
                    
                    StreamGobbler errorGobbler = new StreamGobbler(child.getErrorStream());
                    StreamGobbler outputGobbler = new StreamGobbler(child.getInputStream());
                    
                    errorGobbler.start();
                    outputGobbler.start();
                    
                    //child.waitFor();
                    
                    outputGobbler.join();
                    errorGobbler.join();
                    
                    if (Main.Debug)
                        System.out.println("Testing breakpoint 3");
                    
                    ret.addStdOut(testCaseProg.dirName, outputGobbler.text);
                    ret.addStdErr(testCaseProg.dirName, errorGobbler.text);
                    
                    if (Main.Debug)
                        System.out.println("Testing breakpoint 4");
                    
                    if (!compareOutput(outputGobbler.text + errorGobbler.text, outputFile)) {
                        ret.success = false;
                        break;
                    }
                } catch (Exception e) {
                    ret.addStdErr(testCaseProg.dirName, e.getMessage());
                    ret.success = false;
                    break;
                }
                
            }
        }
        return ret;
    }
}
