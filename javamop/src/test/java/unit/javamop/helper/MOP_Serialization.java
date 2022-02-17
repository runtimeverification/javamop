package javamop.helper;

import examples.ExamplesIT;
import javamop.parser.SpecExtractor;
import javamop.parser.ast.MOPSpecFile;
import javamop.util.MOPException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by hx312 on 4/14/2016.
 * The implementation is modified from the tutorial:
 * http://www.tutorialspoint.com/java/java_serialization.htm
 */
public class MOP_Serialization {
    /**
     * Method for serializing an mop spec object to a file.
     *
     * @param mopSpecFile The mop spec object that is going to be serialized.
     * @param outputPath  The path in which the serialization of mop spec object will be stored.
     */
    public static void writeMOPSpecObject2File(MOPSpecFile mopSpecFile, String outputPath) {
        try {
            FileOutputStream fileOut =
                    new FileOutputStream(outputPath);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(mopSpecFile);
            out.close();
            fileOut.close();
            System.out.println("Serialized data of spec is stored into " + outputPath);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    /**
     * Deserialize an MOP spec object from a file.
     *
     * @param inputPath The path to the serialization file.
     * @return The MOP spec object.
     */
    public static MOPSpecFile readMOPSpecObjectFromFile(String inputPath) {
        MOPSpecFile mopSpecFile = null;

        try {
            FileInputStream fileIn = new FileInputStream(inputPath);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            mopSpecFile = (MOPSpecFile) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("MOPSpecFile class not found.");
            c.printStackTrace();
        }

        return mopSpecFile;
    }


    public static final int[] selectedTestCases = new int[]{2, 10, 11, 15, 18, 19};

    public static boolean contains(int[] arr, int elem) {
        if (arr == null) return false;

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == elem)
                return true;
        }
        return false;
    }

    /**
     * Generate the trusted AST files, invoked once in version 4.4;
     *
     * @param args
     * @throws MOPException
     */
    public static void main(String[] args) throws MOPException {
        Path outputDir = Paths.get("src" + File.separator + "test" + File.separator
                + "resources");
        //generate some serialization files from mop specs.
        Object[] inputList = ExamplesIT.data().toArray();

        for (int i = 0; i < inputList.length; i++) {
            if (contains(selectedTestCases, i)) {
                Object[] inputI = (Object[]) inputList[i];
                String path = (String) inputI[0];

                File inputSpecFile = new File(path);
                String testName = path.substring(path.lastIndexOf(File.separator) + 1);
                String serFileOutputPath = outputDir.toAbsolutePath().toString()
                        + File.separator + testName + ".ser";
                MOPSpecFile specFile = SpecExtractor.parse(inputSpecFile);
                writeMOPSpecObject2File(specFile, serFileOutputPath);
            }
        }
    }
}
