package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.ICodeVisitor;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;

/**
 * This class represents a statement that holds an expression. For example, an
 * expression that represents a method invocation can be encapsulated into an
 * instance of this class: <code>
 * CodeStmt stmt = new CodeExprStmt(new CodeMethodInvokeExpr(...));
 * </code>
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 *
 */
public class CodeExprStmt extends CodeStmt {
    private final CodeExpr expr;

    public CodeExprStmt(CodeExpr expr) {
        this.expr = expr;

        this.validate();
    }

    private void validate() {
        if (this.expr == null)
            throw new IllegalArgumentException();
    }

    @Override
    public boolean isBlock() {
        return false;
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        this.expr.getCode(fmt);
    }

    @Override
    public void accept(ICodeVisitor visitor) {
        this.expr.accept(visitor);
    }
}
