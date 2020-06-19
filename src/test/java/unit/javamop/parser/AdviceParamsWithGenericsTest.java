package unit.javamop.parser;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javamop.parser.ast.mopspec.MOPParameters;
import javamop.parser.astex.MOPSpecFileExt;
import javamop.parser.astex.mopspec.EventDefinitionExt;
import javamop.parser.astex.mopspec.JavaMOPSpecExt;
import javamop.parser.main_parser.JavaMOPParser;
import javamop.parser.main_parser.ParseException;
import org.junit.Test;

import static org.junit.Assert.*;

public class AdviceParamsWithGenericsTest {

    private static final String errIncorrectArgNum = "Unexpected number of parameters";
    private static final String errIncorrectGenericType = "Unexpected number of parameters";

    private InputStream fromResources(Path filePath) {
        ClassLoader classLoader = getClass().getClassLoader();

        return classLoader.getResourceAsStream(filePath.toString());
    }

    private EventDefinitionExt getEvent(String folder, String name) {
        MOPSpecFileExt parsedFile = null;
        try {
            parsedFile = JavaMOPParser.parse(fromResources(Paths.get(folder, name)));
            assertNotNull("Parser neither parsed file nor threw exception", parsedFile);

        } catch (ParseException e) {
            fail("Unexpected ParseException" + e.getMessage());
        }

        List<JavaMOPSpecExt> specs = parsedFile.getSpecs();
        assertEquals("Unexpected number of Spec ASTs", 1, specs.size());
        JavaMOPSpecExt spec = specs.get(0);

        List<EventDefinitionExt> events = spec.getEvents();
        assertEquals("Unexpected number of Event ASTs", 1, events.size());

        return events.get(0);
    }


    @Test
    public void singleBasicParamTest() {
        EventDefinitionExt event = getEvent("single-property", "AdviceParamsWithGenericsBasic.mop");
        MOPParameters adviceParams = event.getMOPParameters();

        assertEquals(errIncorrectArgNum, 1, adviceParams.size());
        assertEquals(errIncorrectGenericType
                    , "List<Foo>"
                    , adviceParams.get(0).getType().toString());
    }

    @Test
    public void multipleBasicParamsTest() {
        EventDefinitionExt event = getEvent("single-property", "AdviceParamsWithGenericsMultipleBasic.mop");
        MOPParameters adviceParams = event.getMOPParameters();

        assertEquals(errIncorrectArgNum, 2, adviceParams.size());
        assertEquals(errIncorrectGenericType
                    , "List<Foo>"
                    , adviceParams.get(0).getType().toString());

        assertEquals(errIncorrectGenericType
                    , "List<Bar>"
                    , adviceParams.get(1).getType().toString());
    }

    @Test
    public void singleNestedParamTest() {
        EventDefinitionExt event = getEvent("single-property", "AdviceParamsWithGenericsNested.mop");
        MOPParameters adviceParams = event.getMOPParameters();

        assertEquals(errIncorrectArgNum, 1, adviceParams.size());
        assertEquals(errIncorrectGenericType
                    , "List<Map<List<Foo>>>"
                    , adviceParams.get(0).getType().toString());
    }


    @Test
    public void multipleNestedParamsTest() {
        EventDefinitionExt event = getEvent("single-property", "AdviceParamsWithGenericsMultipleNested.mop");
        MOPParameters adviceParams = event.getMOPParameters();

        assertEquals(errIncorrectArgNum, 2, adviceParams.size());
        assertEquals(errIncorrectGenericType
                    , "List<Map<List<Foo>>>"
                    , adviceParams.get(0).getType().toString());

        assertEquals(errIncorrectGenericType
                    , "List<Map<List<Bar>>>"
                    , adviceParams.get(1).getType().toString());
    }

    @Test
    public void singleWildcardParamTest() {
        EventDefinitionExt event = getEvent("single-property", "AdviceParamsWithGenericsWildcard.mop");
        MOPParameters adviceParams = event.getMOPParameters();

        assertEquals(errIncorrectArgNum, 1, adviceParams.size());
        assertEquals(errIncorrectGenericType
                    , "List<? extends Foo<Buzz>>"
                    , adviceParams.get(0).getType().toString());
    }

    @Test(expected = ParseException.class)
    public void illegalParamsTest() throws ParseException{
        JavaMOPParser.parse(fromResources(Paths.get( "single-property"
                                                   , "AdviceParamsWithGenericsIllegal.mop")));
    }

}
