package baseaspect;

import examples.TestHelper;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Created by xiaohe on 1/6/15.
 */
public class BaseAspectIT {
    private final FileSystem fileSystem = FileSystems.getDefault();

    private final String inputMOP = ".." + File.separator + "HasNext.mop";

    private final Path defaultTestBasePath = this.getBasePath("examples" + File.separator + "BaseAspect"
            + File.separator + "default-baseaspect" + File.separator + "HasNextMonitorAspect.aj.expected.out");

    private final TestHelper helper_default = new TestHelper(defaultTestBasePath.toString() + File.separator +
            "HasNextMonitorAspect.aj.expected.out");

    private final Path userSpecifiedTestBasePath = this.getBasePath("examples" + File.separator + "BaseAspect"
            + File.separator + "user-specified-baseaspect" + File.separator + "HasNextMonitorAspect.aj.expected.out");

    private final TestHelper helper_userSpecified = new TestHelper(userSpecifiedTestBasePath +
            File.separator + "HasNextMonitorAspect.aj.expected.out");


    private Path getBasePath(String path) {
        return this.fileSystem.getPath(path).getParent();
    }

    @Test
    public void testDefaultBaseAspect() throws Exception {
        String prefix = "HasNextMonitorAspect.aj";
        String actualAJ = this.defaultTestBasePath + File.separator + ".." + File.separator + prefix;
        String javaOutputPrefix = "HasNext_1";

        String command = System.getProperty("user.dir") + File.separator + "bin" + File.separator + "javamop";
        if (SystemUtils.IS_OS_WINDOWS) {
            command += ".bat";
        }

        String classpath = "." + File.pathSeparator + javaOutputPrefix + File.separator+ "mop" + File.separator
                + File.pathSeparator + System.getProperty("java.class.path") + File.pathSeparator +
                javaOutputPrefix + File.separator;

        try {
            helper_default.testCommand(null, command, inputMOP);
            helper_default.assertEqualFiles(this.defaultTestBasePath + File.separator + prefix + ".expected.out",
                    actualAJ);


            // AJC has nonzero return codes with just warnings, not errorss.
            helper_default.testCommand(null, false, true, "java", "-cp", classpath,
                    "org.aspectj.tools.ajc.Main", "-1.6", "-d", javaOutputPrefix,
                    javaOutputPrefix + File.separator + javaOutputPrefix + ".java", ".." + File.separator + prefix);

            helper_default.testCommand(javaOutputPrefix, javaOutputPrefix, false, true,
                    "java", "-cp", classpath, javaOutputPrefix);


        } finally {
            helper_default.deleteFiles(true, ".." + File.separator + prefix);
            helper_default.deleteFiles(true, javaOutputPrefix + File.separator + "mop");
            helper_default.deleteFiles(true, javaOutputPrefix + File.separator + javaOutputPrefix + ".actual.err");
            helper_default.deleteFiles(true, javaOutputPrefix + File.separator + javaOutputPrefix +  ".actual.out");
            helper_default.deleteFiles(true, javaOutputPrefix + File.separator + javaOutputPrefix +  ".class");
        }
    }

}
