package javamop.output;

import examples.ExamplesIT;
import javamop.JavaMOPMain;
import javamop.JavaMOPOptions;
import javamop.NodeEquivalenceChecker;
import javamop.helper.IOUtils;
import javamop.helper.MOP_Serialization;
import javamop.parser.SpecExtractorTest;
import javamop.parser.ast.MOPSpecFile;
import javamop.util.MOPNameSpace;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by xiaohe on 4/15/16.
 */
@RunWith(Parameterized.class)
public class MOPProcessorTest {
    public static final String inputASTPrefix = SpecExtractorTest.astPrefix;
    public static final String expectedOutputPrefix = inputASTPrefix + "output" + File.separator;

    private MOPSpecFile inputAST;
    private String inputASTPath;

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

        this.inputASTPath = inputASTPrefix + testName + ".ser";

        this.inputAST = MOP_Serialization.readMOPSpecObjectFromFile(inputASTPath);
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

        String actualRVString = processor.generateRVFile(this.inputAST)
                .replaceAll("[\r\n]", "");
        String expectedRVString = IOUtils.readFile(this.output_RVM_FilePath)
                .replaceAll("[\r\n]", "");
        assertEquals("The generated RV String for spec " + this.mopFilePath +
                " is not as expected", expectedRVString, actualRVString);

        MOPSpecFile originalSpecFile = MOP_Serialization.readMOPSpecObjectFromFile(inputASTPath);
        assertTrue("The method for generating .rvm spec should not alter the MOPSpecFile object",
                NodeEquivalenceChecker.equalMOPSpecFiles(originalSpecFile, this.inputAST));
    }

    @Test
    public void generateAJFile() throws Exception {
        MOPProcessor processor = new MOPProcessor(this.aspectName);
        MOPNameSpace.init();

        String actualAJString = processor.generateAJFile(this.inputAST)
                .replaceAll("[\r\n]", "");
        String expectedAJString = IOUtils.readFile(this.output_AJ_FilePath)
                .replaceAll("[\r\n]", "");
        assertEquals("The generated AJ String for spec " + this.mopFilePath +
                " is not as expected", expectedAJString, actualAJString);

        MOPSpecFile originalSpecFile = MOP_Serialization.readMOPSpecObjectFromFile(inputASTPath);
        assertTrue("The method for generating .aj code should not alter the MOPSpecFile object",
                NodeEquivalenceChecker.equalMOPSpecFiles(originalSpecFile, this.inputAST));
    }

}