package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.ICodeVisitor;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;

/**
 * This class represents the 'this' expression.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class CodeThisRefExpr extends CodeExpr {
    public CodeThisRefExpr(CodeType type) {
        super(type);
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        fmt.keyword("this");
    }

    @Override
    public void accept(ICodeVisitor visitor) {
    }
}
