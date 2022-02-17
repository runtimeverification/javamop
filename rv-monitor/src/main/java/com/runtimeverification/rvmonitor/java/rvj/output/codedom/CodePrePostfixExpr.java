package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.ICodeVisitor;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;

/**
 * This class represents a prefix or postfix expression; e.g., <code>
 * a++
 * </code>
 *
 * Currently, only four necessary forms are supported: ++p, p++, --p, and p--.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class CodePrePostfixExpr extends CodeExpr {
    private final CodeExpr ref;
    private boolean prefix;
    private boolean plus;

    private CodePrePostfixExpr(CodeExpr ref, boolean prefix, boolean plus) {
        super(ref.getType());

        this.ref = ref;
        this.prefix = prefix;
        this.plus = plus;

        this.validate();
    }

    private void validate() {
        if (this.ref == null)
            throw new IllegalArgumentException();
    }

    public static CodePrePostfixExpr prefix(CodeExpr ref, boolean plus) {
        return new CodePrePostfixExpr(ref, true, plus);
    }

    public static CodePrePostfixExpr postfix(CodeExpr ref, boolean plus) {
        return new CodePrePostfixExpr(ref, false, plus);
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        String op = this.plus ? "++" : "--";

        if (this.prefix)
            fmt.operator(op);

        this.ref.getCode(fmt);

        if (!this.prefix)
            fmt.operator(op);
    }

    @Override
    public void accept(ICodeVisitor visitor) {
        this.ref.accept(visitor);
    }
}
