package javamop.util;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by hx312 on 3/20/2016.
 */
public class ToolTest {
    @Test
    public void privateConstructorTest() throws NoSuchMethodException {
        Constructor[] constructors = Tool.class.getConstructors();
        for (int i = 0; i < constructors.length; i++) {
            Constructor constructor = constructors[i];
            assertTrue("The constructor of the class Tool should be private",
                    Modifier.isPrivate(constructor.getModifiers()));
        }
    }

    /**
     * This test is in fact modelling the behavior of
     * the original method so that the change of the implementation of
     * the method "public static boolean Tool.isSpecFile(String)"
     * will be noticed.
     * @throws Exception
     */
    @Test
    public void isSpecFile() throws Exception {
        assertTrue(Tool.isSpecFile(".mop"));
        assertTrue(Tool.isSpecFile("a.mop"));
        assertTrue(Tool.isSpecFile("xyz.mop"));

        assertFalse(Tool.isSpecFile("x.rvm"));

        boolean throwEx = false;
        try {
            assertTrue(Tool.isSpecFile(null));
        } catch (NullPointerException npe) {
            throwEx = true;
        }

        assertTrue(throwEx);
    }

}