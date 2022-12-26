package com.runtimeverification.rvmonitor.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.jar.Manifest;

/**
 * @author fengchen
 * @author pdaian
 *
 *         A set of tool functions
 */
public class Tool {
    public static String getSpecFileDotExt() {
        return "." + getSpecFileExt();
    }

    public static String getSpecFileExt() {
        return "rvm";
    }

    public static boolean isJavaFile(String path) {
        return path.endsWith(".java");
    }

    public static boolean isSpecFile(String path) {
        return path.endsWith(getSpecFileDotExt());
    }

    public static boolean isListFile(String path) {
        return path.endsWith(".lst");
    }

    public static int findBlockStart(String str, int pos) {
        return str.lastIndexOf("{", pos) + 1;
    }

    public static int findBlockEnd(String str, int pos) {
        int c = 0;
        int i = str.indexOf("}", pos);
        int j = str.indexOf("{", pos);
        while ((i > -1) && (j > -1)) {
            if (i < j) {
                if (c > 0) {
                    c--;
                    i = str.indexOf("}", i + 1);
                } else
                    return i;
            } else {
                c++;
                j = str.indexOf("{", j + 1);
            }
        }
        if (j == -1) {
            while ((c > 0) && (i > -1)) {
                i = str.indexOf("}", i + 1);
                c--;
            }
        }
        // if the input is well formed, then i should be larger than 0 here
        return i;
    }

    public static String removeComments(String input) {
        int i = input.indexOf("//");
        while (i > -1) {
            int j = input.indexOf("\n");
            if (j > -1)
                input = input.substring(0, i)
                        + input.substring(input.indexOf("\n", i) + 1,
                                input.length());
            else
                input = input.substring(0, i);
            i = input.indexOf("//");
        }
        i = input.indexOf("/*");
        while (i > -1) {
            if (input.indexOf("*/") > -1) {
                input = input.substring(0, i)
                        + input.substring(input.indexOf("*/", i) + 2,
                                input.length());
                i = input.indexOf("/*");
            } else
                i = -1;
        }
        return input;
    }

    /**
     * format the code to insert appropriate indentation
     * 
     * @param content
     *            : the code to reformat
     * @param prefix
     *            : the current indentation
     * @param offset
     *            : the string used in indentation, like "\t"
     * @return
     */
    public static String changeIndentation(String content, String offset,
            String prefix) {
        StringBuffer output = new StringBuffer();
        BufferedReader br = new BufferedReader(new StringReader(content));
        String aLine = "";
        boolean emptyline = false;
        int lineNum = 0;
        try {
            for (aLine = br.readLine(); aLine != null; aLine = br.readLine()) {
                lineNum++;
                aLine = aLine.trim();

                if (aLine.length() == 0) {
                    if (emptyline)
                        continue;
                    emptyline = true;
                    output.append("\n");
                    continue;
                }
                emptyline = false;

                String str = removeComments(aLine).trim();

                if (str.endsWith("}")) {
                    String s = str.substring(0, str.length() - 1).trim();
                    if (s.length() == 0) {
                        offset = offset.substring(0,
                                offset.length() - prefix.length());
                    }
                } else if (str.startsWith("} else") || str.startsWith("}else")) {
                    offset = offset.substring(0,
                            offset.length() - prefix.length());
                } else if (str.startsWith("} catch")
                        || str.startsWith("}catch")) {
                    offset = offset.substring(0,
                            offset.length() - prefix.length());
                } else if (str.startsWith("} finally")
                        || str.startsWith("}finally")) {
                    offset = offset.substring(0,
                            offset.length() - prefix.length());
                } else if (str.startsWith("} while")
                        || str.startsWith("}while")) {
                    offset = offset.substring(0,
                            offset.length() - prefix.length());
                } else if (str.endsWith("};")) {
                    offset = offset.substring(0,
                            offset.length() - prefix.length());
                } else if (str.endsWith("},")) {
                    offset = offset.substring(0,
                            offset.length() - prefix.length());
                }
                aLine = offset + aLine;
                output.append(aLine + "\n");
                str = removeComments(aLine).trim();
                if (str.endsWith("{")) {
                    String s = str.substring(0, str.length() - 1).trim();
                    if (!s.endsWith(",") && !s.endsWith("{"))
                        offset += prefix;
                }
            }
        } catch (IndexOutOfBoundsException ie) {
            // only happens where there is format problems
            return content;
        } catch (Exception e) {
            System.out.println("weird error!" + e.getMessage() + " at " + aLine
                    + ", " + lineNum);
            // System.out.println(output);
            e.printStackTrace();
            return content;
        }
        return output.toString();
    }

