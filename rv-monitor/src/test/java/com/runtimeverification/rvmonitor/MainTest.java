package com.runtimeverification.rvmonitor;

import java.io.File;

import com.runtimeverification.rvmonitor.examples.TestHelper;
import com.runtimeverification.rvmonitor.java.rvj.Main;
import org.junit.Test;

public class MainTest {
    @Test
    public void testHasNext() throws Exception {
        String path = "/home/legunsen/projects/javamop/rv-monitor/examples/java/FSM/HasNext2/rvm/HasNext2.rvm";
//        File projectRoot = new File(System.getProperty("user.dir")).getParentFile();
//        String rtJar = projectRoot + File.separator + "rv-monitor" + File.separator + "target" + File.separator + "release" +
//                       File.separator + "rv-monitor" + File.separator + "lib" + File.separator + "*";
//        TestHelper helper = new TestHelper(new File(path).getParent());
//        helper.testCommand(null, projectRoot + File.separator + "rv-monitor" + File.separator + "bin" + File.separator + "rv-monitor", path);
//
        String out = "/home/legunsen/projects/javamop/rv-monitor/examples/java/FSM/HasNext2/rvm";
        Main.main(new String[]{"-d", out, "-debug", "-verbose", path});
    }
}
