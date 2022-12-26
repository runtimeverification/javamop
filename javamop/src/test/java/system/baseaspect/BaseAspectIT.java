package baseaspect;

import examples.TestHelper;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;

/**
 * Created by xiaohe on 1/6/15.
 */
public class BaseAspectIT {

    private final String inputMOP = ".." + File.separator + "HasNext.mop";

    private final String testName = "HasNext";

    private final String ajName = testName + "MonitorAspect.aj";

    private final String defaultTestBasePath = "examples" + File.separator + "BaseAspect"
            + File.separator + "default-baseaspect";

    private final String userSpecifiedTestBasePath = "examples" + File.separator + "BaseAspect"
            + File.separator + "user-specified-baseaspect";

    private final String aj_default = this.defaultTestBasePath + File.separator +
            this.ajName;

    private final String aj_udefined = this.userSpecifiedTestBasePath + File.separator +
            this.ajName;


    private TestHelper helper_default;

    private TestHelper helper_userSpecified;

    private final String libPath = System.getProperty("user.dir") + File.separator +
            "target" + File.separator + "release" + File.separator + "javamop"
            + File.separator + "javamop" + File.separator + "lib" + File.separator;

    private final String logicPluginPath = libPath + "plugins" + File.separator;

    @Test
    /**
     * Test whether javamop can integrate the default base aspect into the generated .aj file if user does not
     * specify a Base Aspect.
     */
    public void testDefaultBaseAspect() throws Exception {
        String javaOutputPrefix = testName + "_1";
        String rvmFile = testName + ".rvm";

        String command = System.getProperty("user.dir") + File.separator + "bin" + File.separator
                + "javamop";
        if (SystemUtils.IS_OS_WINDOWS) {
            command += ".bat";
        }

        String classpath = "." + File.pathSeparator + javaOutputPrefix + File.separator + "mop"
                + File.separator + File.pathSeparator + System.getProperty("java.class.path")
                + File.pathSeparator + javaOutputPrefix + File.separator;

        classpath = logicPluginPath + "*" +
                    File.pathSeparator + libPath + "*" +
                    File.pathSeparator + classpath;

        HashMap<String,String> envMap = new HashMap<>();
        envMap.put("LOGICPLUGINPATH", logicPluginPath);
        System.setProperty("java.class.path", classpath);

        this.helper_default = new TestHelper(aj_default + ".expected.out", envMap);

        try {
            helper_default.testCommand(null, command, "-d", ".", inputMOP);
            helper_default.assertEqualFilesIgnoringLineSeparators
                    (aj_default + ".expected.out", aj_default);


            //generate the monitor library via rv-monitor
            helper_default.testCommand(null, false, true, "java",
                    "com.runtimeverification.rvmonitor.java.rvj.Main",
                    "-d", ".", rvmFile);

            // AJC has nonzero return codes with just warnings, not errors.
            helper_default.testCommand(null, false, true, "java",
                    "org.aspectj.tools.ajc.Main", "-1.6", "-d", javaOutputPrefix,
                    javaOutputPrefix + File.separator + javaOutputPrefix + ".java",
                    testName + "RuntimeMonitor.java", ajName);

            helper_default.testCommand(javaOutputPrefix, javaOutputPrefix, false, true, false,
                    "java", "-cp", classpath, javaOutputPrefix);


        } finally {
            helper_default.deleteFiles(true, this.ajName);
            helper_default.deleteFiles(true, javaOutputPrefix + File.separator + "mop");
            helper_default.deleteFiles(true, javaOutputPrefix + File.separator + javaOutputPrefix + ".actual.err");
            helper_default.deleteFiles(true, javaOutputPrefix + File.separator + javaOutputPrefix + ".actual.out");
            helper_default.deleteFiles(true, javaOutputPrefix + File.separator + javaOutputPrefix + ".class");
            helper_default.deleteFiles(true, testName + "RuntimeMonitor.java");
            helper_default.deleteFiles(true, rvmFile);
        }
    }

    @Test
    /**
     * Test whether javamop can integrate the user provided Base Aspect into the generated .aj file;
     * Also test whether the base aspect works as expected by observing whether certain pointcuts are ignored in
     * the instrumentation.
     */
    public void testUDefinedBaseAspect() throws Exception {
        String prefix1 = "Has_Next";
        String prefix2 = "HasNext_1";
        String rvmFile = testName + ".rvm";

        String command = System.getProperty("user.dir") + File.separator + "bin" +
                File.separator + "javamop";

        if (SystemUtils.IS_OS_WINDOWS) {
            command += ".bat";
        }

        String classpath = "." + File.pathSeparator + prefix1 + File.separator + "mop" + File.separator +
                File.pathSeparator + prefix2 + File.separator + "mop" + File.separator
                + File.pathSeparator + System.getProperty("java.class.path") + File.pathSeparator +
                prefix1 + File.separator + File.pathSeparator + prefix2 + File.separator;

        System.setProperty("LOGICPLUGINPATH", logicPluginPath);

        classpath = logicPluginPath + "*" +
                File.pathSeparator + libPath + "*" +
                File.pathSeparator + classpath;

        HashMap<String,String> envMap = new HashMap<>();
        envMap.put("LOGICPLUGINPATH", logicPluginPath);
        System.setProperty("java.class.path", classpath);

        this.helper_userSpecified = new TestHelper(aj_udefined + ".expected.out", envMap);

        try {
            String baseAJ = ".." + File.separator + "BaseAspect.aj";
            helper_userSpecified.testCommand(null, command, "-baseaspect", baseAJ,
                    "-d", ".", inputMOP);
            helper_userSpecified.assertEqualFilesIgnoringLineSeparators
                    (aj_udefined + ".expected.out", aj_udefined);


            //generate the monitor library via rv-monitor
            helper_userSpecified.testCommand(null, false, true, "java",
                    "com.runtimeverification.rvmonitor.java.rvj.Main",
                    "-d", ".", rvmFile);

            // AJC has nonzero return codes with just warnings, not errors.
            //First, test whether Has_Next.java was instrumented as usual
            helper_userSpecified.testCommand(null, false, true, "java",
                    "org.aspectj.tools.ajc.Main", "-1.6", "-d", prefix1,
                    prefix1 + File.separator + prefix1 + ".java",
                    testName + "RuntimeMonitor.java", ajName);

            helper_userSpecified.testCommand(prefix1, prefix1, false, true, false,
                    "java", "-cp", classpath, prefix1);

            //Second, test whether HasNext_1.java was NOT instrumented as usual (the pointcuts within that path
            // have been ignored in the instrumentation because user provided base aspect eliminate the pointcuts
            // occur in files whose names contain `HasNext`)
            helper_userSpecified.testCommand(null, false, true, "java",
                    "org.aspectj.tools.ajc.Main", "-1.6", "-d", prefix2,
                    prefix2 + File.separator + prefix2 + ".java",
                    testName + "RuntimeMonitor.java", ajName);

            helper_userSpecified.testCommand(prefix2, prefix2, false, true, false,
                    "java", "-cp", classpath, prefix2);


        } finally {
            helper_userSpecified.deleteFiles(true, ajName);
            helper_userSpecified.deleteFiles(true, rvmFile);
            helper_userSpecified.deleteFiles(true, testName + "RuntimeMonitor.java");

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
