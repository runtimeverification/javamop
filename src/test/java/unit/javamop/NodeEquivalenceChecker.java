package unit.javamop;

import javamop.parser.SpecExtractor;
import javamop.parser.ast.MOPSpecFile;
import javamop.util.MOPException;
import unit.javamop.helper.ShapeCheckingVisitor;

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

        mopSpecFile.accept(scv, mopSpecFile2);
    }
}
