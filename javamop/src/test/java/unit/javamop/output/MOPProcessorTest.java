package javamop.output;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import examples.ExamplesIT;
import javamop.JavaMOPMain;
import javamop.JavaMOPOptions;
import javamop.helper.IOUtils;
import javamop.helper.MOP_Serialization;
import javamop.parser.SpecExtractor;
import javamop.parser.SpecExtractorTest;
import javamop.parser.ast.MOPSpecFile;
import javamop.util.MOPException;
import javamop.util.MOPNameSpace;
import javamop.util.Tool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Created by xiaohe on 4/15/16.
 */
@RunWith(Parameterized.class)
public class MOPProcessorTest {
    public static final String inputASTPrefix = SpecExtractorTest.astPrefix;
    public static final String expectedOutputPrefix = inputASTPrefix + "output" + File.separator;

    private MOPSpecFile inputAST;

    private String output_AJ_FilePath;
    private String output_RVM_FilePath;

    private String mopFilePath;
    private String aspectName;

    static {
        JavaMOPMain.options = new JavaMOPOptions();
    }


    public MOPProcessorTest(int testId) {
        this.mopFilePath = (String) ((ArrayList<Object[]>) ExamplesIT.data()).get(testId)[0];
        String testName = mopFilePath.substring
                (mopFilePath.lastIndexOf(File.separator) + 1);

        this.aspectName = testName.substring(0, testName.lastIndexOf("."));

        try {
            this.inputAST = SpecExtractor.parse(new File(this.mopFilePath));
        } catch (MOPException e) {
            e.printStackTrace();
        }
        this.output_AJ_FilePath = expectedOutputPrefix + testName + ".aj";
        this.output_RVM_FilePath = expectedOutputPrefix + testName + ".rvm";
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        int[] inputArr = MOP_Serialization.selectedTestCases;
        Object[][] inputObjArr = new Object[inputArr.length][1];
        for (int i = 0; i < inputArr.length; i++) {
            inputObjArr[i][0] = inputArr[i];
        }
        return Arrays.asList(inputObjArr);
    }

    @Test
    public void generateRVFile() throws Exception {
        MOPProcessor processor = new MOPProcessor(this.aspectName);
        MOPNameSpace.init();

        String actualRVString = IOUtils.deleteNewLines(processor.generateRVFile(this.inputAST));
        actualRVString = Tool.changeIndentation(actualRVString, "", "\t");
        String expectedRVString = IOUtils.deleteNewLines(IOUtils.readFile(this.output_RVM_FilePath));
        expectedRVString = Tool.changeIndentation(expectedRVString, "", "\t");

        assertEquals("The generated RV String for spec " + this.mopFilePath +
                " is not as expected", expectedRVString, actualRVString);
    }

    @Test
    public void generateAJFile() throws Exception {
        MOPProcessor processor = new MOPProcessor(this.aspectName);
        MOPNameSpace.init();

        String actualAJString = IOUtils.deleteNewLines(processor.generateAJFile(this.inputAST));
        actualAJString = Tool.changeIndentation(actualAJString, "", "\t");
        String expectedAJString = IOUtils.deleteNewLines(IOUtils.readFile(this.output_AJ_FilePath));
        expectedAJString = Tool.changeIndentation(expectedAJString, "", "\t");

        assertEquals("The generated AJ String for spec " + this.mopFilePath +
                " is not as expected", expectedAJString, actualAJString);
    }

}