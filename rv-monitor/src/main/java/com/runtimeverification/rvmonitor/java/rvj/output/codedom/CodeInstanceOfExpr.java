package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.ICodeVisitor;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;

/**
 * This class represents an 'instanceof' expression. <code>
 * ref instanceof type
 * </code>
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class CodeInstanceOfExpr extends CodeExpr {
    private final CodeExpr ref;
    private final CodeType type;

    public CodeInstanceOfExpr(CodeExpr ref, CodeType type) {
        super(CodeType.bool());

        this.ref = ref;
        this.type = type;

        this.validate();
    }

    private void validate() {
        if (this.ref == null)
            throw new IllegalArgumentException();
        if (this.type == null)
            throw new IllegalArgumentException();
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        this.ref.getCode(fmt);
        fmt.keyword("instanceof");
        fmt.type(this.type);
    }

    @Override
    public void accept(ICodeVisitor visitor) {
        this.ref.accept(visitor);
    }
}
