package com.runtimeverification.rvmonitor.java.rvj;

import java.io.File;

import com.runtimeverification.rvmonitor.java.rvj.parser.RVMonitorExtender;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.RVMSpecFile;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.RVMSpecFileExt;
import com.runtimeverification.rvmonitor.util.RVMException;
import com.runtimeverification.rvmonitor.util.Tool;

public class SpecExtractor {

    static private String convertFileToString(String path) throws RVMException {
        String content;
        try {
            content = Tool.convertFileToString(path);
        } catch (Exception e) {
            throw new RVMException(e.getMessage());
        }
        return content;
    }

    static private String getAnnotations(String input) throws RVMException {
        String content = "";

        int start = input.indexOf("/*@", 0), end;

        while (start > -1) {
            end = input.indexOf("@*/", start);

            if (end > -1)
                content += input.substring(start + 3, end); // 4 means /*@ + a
            // space
            else
                throw new RVMException("annotation block didn't end");

            start = input.indexOf("/*@", start + 1);
        }
        return content;
    }

    static public String process(File file) throws RVMException {
        if (Tool.isSpecFile(file.getName())) {
            return convertFileToString(file.getAbsolutePath());
        } else if (Tool.isJavaFile(file.getName())) {
            String javaContent = convertFileToString(file.getAbsolutePath());
            String specContent = getAnnotations(javaContent);
            return specContent;
        } else {
            return "";
        }
    }

    static public RVMSpecFile parse(String input) throws RVMException {
        RVMSpecFile rvmSpecFile;
        try {
            // RVMSpecFileExt rvmSpecFileExt = RVMonitorParser.parse(new
            // ByteArrayInputStream(input.getBytes()));
            RVMSpecFileExt rvmSpecFileExt = JavaParserAdapter.parse(input);
            rvmSpecFile = RVMonitorExtender
                    .translateExtendedSpecFile(rvmSpecFileExt);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RVMException("Error when parsing a specification file:\n"
                    + e.getMessage());
        }

        return rvmSpecFile;
    }

}
