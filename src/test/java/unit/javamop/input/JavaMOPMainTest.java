package javamop.input;

import javamop.JavaMOPMain;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hx312 on 4/8/2016.
 * Test different inputs to javamop.
 */
public class JavaMOPMainTest {
    @Test(expected = NullPointerException.class)
    public void testNull() throws Exception {
        JavaMOPMain.main(null);
    }

    @Test
    public void testEmptyStringArr() {
        String[] args = new String[0];
        JavaMOPMain.main(args);
    }

}