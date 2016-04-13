package javamop.input;

import javamop.JavaMOPMain;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

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

}