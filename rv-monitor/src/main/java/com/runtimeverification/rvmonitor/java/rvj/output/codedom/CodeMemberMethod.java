package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import java.util.Arrays;
import java.util.List;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeGenerator;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;

/**
 * This class represents a static or non-static method.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class CodeMemberMethod extends CodeMember implements ICodeGenerator {
    private final boolean overriding;
    private final List<CodeVariable> parameters;
    private final CodeStmtCollection body;

    public CodeMemberMethod(String name, boolean publik, boolean statik,
            boolean finale, CodeType type, boolean overriding, CodeStmt body) {
        this(name, publik, statik, finale, type, overriding,
                new CodeStmtCollection(body));
    }

    public CodeMemberMethod(String name, boolean publik, boolean statik,
            boolean finale, CodeType type, boolean overriding, CodeStmt body,
            CodeVariable... params) {
        this(name, publik, statik, finale, type, overriding,
                new CodeStmtCollection(body), params);
    }

    public CodeMemberMethod(String name, boolean publik, boolean statik,
            boolean finale, CodeType type, boolean overriding,
            CodeStmtCollection body, CodeVariable... params) {
        super(name, publik, statik, finale, type);
        this.overriding = overriding;
        this.parameters = Arrays.asList(params);
        this.body = body;
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        if (this.overriding)
            fmt.identifier("@Override");
        this.getCodeCommon(fmt);
        fmt.operator("(");
        for (int i = 0; i < this.parameters.size(); ++i) {
            if (i > 0)
                fmt.operator(",");
            CodeVariable param = this.parameters.get(i);
            param.getDeclarationCode(fmt);
        }
        fmt.operator(")");
        fmt.openBlock();
        this.body.getCode(fmt);
        fmt.closeBlock();
    }
}
