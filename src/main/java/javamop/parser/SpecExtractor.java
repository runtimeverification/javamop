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
     * Retrieve the text of the file at the given path.
     * @param path The path of the file.
     * @return A string with the content of the file.
     * @throws javamop.util.MOPException If something goes wrong opening or reading the file.
     */
    static private String convertFileToString(final String path) throws MOPException {
        try {
            return Tool.convertFileToString(path);
        } catch (Exception e) {
            throw new MOPException(e.getMessage());
        }
    }
    
    /**
     * Retrieve the specification information from a File.
     * If it is a specification file, return the entire file. Otherwise return empty string.
     * @param file The file to read from.
     * @return The specification information in the file.
     * @throws MOPException If something goes wrong in reading the file.
     */
    static public String readSpecFile(final File file) throws MOPException {
        if (Tool.isSpecFile(file.getName())) {
            return convertFileToString(file.getAbsolutePath());
        } else {
            return "";
        }
    }
    
    /**
     * Produce a MOP Specification File object from text input.
     * @param input The specification as text.
     * @return The specifications parsed into an object.
     * @throws MOPException If something goes wrong reading or parsing the specification.
     */
    static public MOPSpecFile parse(final String input) throws MOPException {
        try {
            final MOPSpecFileExt mopSpecFileExt = JavaParserAdapter.parse(input);
            return JavaMOPExtender.translateMopSpecFile(mopSpecFileExt);
        } catch (final Exception e) {
            e.printStackTrace();
            throw new MOPException("Error when parsing a specification file:\n" + e.getMessage());
        }
    }
    
}
