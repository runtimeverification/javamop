package javamop.helper;

import examples.ExamplesIT;
import javamop.JavaMOPMain;
import javamop.JavaMOPOptions;
import javamop.output.MOPProcessor;
import javamop.parser.SpecExtractor;
import javamop.parser.SpecExtractorTest;
import javamop.parser.ast.MOPSpecFile;
import javamop.util.MOPException;
import javamop.util.MOPNameSpace;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by xiaohe on 4/15/16.
 */
public class ExpectedOutputGenerator {
    /**
     * Generate the trusted AST files, invoked once in version 4.4;
     *
     * @param args
     * @throws MOPException
     */
    public static void main(String[] args) throws MOPException, IOException {
        Path outputDir = Paths.get("src" + File.separator + "test" + File.separator
                + "resources");
        //generate some serialization files from mop specs.
        Object[] inputList = ExamplesIT.data().toArray();
        String expectedOutputPrefix = SpecExtractorTest.astPrefix + "output" + File.separator;

        JavaMOPMain.options = new JavaMOPOptions();


        for (int i = 0; i < inputList.length; i++) {
            if (MOP_Serialization.contains(MOP_Serialization.selectedTestCases, i)) {
                String mopFilePath = (String) ((ArrayList<Object[]>) ExamplesIT.data()).get(i)[0];
                String testName = mopFilePath.substring
                        (mopFilePath.lastIndexOf(File.separator) + 1);
                String aspectName = testName.substring(0, testName.lastIndexOf("."));
                String inputASTPath = SpecExtractorTest.astPrefix + testName + ".ser";

                MOPSpecFile inputAST = MOP_Serialization.readMOPSpecObjectFromFile(inputASTPath);
                MOPProcessor processor = new MOPProcessor(aspectName);
                MOPNameSpace.init();

                String actualRVString = processor.generateRVFile(inputAST);
                String actualAJString = processor.generateAJFile(inputAST);

                String ajOutputPath = expectedOutputPrefix + testName + ".aj";
                String rvOutputPath = expectedOutputPrefix + testName + ".rvm";

                IOUtils.write2File(actualAJString, Paths.get(ajOutputPath));
                IOUtils.write2File(actualRVString, Paths.get(rvOutputPath));
            }
        }
    }
}
