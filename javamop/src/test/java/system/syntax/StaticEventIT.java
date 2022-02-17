package syntax;

import examples.TestHelper;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import java.io.File;

/**
 * Created by xiaohe on 9/18/15.
 * This integration test ensures the future release of JavaMOP will not generate .aj file that
 * contains duplicate pointcut definitions.
 */
public class StaticEventIT {
    private final String testName = "HasNext";

    private final String path = "examples" + File.separator + "MOPSyntax" + File.separator +
            "StaticEvent";

    private final TestHelper helper = new TestHelper(path + File.separator + testName + ".mop");

    @Test
    public void StaticEventTest() throws Exception {
        String command = System.getProperty("user.dir") + File.separator + "bin" + File.separator + "javamop";
        if (SystemUtils.IS_OS_WINDOWS) {
            command += ".bat";
        }

        File rvmFile = new File(System.getProperty("user.dir")
                + File.separator + this.path + File.separator + testName + ".rvm");

        helper.testPredicate("", rvmFile.toPath(), "The rvm spec should contain static event 'next'.",
                (rvmContent -> rvmContent.replaceAll("\\p{Space}", "")
                        .contains("static event next".replaceAll("\\p{Space}", ""))),
                command, testName + ".mop");

        helper.deleteFiles(true, "HasNext.rvm");
        helper.deleteFiles(true, testName + "MonitorAspect.aj");
    }
}
