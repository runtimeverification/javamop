// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop;

import java.io.File;

import javamop.parser.JavaMOPExtender;
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
     * Retrieve all the annotation blocks of the form /*@ ... @* /. They are concatenated
     * together.
     * @param input The program source.
     * @return The annotation blocks concatenated together.
     * @throws MOPException When an annotation block does not end.
     */
    static private String getAnnotations(final String input) throws MOPException {
        String content = "";
        
        int start = input.indexOf("/*@", 0), end;
        
        while (start > -1) {
            end = input.indexOf("@*/", start);
            
            if (end > -1)
                content += input.substring(start + 3, end); // 4 means /*@ + a space
                else
                    throw new MOPException("annotation block didn't end");
                
                start = input.indexOf("/*@", start + 1);
        }
        return content;
    }
    
    /**
     * Retrieve the specification information from a File. If it is a Java file, return
     * the annotations in the file. If it is a specification file, return the entire file.
     * @param file The file to read from.
     * @return The specification information in the file.
     * @throws MOPException If something goes wrong in reading the file.
     */
    static public String process(final File file) throws MOPException {
        if (Tool.isSpecFile(file.getName())) {
            return convertFileToString(file.getAbsolutePath());
        } else if (Tool.isJavaFile(file.getName())) {
            final String javaContent = convertFileToString(file.getAbsolutePath());
            final String specContent = getAnnotations(javaContent);
            return specContent;
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
