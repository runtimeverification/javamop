package examples;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Created by xiaohe on 5/6/15.
 */
public class ReadDirIT {
    private final String path = "examples" + File.separator + "agent" + File.separator + "many"
            + File.separator + "rvm" + File.separator + "cfg";

    private final TestHelper helper = new TestHelper(path);

    @Test
    public void testDirAsInput() throws Exception {
        String command = System.getProperty("user.dir") + File.separator + "bin" + File.separator + "javamop";
        if (SystemUtils.IS_OS_WINDOWS) {
            command += ".bat";
        }

        String outputDir = this.path + File.separator + ".." + File.separator
                + "output" + File.separator;
        File safeFileAJ = new File(outputDir + "SafeFileMonitorAspect.aj");
        File safeFileWriterAJ = new File(outputDir + "SafeFileWriterMonitorAspect.aj");

        try {
            helper.testCommand("output", "cfg", false, true, true, command, ".." + File.separator +
                    "cfg -d .");


            Assert.assertTrue("SafeFileMonitorAspect.aj does not exist.",
                    safeFileAJ.exists());

            Assert.assertTrue("SafeFileWriterMonitorAspect.aj does not exist.",
                    safeFileWriterAJ.exists());
        } finally {
            boolean succ1 = safeFileAJ.delete();
            boolean succ2 = safeFileWriterAJ.delete();

            Assert.assertTrue("Fail to delete SafeFileMonitorAspect.aj",
                    succ1);

            Assert.assertTrue("Fail to delete SafeFileWriterMonitorAspect.aj",
                    succ2);
        }
    }
}
