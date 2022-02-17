package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.ICodeVisitor;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;

/**
 * This class represents an assignment statement.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class CodeAssignStmt extends CodeStmt {
    private final CodeExpr lhs;
    private final CodeExpr rhs;

    public CodeExpr getLHS() {
        return this.lhs;
    }

    public CodeAssignStmt(CodeExpr lhs, CodeExpr rhs) {
        this.lhs = lhs;
        this.rhs = rhs;

        this.validate();
    }

    private void validate() {
        if (this.lhs == null)
            throw new IllegalArgumentException();
        if (this.rhs == null)
            throw new IllegalArgumentException();
    }

    @Override
    public boolean isBlock() {
        return false;
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        this.lhs.getCode(fmt);
        fmt.operator("=");
        this.rhs.getCode(fmt);
    }

    @Override
    public void accept(ICodeVisitor visitor) {
        // Referring a variable here is different from referring one in other
        // contexts,
        // in that referring it here is subject to elimination; i.e., the
        // variable should
        // not cause the visitor to mark it as "referred".
        // this.lhs.accept(visitor);
        this.rhs.accept(visitor);

        visitor.assignVariable(this);
    }
}
