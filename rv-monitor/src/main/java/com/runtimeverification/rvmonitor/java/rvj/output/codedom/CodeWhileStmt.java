package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.ICodeVisitor;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;

/**
 * This class represents a for loop.
 *
 * <code>
 * while (cond) {
 *   body
 * }
 * </code>
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 *
 */
public class CodeWhileStmt extends CodeStmt {
    private final CodeExpr cond;
    private final CodeStmtCollection body;

    public CodeWhileStmt(CodeExpr cond, CodeStmtCollection body) {
        this.cond = cond;
        this.body = body;

        this.validate();
    }

    private void validate() {
        if (this.cond == null)
            throw new IllegalArgumentException();
        if (this.body == null)
            throw new IllegalArgumentException();
    }

    @Override
    public boolean isBlock() {
        return true;
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        fmt.keyword("while");
        fmt.operator("(");
        this.cond.getCode(fmt);
        fmt.operator(")");

        fmt.openBlock();
        this.body.getCode(fmt);
        fmt.closeBlock();
    }

    @Override
    public void accept(ICodeVisitor visitor) {
        visitor.openScope();
        this.cond.accept(visitor);
        this.body.accept(visitor);
        visitor.closeScope();

        visitor.visit(this.body);
    }
}