    public static String formatGeneratedCode(String content, String prefix) {
        StringBuffer output = new StringBuffer();
        for (int i = content.indexOf("/*+"); i > -1; i = content.indexOf("/*+")) {
            output.append(content.substring(0, i));
            int j = content.indexOf("+*/");
            if (j > -1) {
                String generatedCode = content.substring(i, j + 3);
                String str = content.substring(0, i).trim();
                String lastLine = str.lastIndexOf('\n') > -1 ? str
                        .substring(str.lastIndexOf('\n') + 1) : str;
                while (lastLine.startsWith("\r"))
                    lastLine = lastLine.substring(1);
                StringBuffer offset = new StringBuffer();
                for (int ii = 0; ii < lastLine.length(); ii++) {
                    if (lastLine.charAt(ii) == ' ')
                        offset.append(' ');
                    else if (lastLine.charAt(ii) == '\t')
                        offset.append('\t');
                    else
                        break;
                }
                if (lastLine.endsWith("{"))
                    offset.append(prefix);
                generatedCode = changeIndentation(generatedCode,
                        offset.toString(), prefix);
                output.append(generatedCode);
                content = content.substring(j + 3);
            }
        }
        output.append(content);
        return output.toString();
    }

    public static String getPackage(String str) {
        int i = str.indexOf("package");
        if (i > -1)
            return str.substring(i, str.indexOf("\n", i) + 1);
        else
            return "";
    }

    public static String getImports(String str) throws RVMException {
        BufferedReader reader = new BufferedReader(new StringReader(str));
        String result = "";
        try {
            String aLine = reader.readLine();
            while (aLine != null) {
                aLine = aLine.trim();
                if (aLine.startsWith("import ")) {
                    result += aLine + "\n";
                }
                aLine = reader.readLine();
            }
        } catch (IOException e) {
            throw new RVMException(e);
        }
        return result;
    }

    public static String getFileName(String path) {
        int i = path.lastIndexOf(File.separator);
        int j = path.lastIndexOf(".");
        return path.substring(i + 1, j);
    }

    public static String convertFileToString(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] b = new byte[fileInputStream.available()];
        fileInputStream.read(b);
        fileInputStream.close();
        String content = new String(b);
        return content;
    }

    public static String convertFileToString(String path) throws IOException {
        return convertFileToString(new File(path));
    }

    public static String polishPath(String path) {
        if (path.indexOf("%20") > 0)
            path = path.replaceAll("%20", " ");

        return path;
    }

    public static String stripFilenameExtension(String path) {
        if (path == null) {
            return null;
        }
        int sepIndex = path.lastIndexOf(".");
        return (sepIndex != -1 ? path.substring(0, sepIndex) : path);
    }

    public static boolean isRVMonitorInJarFile() {
        try {
            Class<?> mainClass = Class
                    .forName("com.runtimeverification.rvmonitor.java.rvj.Main");
            ClassLoader loader = mainClass.getClassLoader();
            String mainClassPath = loader.getResource(
                    "com/runtimeverification/rvmonitor/java/rvj/Main.class")
                    .toString();

            if (mainClassPath
                    .endsWith(".jar!/com/runtimeverification/rvmonitor/java/rvj/Main.class")
                    && mainClassPath.startsWith("jar:")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getConfigPath() {
        String configPath;
        try {
            Class<?> mainClass = Class
                    .forName("com.runtimeverification.rvmonitor.java.rvj.Main");
            ClassLoader loader = mainClass.getClassLoader();
            String mainClassPath = loader.getResource(
                    "com/runtimeverification/rvmonitor/java/rvj/Main.class")
                    .toString();

            if (mainClassPath
                    .endsWith(".jar!/com/runtimeverification/rvmonitor/java/rvj/Main.class")
                    && mainClassPath.startsWith("jar:")) {
                String jarFilePath;
                jarFilePath = mainClassPath
                        .substring(
                                "jar:file:".length(),
                                mainClassPath.length()
                                        - "!/com/runtimeverification/rvmonitor/java/rvj/Main.class"
                                                .length());
                jarFilePath = Tool.polishPath(jarFilePath);

                configPath = new File(jarFilePath).getParentFile().getParent()
                        + File.separator + "config";
            } else {
                String packageFilePath;

                packageFilePath = mainClassPath.substring("file:".length(),
                        mainClassPath.length() - "/Main.class".length());
                packageFilePath = Tool.polishPath(packageFilePath);

                configPath = new File(packageFilePath).getParentFile()
                        .getParent() + File.separator + "config";
            }

            return configPath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Print version information from Jar MANIFEST to console
     */
    public static void printVersionMessage() {
        try {
            Class currentClass = Tool.class;
            URL url = currentClass.getResource('/'
                    + currentClass.getName().replace('.', '/') + ".class");
            JarURLConnection conn = (JarURLConnection) url.openConnection();
            Manifest mf = conn.getManifest();
            String revision = mf.getMainAttributes().getValue(
                    "Implementation-Revision");
            String branch = mf.getMainAttributes().getValue(
                    "Implementation-Branch");
            Date date = new Date(Long.parseLong(mf.getMainAttributes()
                    .getValue("Implementation-Date")));
            System.out.println("RV-Monitor version "
                    + currentClass.getPackage().getImplementationVersion());
            System.out.println("Git revision: " + revision);
            System.out.println("Git branch: " + branch);
            System.out.println("Build date: " + date);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not load version info. Check your build system?");
        }
    }
}
