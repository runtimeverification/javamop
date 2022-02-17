package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import java.util.ArrayList;
import java.util.List;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.ICodeVisitor;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;

/**
 * This class represents a comment. This class can hold multiple lines of
 * comment.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class CodeCommentStmt extends CodeStmt {
    private final List<String> lines;

    public CodeCommentStmt(String... lines) {
        this.lines = new ArrayList<String>();
        for (String line : lines)
            this.lines.add(line);
    }

    @Override
    public boolean isBlock() {
        // This will prevent from printing ';'.
        return true;
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        for (String line : this.lines)
            fmt.comment(line);
    }

    @Override
    public void accept(ICodeVisitor visitor) {
    }
}
