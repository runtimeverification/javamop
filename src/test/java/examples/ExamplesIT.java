package examples;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.*;

/**
 * JUnit test case to run through select program examples. Based on examples/run and examples/runall.
 */
@RunWith(Parameterized.class)
public class ExamplesIT {
    
    private final TestHelper helper;
    private final String path;
    
    /**
     * Construct this instance of the parameterized test.
     * @param path The path to the .mop file used in this test.
     */
    public ExamplesIT(String path) {
        this.path = new File(path).getParent();
        helper = new TestHelper(path);
    }
    
    /**
     * Test all the instances of this example. Each example has a _1, _2, and possibly a _3 
     * component. This runs assertions on all the avilable ones. This function is inspired by the 
     * examples/run script.
     */
    @Test
    public void testExample() throws Exception {
        final String testName = path.substring(path.lastIndexOf('/')+1);
        helper.testCommand(null, true, "javamop", testName + ".mop");
        
        String classpath = ".:mop/:" + System.getProperty("java.class.path");
        
        String subcasePath = testName + "_";
        for(int i = 1; new File(path + File.separator + subcasePath + i).exists(); i++) {
            String subcasePathI = subcasePath + i;
            String specificClasspath = classpath + ":" + subcasePathI + ":" + subcasePathI + 
                File.separator + "mop";
            // AJC has nonzero return codes with just warnings, not errorss.
            helper.testCommand(null, true, "ajc", "-1.6", "-cp", specificClasspath, "-d", 
                subcasePathI, subcasePathI + File.separator + subcasePathI + ".java", 
                testName + "MonitorAspect.aj");
            helper.testCommand(subcasePathI, subcasePathI, true, "java", "-cp", specificClasspath, 
                subcasePathI);
            helper.deleteFiles(true, subcasePathI + File.separator + subcasePathI + ".actual.err", 
                subcasePathI + File.separator + subcasePathI + ".actual.out");
            helper.deleteFiles(true, subcasePathI + File.separator + subcasePathI + ".class");
            
            String[] classFilePrefix = {
                subcasePathI + File.separator + "mop" + File.separator + testName,
                subcasePathI + File.separator + testName
            };
            for(String prefix : classFilePrefix) {
                helper.deleteFiles(false, prefix + "Monitor.class", prefix + "Monitor_Set.class", 
                    prefix + "MonitorAspect.class", prefix + "RuntimeMonitor.class", 
                    prefix + "SuffixMonitor.class", prefix + "Monitor$IntStack.class",
                    prefix + "SuffixMonitor_Set.class", prefix  + "DisableHolder.class",
                    prefix + "MonitorAspect$" + testName + "_DummyHookThread.class"
                );
                helper.deleteFiles(false, subcasePathI + File.separator + "mop" + File.separator +
                    "I" + testName + "Monitor.class");
            }
        }
        helper.deleteFiles(true, testName + "MonitorAspect.aj");
    }
    
    /**
     * Run a subset of the examples as tests. These are from the examples/runall script.
     */
    @Parameterized.Parameters(name="{0}")
    public static Collection<Object[]> data() {
        ArrayList<Object[]> data = new ArrayList<Object[]>();
        //from examples/runall.txt
        data.add(new Object[]{"examples/CFG/HasNext/HasNext.mop"});
        data.add(new Object[]{"examples/CFG/SafeFile/SafeFile.mop"});
        data.add(new Object[]{"examples/CFG/SafeFileWriter/SafeFileWriter.mop"});
        
        data.add(new Object[]{"examples/ERE/HasNext/HasNext.mop"});
        //#SuffixHasNext, hasnext2, hashset, passwordlogic require to change mop/directoy name
        data.add(new Object[]{"examples/ERE/SafeSyncCollection/SafeSyncCollection.mop"});
        data.add(new Object[]{"examples/ERE/SafeFileWriter/SafeFileWriter.mop"});
        data.add(new Object[]{"examples/ERE/SafeSyncMap/SafeSyncMap.mop"});
        //Pending Issue 5
        data.add(new Object[]{"examples/ERE/SafeEnum/SafeEnum.mop"});
        data.add(new Object[]{"examples/ERE/UnsafeIterator/UnsafeIterator.mop"});
        data.add(new Object[]{"examples/ERE/UnsafeMapIterator/UnsafeMapIterator.mop"});
        
        data.add(new Object[]{"examples/FSM/HasNext/HasNext.mop"});
        data.add(new Object[]{"examples/FSM/HasNext2/HasNext2.mop"});
        
        //Pending Issue 5
        data.add(new Object[]{"examples/LTL/SafeEnum/SafeEnum.mop"});
        data.add(new Object[]{"examples/LTL/HasNext/HasNext.mop"});
        data.add(new Object[]{"examples/LTL/SafeFileWriter/SafeFileWriter.mop"});
        data.add(new Object[]{"examples/LTL/SafeIterator/SafeIterator.mop"});
        data.add(new Object[]{"examples/LTL/SafeMapIterator/SafeMapIterator.mop"});
        
        data.add(new Object[]{"examples/MOPSyntax/Creation/Creation.mop"});
        data.add(new Object[]{"examples/EndProgram/FileClose/FileClose.mop"});
        //# Think about sth similar to handle countCond
        return data;
    }
}