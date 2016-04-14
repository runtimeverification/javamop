package javamop.parser;

import examples.ExamplesIT;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Created by He Xiao on 3/19/2016.
 * Check the parsing function provided in javamop.parser.SpecExtractor class:
 *  The method 'static public MOPSpecFile parse(final File file);' will
 *  parse an mop specification file and get a MOPSpecFile object.
 *  The unit tests defined here will compare the MOPSpecFile output (AST)
 *  produced by current implementation of the parser module and the trusted one (AST)
 *  produced by JavaMOP V4.4 (Github)
 */
@RunWith(Parameterized.class)
public class SpecExtractorTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Collection<Object[]> fullList = ExamplesIT.data();
        Collection<Object[]> inputPaths = new ArrayList<>();

        //TODO: select some tests to run

        return inputPaths;
    }

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void parse() throws Exception {

    }
}