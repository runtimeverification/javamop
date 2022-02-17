package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import java.util.Arrays;
import java.util.List;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.ICodeVisitor;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;

/**
 * This class represents a method invocation. If the invoked method is static,
 * the 'target' field is null.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 *
 */
public class CodeMethodInvokeExpr extends CodeExpr {
    private final CodeExpr target;
    private final CodeType declaring;
    private final String methodname;
    private final List<CodeExpr> arguments;

    public CodeMethodInvokeExpr(CodeType type, CodeExpr target,
            String methodname, CodeExpr... args) {
        this(type, null, target, methodname, Arrays.asList(args));
    }

    public CodeMethodInvokeExpr(CodeType type, CodeExpr target,
            String methodname, List<CodeExpr> args) {
        this(type, null, target, methodname, args);
    }

    public CodeMethodInvokeExpr(CodeType type, CodeType declaring,
            CodeExpr target, String methodname, CodeExpr... args) {
        this(type, declaring, target, methodname, Arrays.asList(args));
    }

    public CodeMethodInvokeExpr(CodeType type, CodeType declaring,
            CodeExpr target, String methodname, List<CodeExpr> args) {
        super(type);

        this.target = target;
        this.declaring = declaring;
        this.methodname = methodname;
        this.arguments = args;
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        if (this.declaring != null) {
            fmt.type(this.declaring);
            fmt.operator(".");
        }
        if (this.target != null) {
            this.target.getCode(fmt);
            fmt.operator(".");
        }
        fmt.identifier(this.methodname);
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
    }

    @Override
    public void accept(ICodeVisitor visitor) {
        if (this.target != null)
            this.target.accept(visitor);
        for (CodeExpr arg : this.arguments)
            arg.accept(visitor);
    }
}
