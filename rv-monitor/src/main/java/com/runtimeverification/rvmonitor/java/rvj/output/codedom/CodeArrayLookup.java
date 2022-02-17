package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.ICodeVisitor;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;

/**
 * This class represents an array lookup.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class CodeArrayLookup extends CodeExpr {
    private final CodeExpr array;
    private final CodeExpr index;

    public CodeArrayLookup(CodeType type, CodeExpr array, CodeExpr index) {
        super(type);

        this.array = array;
        this.index = index;

        this.validate();
    }

    private void validate() {
        if (this.array == null)
            throw new IllegalArgumentException();
        if (this.index == null)
            throw new IllegalArgumentException();
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        this.array.getCode(fmt);
        fmt.operator("[");
        this.index.getCode(fmt);
        fmt.operator("]");
    }

    @Override
    public void accept(ICodeVisitor visitor) {
        this.array.accept(visitor);
        this.index.accept(visitor);
    }
}
