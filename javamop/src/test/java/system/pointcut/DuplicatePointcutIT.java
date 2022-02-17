package pointcut;

import examples.TestHelper;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Created by xiaohe on 9/18/15.
 * This integration test ensures the future release of JavaMOP will not generate .aj file that
 * contains duplicate pointcut definitions.
 */
public class DuplicatePointcutIT {
    private final String path = "examples" + File.separator + "MOPSyntax" + File.separator +
            "Pointcut";

    private final TestHelper helper = new TestHelper(path);

    @Test
    public void DuplicatePointcutIT() throws Exception {
        String command = System.getProperty("user.dir") + File.separator + "bin" + File.separator + "javamop";
        if (SystemUtils.IS_OS_WINDOWS) {
            command += ".bat";
        }

        String testName = "Pointcut";
        String output = testName + File.separator + "output";
        helper.testCommand(null, false, true, command, "-merge", testName);

        //generate monitor library code
        helper.testCommand(null, false, true, "java",
                "com.runtimeverification.rvmonitor.java.rvj.Main", "-merge", testName);

        File combinedAJ = helper.getPath(testName + File.separator + "MultiSpec_1MonitorAspect" +
                ".aj").toFile();
        File monitorLib = helper.getPath(testName + File.separator + "MultiSpec_1RuntimeMonitor" +
                ".java")  .toFile();
        Assert.assertTrue(combinedAJ.getAbsolutePath() + " not generated", combinedAJ.exists());

        // AJC has nonzero return codes with just warnings, not errors.
        helper.testCommand(null, false, true, "java",
                "org.aspectj.tools.ajc.Main", "-1.6", "-d", output, monitorLib.getAbsolutePath(),
                combinedAJ.getAbsolutePath());

        helper.deleteFiles(true, testName + File.separator + combinedAJ.getName());
        helper.deleteFiles(true, testName + File.separator + "Comparable_CompareToNull.rvm");
        helper.deleteFiles(true, testName + File.separator + "Comparable_CompareToNullException.rvm");
        helper.deleteFiles(true, testName + File.separator + monitorLib.getName());
        helper.deleteFiles(true, output);
    }
}
