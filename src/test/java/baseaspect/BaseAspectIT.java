package baseaspect;

import examples.TestHelper;
import org.apache.commons.lang3.SystemUtils;

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

    private final String ajName = "HasNextMonitorAspect.aj";

    private final Path defaultTestBasePath = this.getBasePath("examples" + File.separator + "BaseAspect"
            + File.separator + "default-baseaspect" + File.separator + this.ajName + ".expected.out");

    private final TestHelper helper_default = new TestHelper(defaultTestBasePath.toString() + File.separator +
            this.ajName + ".expected.out");

    private final Path userSpecifiedTestBasePath = this.getBasePath("examples" + File.separator + "BaseAspect"
            + File.separator + "user-specified-baseaspect" + File.separator + this.ajName + ".expected.out");

    private final TestHelper helper_userSpecified = new TestHelper(userSpecifiedTestBasePath +
            File.separator + this.ajName + ".expected.out");

    private final String actualAJ = this.defaultTestBasePath + File.separator + ".." + File.separator +
            this.ajName;

    private Path getBasePath(String path) {
        return this.fileSystem.getPath(path).getParent();
    }

//    @Test
    /**
     * Test whether javamop can integrate the default base aspect into the generated .aj file if user does not
     * specify a Base Aspect.
     */
    public void testDefaultBaseAspect() throws Exception {
        String javaOutputPrefix = "HasNext_1";

        String command = System.getProperty("user.dir") + File.separator + "bin" + File.separator + "javamop";
        if (SystemUtils.IS_OS_WINDOWS) {
            command += ".bat";
        }

        String classpath = "." + File.pathSeparator + javaOutputPrefix + File.separator + "mop" + File.separator
                + File.pathSeparator + System.getProperty("java.class.path") + File.pathSeparator +
                javaOutputPrefix + File.separator;

        try {
            helper_default.testCommand(null, command, inputMOP);
            helper_default.assertEqualFilesIgnoringLineSeparators
                    (this.defaultTestBasePath + File.separator + this.ajName
                            + ".expected.out", actualAJ);


            // AJC has nonzero return codes with just warnings, not errorss.
            helper_default.testCommand(null, false, true, "java", "-cp", classpath,
                    "org.aspectj.tools.ajc.Main", "-1.6", "-d", javaOutputPrefix,
                    javaOutputPrefix + File.separator + javaOutputPrefix + ".java", ".." +
                            File.separator + this.ajName);

            helper_default.testCommand(javaOutputPrefix, javaOutputPrefix, false, true,
                    "java", "-cp", classpath, javaOutputPrefix);


        } finally {
            helper_default.deleteFiles(true, ".." + File.separator + this.ajName);
            helper_default.deleteFiles(true, javaOutputPrefix + File.separator + "mop");
            helper_default.deleteFiles(true, javaOutputPrefix + File.separator + javaOutputPrefix + ".actual.err");
            helper_default.deleteFiles(true, javaOutputPrefix + File.separator + javaOutputPrefix + ".actual.out");
            helper_default.deleteFiles(true, javaOutputPrefix + File.separator + javaOutputPrefix + ".class");
        }
    }

//    @Test
    /**
     * Test whether javamop can integrate the user provided Base Aspect into the generated .aj file;
     * Also test whether the base aspect works as expected by observing whether certain pointcuts are ignored in
     * the instrumentation.
     */
    public void testUDefinedBaseAspect() throws Exception {
        String prefix1 = "Has_Next";
        String prefix2 = "HasNext_1";

        String command = System.getProperty("user.dir") + File.separator + "bin" + File.separator + "javamop";
        if (SystemUtils.IS_OS_WINDOWS) {
            command += ".bat";
        }

        String classpath = "." + File.pathSeparator + prefix1 + File.separator + "mop" + File.separator +
                File.pathSeparator + prefix2 + File.separator + "mop" + File.separator
                + File.pathSeparator + System.getProperty("java.class.path") + File.pathSeparator +
                prefix1 + File.separator + File.pathSeparator + prefix2 + File.separator;

        try {
            String baseAJ = ".." + File.separator + "BaseAspect.aj";
            helper_userSpecified.testCommand(null, command, "-baseaspect", baseAJ, inputMOP);
            helper_userSpecified.assertEqualFilesIgnoringLineSeparators
                    (this.userSpecifiedTestBasePath + File.separator + ajName + ".expected.out", actualAJ);

            // AJC has nonzero return codes with just warnings, not errorss.
            //First, test whether Has_Next.java was instrumented as usual
            helper_userSpecified.testCommand(null, false, true, "java", "-cp", classpath,
                    "org.aspectj.tools.ajc.Main", "-1.6", "-d", prefix1,
                    prefix1 + File.separator + prefix1 + ".java", ".." + File.separator + ajName);

            helper_userSpecified.testCommand(prefix1, prefix1, false, true,
                    "java", "-cp", classpath, prefix1);

            //Second, test whether HasNext_1.java was NOT instrumented as usual (the pointcuts within that path
            // have been ignored in the instrumentation because user provided base aspect eliminate the pointcuts
            // occur in files whose names contain `HasNext`)
            helper_userSpecified.testCommand(null, false, true, "java", "-cp", classpath,
                    "org.aspectj.tools.ajc.Main", "-1.6", "-d", prefix2,
                    prefix2 + File.separator + prefix2 + ".java", ".." + File.separator + ajName);

            helper_userSpecified.testCommand(prefix2, prefix2, false, true,
                    "java", "-cp", classpath, prefix2);


        } finally {
            helper_userSpecified.deleteFiles(true, ".." + File.separator + ajName);
            helper_userSpecified.deleteFiles(true, prefix1 + File.separator + "mop");
            helper_userSpecified.deleteFiles(true, prefix1 + File.separator + prefix1 + ".actual.err");
            helper_userSpecified.deleteFiles(true, prefix1 + File.separator + prefix1 + ".actual.out");
            helper_userSpecified.deleteFiles(true, prefix1 + File.separator + prefix1 + ".class");

            helper_userSpecified.deleteFiles(true, prefix2 + File.separator + "mop");
            helper_userSpecified.deleteFiles(true, prefix2 + File.separator + prefix2 + ".actual.err");
            helper_userSpecified.deleteFiles(true, prefix2 + File.separator + prefix2 + ".actual.out");
            helper_userSpecified.deleteFiles(true, prefix2 + File.separator + prefix2 + ".class");
        }
    }
}
