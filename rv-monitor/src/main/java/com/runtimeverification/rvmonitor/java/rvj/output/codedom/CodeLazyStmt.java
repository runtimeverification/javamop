package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.ICodeVisitor;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;

/**
 * This class represents a statement that yields a stable result only after the
 * first stage, among two, of code generation is complete. For most cases, a
 * CodeObject instance yields the same result regardless of the stage. However,
 * some code needs to collect some information that can be easily obtained
 * during the first stage. This class was created to handle such case.
 *
 * This class merely acts as a marker, which says that the code generated from
 * an instance of this class is unstable during the first stage. Therefore, it
 * is caller's responsibility to make sure getCode() is invoked again after the
 * first stage is complete. This requirement can be easily satisfied by
 * generating code once again.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 *
 */
public abstract class CodeLazyStmt extends CodeStmt {
    protected abstract CodeStmtCollection evaluate();

    @Override
    public final void getCode(ICodeFormatter fmt) {
        CodeStmtCollection stmts = this.evaluate();
        stmts.getCode(fmt);
    }

    @Override
    public boolean isBlock() {
        // It can be changed, but let's assume it is at this moment.
        return true;
    }

    @Override
    public void accept(ICodeVisitor visitor) {
        CodeStmtCollection stmts = this.evaluate();
        if (stmts != null)
            stmts.accept(visitor);
    }
}
