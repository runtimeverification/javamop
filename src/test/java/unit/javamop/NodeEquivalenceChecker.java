package javamop;

import javamop.helper.MOP_Serialization;
import javamop.helper.ShapeCheckingVisitor;
import javamop.parser.SpecExtractor;
import javamop.parser.ast.MOPSpecFile;
import javamop.util.MOPException;

import java.io.File;

/**
 * Created by hx312 on 3/20/2016.
 */
public class NodeEquivalenceChecker {
    /**
     * Check whether two MOP spec files have the same syntactic structure.
     *
     * @param file1
     * @param file2
     * @return
     */
    public static boolean equalMOPSpecFiles(MOPSpecFile file1, MOPSpecFile file2) {
        ShapeCheckingVisitor scv = new ShapeCheckingVisitor();

        //the shape checking visitor will traverse the structure of the mop spec file1,
        //and check whether file1 and file2 have the same structure, if so, it will
        //continue shape checking one layer deeper by comparing the children's structures.
        //if some part does not match between the two structures, then return false immediately.
        return file1.accept(scv, file2);
    }

    public static void main(String[] args) throws MOPException {
        File file1 = new File
                ("examples\\EndProgram\\FileClose\\FileClose.mop");
        MOPSpecFile mopSpecFile = SpecExtractor.parse(file1);

        MOP_Serialization.writeMOPSpecObject2File(mopSpecFile,
                file1.getParent() + "\\FileClose.ser");

        MOPSpecFile restoredSpec1 =
                MOP_Serialization.readMOPSpecObjectFromFile
                        (file1.getParent() + "\\FileClose.ser");

        MOPSpecFile mopSpecFile4 = SpecExtractor.parse(new File
                ("C:\\Users\\hx312\\Documents\\jmop-tmp\\resources\\FileClose.mop"));


        System.out.println("mop file one and 4 are the same? " +
                equalMOPSpecFiles(restoredSpec1, mopSpecFile4));

    }
}
