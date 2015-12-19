// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.util;


import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * @author fengchen
 *
 * A set of tool functions
 */
public final class Tool {
    
    /**
     * To prevent instantiation.
     */
    private Tool() {
        
    }

    /**
     * Determine if a path belongs to a Specification file based on its extension.
     * @param path The path of the file.
     * @return If the file is a specification file.
     */
    public static boolean isSpecFile(final String path) {
        return path.endsWith(".mop");
    }
    
    /**
     * Determine if a path belongs to a List file based on its extension.
     * @param path The path of the file.
     * @return If the file is a list file.
     */
    public static boolean isListFile(final String path) {
        return path.endsWith(".lst");
    }
    
    /**
     * Find the location of next opening curly bracket.
     * @param str The string to search.
     * @param pos The index to start searching.
     * @return The index of the next open curly bracket in {@code str} after {@code pos}.
     */
    public static int findBlockStart(final String str, final int pos) {
        return str.lastIndexOf("{", pos) + 1;
    }
    
    /**
     * Find the matching close curly bracket to an open curly bracket.
     * @param str The string to search through.
     * @param pos The index to start searching.
     * @return The index of the matching close curly bracket, or a number less than 0 in invalid input.
     */
    public static int findBlockEnd(final String str, final int pos) {
        int c = 0;
        int i = str.indexOf("}", pos);
        int j = str.indexOf("{", pos);
        while ((i > -1) && (j > -1)) {
            if (i < j){
                if (c > 0){
                    c -- ;
                    i = str.indexOf("}", i+1);
                }
                else
                    return i;
            } else{
                c ++;
                j = str.indexOf("{", j+1);
            } 
        }
        if (j == -1){
            while ((c > 0) && (i > -1)){
                i = str.indexOf("}", i + 1);
                c --;
            }
        }
        // if the input is well formed, then i should be larger than 0 here
        return i;
    }
    
    /**
     * Strip line and block comments from a source code string.
     * @param input The source code.
     * @return The source code, stripped of comments.
     */
    public static String removeComments(String input) {
        int i = input.indexOf("//");
        while (i>-1){
            int j = input.indexOf("\n");
            if (j > -1)
                input = input.substring(0, i) + input.substring(input.indexOf("\n", i)+1, 
                    input.length());
            else
                input = input.substring(0, i);
            i = input.indexOf("//");
        }
        i = input.indexOf("/*");
        while (i>-1){
            if (input.indexOf("*/") > -1) {
                input = input.substring(0, i) + input.substring(input.indexOf("*/", i)+2, 
                    input.length());
                i = input.indexOf("/*");
            } else
                i = -1;
        }
        return input;
    }
    
    /**
     * Format the code to insert appropriate indentation.
     * @param content: the code to reformat
     * @param prefix: the current indentation
     * @param offset: the string used in indentation, like "\t"
     * @return The properly indented code.
     */
    public static String changeIndentation(final String content, String offset, 
            final String prefix) {
        StringBuffer output = new StringBuffer();
        BufferedReader br = new BufferedReader(new StringReader(content));
        String aLine = "";
        boolean emptyline = false;
        int lineNum = 0;
        try {
            for (aLine = br.readLine(); aLine != null; aLine = br.readLine()){
                lineNum++;
                aLine = aLine.trim();
                
                if (aLine.length() == 0)
                {
                    if(emptyline)
                        continue;
                    emptyline = true;
                    output.append("\n");
                    continue;
                }
                emptyline = false;
                
                String str = removeComments(aLine).trim();
                
                if (str.endsWith("}")){
                    String s = str.substring(0, str.length() - 1).trim();
                    if (s.length() == 0){
                        offset = offset.substring(0, offset.length() - prefix.length());
                    }
                }
                else if (str.startsWith("} else") || str.startsWith("}else")){
                    offset = offset.substring(0, offset.length() - prefix.length());
                }
                else if (str.startsWith("} catch") || str.startsWith("}catch")){
                    offset = offset.substring(0, offset.length() - prefix.length());
                }
                else if (str.startsWith("} finally") || str.startsWith("}finally")){
                    offset = offset.substring(0, offset.length() - prefix.length());
                }
                else if (str.startsWith("} while") || str.startsWith("}while")){
                    offset = offset.substring(0, offset.length() - prefix.length());
                }
                else if (str.endsWith("};")){
                    offset = offset.substring(0, offset.length() - prefix.length());
                }
                else if (str.endsWith("},")){
                    offset = offset.substring(0, offset.length() - prefix.length());
                }

                aLine = (str.matches("raw\\s*:") ? "" : offset) + aLine;
                output.append(aLine + "\n");
                str = removeComments(aLine).trim();
                if (str.endsWith("{")){
                    String s = str.substring(0, str.length()-1).trim();
                    if (!s.endsWith(",") && !s.endsWith("{"))
                        offset += prefix;
                }
            }
        } catch (Exception e){
            System.out.println("weird error!" + e.getMessage() + " at " + aLine + ", " + lineNum);
            //System.out.println(output);
            e.printStackTrace();
            return content;
        }
        return output.toString();
    }
    
