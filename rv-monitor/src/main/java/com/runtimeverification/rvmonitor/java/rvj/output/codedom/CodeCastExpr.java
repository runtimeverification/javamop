package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.ICodeVisitor;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;

/**
 * This class represents a cast expression; e.g., <code>
 * (type)source;
 * </code>
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class CodeCastExpr extends CodeExpr {
    private final CodeExpr source;

    public CodeCastExpr(CodeType type, CodeExpr source) {
        super(type);

        this.source = source;

        this.validate();
    }

    private void validate() {
        if (this.source == null)
            throw new IllegalArgumentException();
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        fmt.operator("(");
        fmt.type(this.type);
        fmt.operator(")");
        this.source.getCode(fmt);
    }

    @Override
    public void accept(ICodeVisitor visitor) {
        this.source.accept(visitor);
    }
}
