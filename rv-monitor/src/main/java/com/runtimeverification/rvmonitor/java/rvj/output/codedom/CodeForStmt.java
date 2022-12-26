package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.ICodeVisitor;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;

/**
 * This class represents a for loop.
 *
 * <code>
 * for (init; cond; incr) {
 *   body
 * }
 * </code>
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 *
 */
public class CodeForStmt extends CodeStmt {
    private final CodeStmt init;
    private final CodeExpr cond;
    private final CodeStmt incr;
    private final CodeStmtCollection body;

    public CodeForStmt(CodeStmt init, CodeExpr cond, CodeStmt incr,
            CodeStmtCollection body) {
        this.init = init;
        this.cond = cond;
        this.incr = incr;
        this.body = body;

        this.validate();
    }

    private void validate() {
        if (this.init == null)
            throw new IllegalArgumentException();
        if (this.cond == null)
            throw new IllegalArgumentException();
        if (this.incr == null)
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
        fmt.keyword("for");
        fmt.operator("(");
        this.init.getCode(fmt);
        fmt.operator(";");
        this.cond.getCode(fmt);
        fmt.operator(";");
        this.incr.getCode(fmt);
        fmt.operator(")");

        fmt.openBlock();
        this.body.getCode(fmt);
        fmt.closeBlock();
    }

    @Override
    public void accept(ICodeVisitor visitor) {
        visitor.openScope();
        this.init.accept(visitor);
        this.cond.accept(visitor);
        this.incr.accept(visitor);
        this.body.accept(visitor);
        visitor.closeScope();

        visitor.visit(this.body);
    }
}
