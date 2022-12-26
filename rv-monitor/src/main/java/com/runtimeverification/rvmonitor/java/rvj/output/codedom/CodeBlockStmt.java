package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.ICodeVisitor;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;

/**
 * This class represents a block, other than if, for, while and so on. The code
 * generator is to generate the following code from an instance of this class:
 * <code>
 * {
 *    ... (represented by the 'body' field)
 * }
 * </code> It simply wraps the statements represented by the 'body' field by a
 * pair of curly braces.
 *
 * This class is used to create a block that does not leave any unnecessary
 * local variable to the rest of the code.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 *
 */
public class CodeBlockStmt extends CodeStmt {
    private final CodeStmtCollection body;

    public CodeStmtCollection getBody() {
        return this.body;
    }

    public CodeBlockStmt(CodeStmtCollection body) {
        this.body = body;

        this.validate();
    }

    private void validate() {
        if (this.body == null)
            throw new IllegalArgumentException();
    }

    @Override
    public boolean isBlock() {
        return true;
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        fmt.openBlock();
        this.body.getCode(fmt);
        fmt.closeBlock();
    }

    @Override
    public void accept(ICodeVisitor visitor) {
        visitor.openScope();
        for (CodeStmt stmt : this.body.getStmts())
            stmt.accept(visitor);
        visitor.closeScope();

        visitor.visit(this.body);
    }
}
