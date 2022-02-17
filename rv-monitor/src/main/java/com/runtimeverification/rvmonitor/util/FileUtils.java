package com.runtimeverification.rvmonitor.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utility class for interacting with files.
 * 
 * @author A. Cody Schuffelen
 */
public class FileUtils {

    /**
     * Extract a literal file from a jar. Only files in /jar_include/ are copied
     * over verbatim to the jar file, so /jar_include/ is included as part of
     * the path.
     * 
     * @param classSource
     *            A class in the relevant package of the jar.
     * @param path
     *            The path of the file, relative to /jar_includes/ in the
     *            package of {@link classSource}.
     */
    public static String extractFileFromJar(Class<?> classSource, String path) {
        String pathToClass = classSource.getPackage().getName()
                .replace('.', '/');
        String fullPath = "/" + pathToClass + "/jar_include/" + path;
        InputStream input = classSource.getResourceAsStream(fullPath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        StringBuilder builder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return builder.toString();
    }

    /**
     * Extract a literal file from a jar. Only files in /jar_include/ are copied
     * over verbatim to the jar file, so /jar_include/ is included as part of
     * the path.
     * 
     * @param classSource
     *            An instance of a class in the relevant package of the jar.
     * @param path
     *            The path of the file, relative to /jar_includes/ in the
     *            package of {@link classSource}.
     */
    public static String extractFileFromJar(Object classSource, String path) {
        return extractFileFromJar(classSource.getClass(), path);
    }
}