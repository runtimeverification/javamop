// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
import examples.TestHelper;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import java.io.File;

/**
 * JUnit test case to test agent generation with -usedb option
 */
public class AgentGenerationRemoteIT {

    private final TestHelper helper = new TestHelper("examples" + File.separator + "agent"
            + File.separator + "usedb" + File.separator + "usedb.expected.out");

    @Test
    public void testAgentGenerationUsedb() throws Exception {
        helper.deleteFiles(false, "properties", "properties-copy", "agent.jar");

       String command = System.getProperty("user.dir") + File.separator + "bin" + File.separator + "javamop";
        if (SystemUtils.IS_OS_WINDOWS) {
            command += ".bat";
        }

        if (SystemUtils.IS_OS_WINDOWS) {
            command += ".bat";
        }
        helper.testCommand("usedb", true, true, command, "-agent -n agent -usedb");

        helper.deleteFiles(true, "properties", "properties-copy", "agent.jar");
        helper.deleteFiles(true, "usedb.actual.out", "usedb.actual.err");
    }
}
