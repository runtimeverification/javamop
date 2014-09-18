// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * 
 * Class to combine a Java file with an AspectJ file together.
 * 
 * @author Qingzhou Luo
 * */
public class AJFileCombiner {
    
    /**
     * 
     * First parameter: path to java file
     * Second parameter: path to aj file
     * 
     * */
    public static void main(String[] args) {
        List<String> javaImports = new ArrayList<String>();
        List<String> ajImports = new ArrayList<String>();
        StringBuffer javaCode = new StringBuffer();
        StringBuffer ajCode = new StringBuffer();
        File javaFile = new File(args[0]);
        File ajFile = new File(args[1]);
        String pack = null;
        try {
            Scanner scanner = new Scanner(ajFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().startsWith("import")) {
                    ajImports.add(line);
                }
                else if (line.trim().startsWith("package")) {
                    pack = line;
                }
                else {
                    ajCode.append(line);
                    ajCode.append("\n");
                }
            }
            scanner.close();
            
            scanner = new Scanner(javaFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().startsWith("import")) {
                    javaImports.add(line);
                }
                else if (line.trim().startsWith("package")) {
                    pack = line;
                }
                else {
                    if (line.contains("public final class")) {
                        line = line.replace("public final class", "class");
                    }
                    javaCode.append(line);
                    javaCode.append("\n");
                }
            }
            
            List<String> combinedImports = combineList(javaImports, ajImports);
            StringBuffer results = new StringBuffer();
            if (pack != null) {
                results.append(pack);
                results.append("\n");
            }
            for (String s : combinedImports) {
                results.append(s);
                results.append("\n");
            }
            results.append(javaCode);
            results.append(ajCode);
            FileWriter fw = new FileWriter(ajFile);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(results.toString());
            bw.flush();
            bw.close();
            scanner.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static List<String> combineList(List<String> one, List<String> two) {
        List<String> result = new ArrayList<String>(one);
        for (String s : two) {
            if (!result.contains(s)) {
                result.add(s);
            }
        }
        return result;
    }
}
