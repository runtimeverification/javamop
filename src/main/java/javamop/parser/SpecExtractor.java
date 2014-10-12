// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser;

import java.io.File;

import javamop.parser.ast.MOPSpecFile;
import javamop.parser.astex.MOPSpecFileExt;
import javamop.util.MOPException;
import javamop.util.Tool;

/**
 * Class for retrieving specifications from files.
 */
public final class SpecExtractor {

    /**
     * Produce a MOP Specification File object from text input.
     *
     * @param file The specification file.
     * @return The specifications parsed into an object.
     * @throws MOPException If something goes wrong reading or parsing the specification.
     */
    static public MOPSpecFile parse(final File file) throws MOPException {
        String input = "";
        try {
            if (Tool.isSpecFile(file.getName())) {
                input = Tool.convertFileToString(file.getAbsolutePath());
            }

            final MOPSpecFileExt mopSpecFileExt = JavaParserAdapter.parse(input);
            return JavaMOPExtender.translateMopSpecFile(mopSpecFileExt);
        } catch (final Exception e) {
            e.printStackTrace();
            throw new MOPException("Error when parsing a specification file:\n" + e.getMessage());
        }
    }
}
