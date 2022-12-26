package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.ICodeVisitor;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;

/**
 * This class represents a negation expression; e.g., <code>
 * !nested
 * </code>
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class CodeNegExpr extends CodeExpr {
    private final CodeExpr nested;

    public CodeNegExpr(CodeExpr nested) {
        super(nested.getType());

        this.nested = nested;
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        fmt.operator("!");
        this.nested.getCode(fmt);
    }

    @Override
    public void accept(ICodeVisitor visitor) {
        this.nested.accept(visitor);
    }
}
