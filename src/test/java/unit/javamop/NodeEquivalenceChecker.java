package javamop;

import javamop.parser.SpecExtractor;
import javamop.parser.ast.MOPSpecFile;
import javamop.util.MOPException;
import javamop.helper.ShapeCheckingVisitor;

import java.io.File;

/**
 * Created by hx312 on 3/20/2016.
 */
public class NodeEquivalenceChecker {
    public static void main(String[] args) throws MOPException {
        ShapeCheckingVisitor scv = new ShapeCheckingVisitor();
        MOPSpecFile mopSpecFile = SpecExtractor.parse(new File
                ("A:\\UIUC-SW\\javamop\\target\\release\\javamop\\javamop\\examples\\ERE" +
                 "\\HasNext\\HasNext.mop"));

        MOPSpecFile mopSpecFile2 = SpecExtractor.parse(new File
                ("A:\\UIUC-SW\\javamop\\target\\release\\javamop\\javamop\\examples\\ERE\\HashSet\\HashSet.mop"));

        MOPSpecFile mopSpecFile3 = SpecExtractor.parse(new File
                ("A:\\UIUC-SW\\Debugging\\testMe.mop"));

        System.out.println("mop file one and 2 are the same? " +
                mopSpecFile.accept(scv, mopSpecFile2));

        System.out.println("mop file one and 3 are the same? " +
                mopSpecFile.accept(scv, mopSpecFile3));;

    }
}
