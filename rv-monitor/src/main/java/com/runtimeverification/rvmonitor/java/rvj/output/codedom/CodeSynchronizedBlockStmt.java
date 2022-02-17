package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.ICodeVisitor;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;

/**
 * This class represents a Java's synchronized block; e.g., <code>
 * synchronized (obj) {
 *   body
 * }
 * </code>
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class CodeSynchronizedBlockStmt extends CodeStmt {
    private final CodeExpr targetobject;
    private final CodeStmtCollection body;

    public CodeSynchronizedBlockStmt(CodeExpr targetobject,
            CodeStmtCollection body) {
        this.targetobject = targetobject;
        this.body = body;
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        fmt.keyword("synchronized");
        fmt.operator("(");
        this.targetobject.getCode(fmt);
        fmt.operator(")");
        fmt.openBlock();
        this.body.getCode(fmt);
        fmt.closeBlock();
    }

    @Override
    public void accept(ICodeVisitor visitor) {
        this.targetobject.accept(visitor);
        visitor.openScope();
        this.body.accept(visitor);
        visitor.closeScope();
    }

    @Override
    public boolean isBlock() {
        return true;
    }
}
