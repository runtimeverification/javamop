package javamop;

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
        MOPSpecFile mopSpecFile = SpecExtractor.parse(new File
                ("A:\\UIUC-SW\\javamop\\target\\release\\javamop\\javamop\\examples\\ERE" +
                        "\\HasNext\\HasNext.mop"));

        MOPSpecFile mopSpecFile2 = SpecExtractor.parse(new File
                ("A:\\UIUC-SW\\javamop\\target\\release\\javamop\\javamop\\examples\\ERE\\HashSet\\HashSet.mop"));

        MOPSpecFile mopSpecFile3 = SpecExtractor.parse(new File
                ("A:\\UIUC-SW\\Debugging\\testMe.mop"));

        MOPSpecFile mopSpecFile4 = SpecExtractor.parse(new File
                ("A:\\UIUC-SW\\javamop\\target\\release\\javamop\\javamop\\examples\\ERE" +
                        "\\HasNext\\HasNext.mop"));

        System.out.println("mop file one and 2 are the same? " +
                equalMOPSpecFiles(mopSpecFile, mopSpecFile2));

        System.out.println("mop file one and 3 are the same? " +
                equalMOPSpecFiles(mopSpecFile, mopSpecFile3));
        ;

        System.out.println("mop file one and 4 are the same? " +
                equalMOPSpecFiles(mopSpecFile, mopSpecFile4));
        ;
    }
}