    /**
     * Retrieve the file name of a given path, stripping its directory and file extension.
     * @param path The path to the filename.
     * @return The name of the file, without path or suffix.
     */
    public static String getFileName(String path){
        int i = path.lastIndexOf(File.separator);
        int j = path.lastIndexOf(".");
        return path.substring(i+1, j);        
    }
    
    /**
     * Retrieve the contents of the given file.
     * @param file The file to read.
     * @return The contents of the file.
     * @throws IOException If there is an error in reading the file.
     */
    public static String convertFileToString(final File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] b = new byte[fileInputStream.available()];
        fileInputStream.read(b);
        fileInputStream.close();
        String content = new String(b);
        return content;
    }
    
    /**
     * Retrieve the contents of the given file.
     * @param path The path to the file to read.
     * @return The contents of the file.
     * @throws IOException If there is an error in reading the file.
     */
    public static String convertFileToString(final String path) throws IOException {
        return convertFileToString(new File(path));
    }

    /**
     * Retrieve the contents of the given file and return all lines in a set
     * @param path The path to the file to read.
     * @return The contents of the file as a set.
     * @throws IOException If there is an error in reading the file.
     */
    public static Set<String> convertFileToStringSet(final String path) throws IOException {
        Set<String> contents = new HashSet<String>();
        Scanner sc = new Scanner(new File(path));
        while (sc.hasNext()) {
            contents.add(sc.next());
        }
        return contents;
    }
    
    /**
     * Make a filesystem path look nice for windows.
     * @param path The path to improve.
     * @return The aesthetically improved path.
     */
    public static String polishPath(final String path) {
        return path.replaceAll("%20", " ");
    }
    
    /**
     * Retrieve the path of the configuration file.
     * @return The string path to the configuration file.
     */
    public static String getConfigPath(){
        try {
            final Class<?> mainClass = Class.forName("javamop.JavaMOPMain");
            final ClassLoader loader = mainClass.getClassLoader();
            final String mainClassPath = loader.getResource("javamop/JavaMOPMain.class").toString();
            
            if (mainClassPath.endsWith(".jar!/javamop/JavaMOPMain.class") && 
                    mainClassPath.startsWith("jar:")) {
                String jarFilePath;
                jarFilePath = mainClassPath.substring("jar:file:".length(), 
                    mainClassPath.length() - "!/javamop/JavaMOPMain.class".length());
                jarFilePath = Tool.polishPath(jarFilePath);
                
                return new File(jarFilePath).getParentFile().getParent() + File.separator + 
                    "config";
            } else {
                String packageFilePath;
                
                packageFilePath = mainClassPath.substring("file:".length(), 
                    mainClassPath.length() - "/JavaMOPMain.class".length());
                packageFilePath = Tool.polishPath(packageFilePath);
                
                return new File(packageFilePath).getParentFile().getParent() + 
                    File.separator + "config";
            }
        } catch  (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Delete a directory and all its contents.
     *
     * @param path The path of the directory to delete.
     * @throws java.io.IOException If the underlying FileUtils throws IOException.
     */
    public static void deleteDirectory(final Path path) throws IOException {
        // http://stackoverflow.com/a/8685959
        FileUtils.deleteDirectory(path.toFile());
    }
}
