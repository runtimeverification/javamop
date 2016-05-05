package javamop.input;

import javamop.JavaMOPMain;
import javamop.helper.IOUtils;
import org.eclipse.core.internal.dtree.TestHelper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by hx312 on 4/8/2016.
 * Test different inputs to javamop.
 */
public class JavaMOPMainTest {
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test(expected = NullPointerException.class)
    public void testNull() throws Exception {
        JavaMOPMain.main(null);
    }

    @Test
    public void testEmptyStringArr() {
        String[] args = new String[0];
        exit.expectSystemExitWithStatus(1);

        JavaMOPMain.main(args);
    }

    @Test
    public void testInputFileWithNonMOPExtension() {
        String[] args = new String[]{"./src/test/java/unit/javamop/input/HasNext.mo"};
        JavaMOPMain.main(args); //expect normal exit.
    }

    //TODO: multiple specs
    //TODO: With Invalid option
    //TODO: valid option: -merge

    @Test
    public void testMultiPropertyInSingleSpec() throws IOException {
        String testOutDir = System.getProperty("testOutDir");
        String mopOutDir = testOutDir + File.separator + "multi-property";

        String[] args = new String[]{"./src/test/resources/multi-property/UnsafeIterator.mop",
        "-d",  mopOutDir};
        JavaMOPMain.main(args);


        examples.TestHelper.assertEqualUnorderedFiles(
                "./src/test/resources/multi-property/UnsafeIterator.rvm.expected",
                mopOutDir + "/UnsafeIterator.rvm");


        examples.TestHelper.assertEqualUnorderedFiles(
                "./src/test/resources/multi-property/UnsafeIteratorMonitorAspect.aj.expected",
                mopOutDir + "/UnsafeIteratorMonitorAspect.aj"
        );

    }

}