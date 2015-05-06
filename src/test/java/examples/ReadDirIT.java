package examples;

import org.apache.commons.lang3.SystemUtils;
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
    public void testDirAsInput() throws Exception{
        String command = System.getProperty("user.dir") + File.separator + "bin" + File.separator + "javamop";
        if (SystemUtils.IS_OS_WINDOWS) {
            command += ".bat";
        }
        try {
            helper.testCommand("output", "cfg", false, true, true, command, ".." + File.separator +
                    "cfg -d .");
        } finally {
//            helper.deleteFiles(true, "");
        }
    }
}
