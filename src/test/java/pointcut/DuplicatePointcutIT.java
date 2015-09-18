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

        helper.testCommand(null, false, true, command, "-merge", ".." + File.separator +
                "Pointcut");

        String ajName = "MultiSpec_1MonitorAspect.aj";
        File combinedAJ = new File(ajName);

        Assert.assertTrue(ajName + " not generated", combinedAJ.exists());

        String classpath = "." + File.separator + System.getProperty("java.class.path");

        // AJC has nonzero return codes with just warnings, not errors.
        helper.testCommand(null, false, true, "java", "-cp", classpath,
                "org.aspectj.tools.ajc.Main", "-1.6", ajName);

        helper.deleteFiles(true, ajName);
    }
}
