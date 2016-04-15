package javamop.output;

import examples.ExamplesIT;
import javamop.JavaMOPMain;
import javamop.helper.IOUtils;
import javamop.helper.MOP_Serialization;
import javamop.parser.SpecExtractorTest;
import javamop.parser.ast.MOPSpecFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

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


    public MOPProcessorTest(int testId) {
        this.mopFilePath = (String) ((ArrayList<Object[]>) ExamplesIT.data()).get(testId)[0];
        String testName = mopFilePath.substring
                (mopFilePath.lastIndexOf(File.separator) + 1);
        String inputASTPath = inputASTPrefix + testName + ".ser";

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
        MOPProcessor processor = new MOPProcessor("");
        String actualRVString = processor.generateRVFile(this.inputAST);
        String expectedRVString = IOUtils.readFile(this.output_RVM_FilePath);
        assertTrue("The generated RV String for spec " + this.mopFilePath +
                " is not as expected", expectedRVString.equals(actualRVString));

        //TODO check for side effects

    }

    @Test
    public void generateAJFile() throws Exception {
        MOPProcessor processor = new MOPProcessor("");
        String actualAJString = processor.generateAJFile(this.inputAST);
        String expectedAJString = IOUtils.readFile(this.output_AJ_FilePath);
        assertTrue("The generated AJ String for spec " + this.mopFilePath +
                " is not as expected", expectedAJString.equals(actualAJString));

        //TODO check for side effects

    }

}