package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.ICodeVisitor;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;

/**
 * This class represents a 'return' statement; e.g., <code>
 *   return 1;
 * </code> The returned value can be nothing because a void method does not have
 * any return value.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class CodeReturnStmt extends CodeStmt {
    private final CodeExpr retval;

    public CodeReturnStmt() {
        this(null);
    }

    public CodeReturnStmt(CodeExpr retval) {
        this.retval = retval;
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        fmt.keyword("return");
        if (this.retval != null)
            this.retval.getCode(fmt);
    }

    @Override
    public void accept(ICodeVisitor visitor) {
        if (this.retval != null)
            this.retval.accept(visitor);
    }

    @Override
    public boolean isBlock() {
        return false;
    }
}
