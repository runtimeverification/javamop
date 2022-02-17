package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.ICodeVisitor;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;

/**
 * This class represents a 'break' statement.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class CodeBreakStmt extends CodeStmt {
    @Override
    public void getCode(ICodeFormatter fmt) {
        fmt.keyword("break");
    }

    @Override
    public void accept(ICodeVisitor visitor) {
    }

    @Override
    public boolean isBlock() {
        return false;
    }

}
