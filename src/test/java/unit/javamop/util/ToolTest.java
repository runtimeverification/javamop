package javamop.util;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

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

    @Test
    public void isSpecFile() throws Exception {

    }

    @Test
    public void isListFile() throws Exception {

    }

    @Test
    public void findBlockStart() throws Exception {

    }

    @Test
    public void findBlockEnd() throws Exception {

    }

    @Test
    public void removeComments() throws Exception {

    }

    @Test
    public void changeIndentation() throws Exception {

    }

    @Test
    public void getFileName() throws Exception {

    }

    @Test
    public void convertFileToString() throws Exception {

    }

    @Test
    public void convertFileToString1() throws Exception {

    }

    @Test
    public void convertFileToStringSet() throws Exception {

    }

    @Test
    public void polishPath() throws Exception {

    }

    @Test
    public void getConfigPath() throws Exception {

    }

    @Test
    public void deleteDirectory() throws Exception {

    }
}