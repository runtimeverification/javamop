package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import java.util.Arrays;
import java.util.List;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.ICodeVisitor;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;

/**
 * This class represents a 'new' expression; e.g., <code>
 * new type(arguments)
 * </code>
 *
 * Additionally, this class can represent a creation of an anonymous class;
 * e.g., <code>
 * new type(arguments) {
 *   anonymous-class
 * };
 * </code>
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 *
 */
public class CodeNewExpr extends CodeExpr {
    private final List<CodeExpr> arguments;
    private final CodeClassDef anonymousclass;

    public CodeNewExpr(CodeType type, CodeExpr... args) {
        this(type, null, Arrays.asList(args));
    }

    public CodeNewExpr(CodeType type, List<CodeExpr> args) {
        this(type, null, args);
    }

    public CodeNewExpr(CodeType type, CodeClassDef anonklass, CodeExpr... args) {
        this(type, anonklass, Arrays.asList(args));
    }

    public CodeNewExpr(CodeType type, CodeClassDef anonklass,
            List<CodeExpr> args) {
        super(type);

        this.arguments = args;
        this.anonymousclass = anonklass;
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        fmt.keyword("new");
        fmt.type(this.type);
        fmt.operator("(");
        {
            boolean first = true;
            for (CodeExpr arg : this.arguments) {
                if (first)
                    first = false;
                else
                    fmt.operator(",");
                arg.getCode(fmt);
            }
        }
        fmt.operator(")");

        if (this.anonymousclass != null)
            this.anonymousclass.getCode(fmt);
    }

    @Override
    public void accept(ICodeVisitor visitor) {
        for (CodeExpr arg : this.arguments)
            arg.accept(visitor);
    }
}
