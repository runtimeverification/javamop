package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeGenerator;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;

/**
 * This class represents a staic or non-staic field.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class CodeMemberField extends CodeMember implements ICodeGenerator {
    private final CodeExpr init;

    public final CodeType getType() {
        return this.type;
    }

    public CodeMemberField(String name, boolean publik, boolean statik,
            boolean finale, CodeType type) {
        this(name, publik, statik, finale, type, null);
    }

    public CodeMemberField(String name, boolean publik, boolean statik,
            boolean finale, CodeType type, CodeExpr init) {
        super(name, publik, statik, finale, type);

        this.init = init;
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        this.getCodeCommon(fmt);
        if (this.init != null && this.init != CodeLiteralExpr.nul()) {
            fmt.operator("=");
            this.init.getCode(fmt);
        }
        fmt.endOfStatement();
    }
}
