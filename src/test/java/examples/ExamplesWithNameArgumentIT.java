// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package examples;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import java.io.File;

/**
 * Test javamop with -n argument is able to generate .aj file successfully.
 */

public class ExamplesWithNameArgumentIT {
    private final String mopFile = "rvm" + File.separator + "HasNext.mop";
    private final String path = "examples" + File.separator + "agent" + File.separator + "HasNext";

    private final TestHelper helper = new TestHelper(path + File.separator + mopFile);

    @Test
    public void testExampleWithArgs() throws Exception{
        String command = System.getProperty("user.dir") + File.separator + "bin" + File.separator + "javamop";
        if (SystemUtils.IS_OS_WINDOWS) {
            command += ".bat";
        }
        try {
            helper.testCommand(null, false, true, command, "-n", "test", "HasNext.mop");
        } finally {
            helper.deleteFiles(true, "testMonitorAspect.aj");
            helper.deleteFiles(true, "HasNext.rvm");
        }
    }
}
