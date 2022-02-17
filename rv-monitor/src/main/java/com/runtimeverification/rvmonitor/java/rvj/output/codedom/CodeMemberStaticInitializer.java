package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeGenerator;

/**
 * This class represents a static initializer of a class. A static initializer
 * is the block represented by the 'static' keyword; e.g., <code>
 * static {
 *   body
 * }
 * </code>
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class CodeMemberStaticInitializer extends CodeMember implements
ICodeGenerator {
    private CodeStmtCollection body;

    public CodeMemberStaticInitializer(CodeStmtCollection body) {
        super(null, false, true, true, null);

        this.body = body;

        this.validate();
    }

    private void validate() {
        if (this.body == null)
            throw new IllegalArgumentException();
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        fmt.keyword("static");
        fmt.openBlock();
        this.body.getCode(fmt);
        fmt.closeBlock();
    }
}
