package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.ICodeVisitor;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;

/**
 * This class represents a field reference expression. The reference can be used
 * for both getfield and setfield. If the referred field is static, the 'target'
 * field is null.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 *
 */
public class CodeFieldRefExpr extends CodeExpr {
    private final CodeExpr target;
    private final CodeType declaring;
    private final CodeMemberField field;

    public CodeMemberField getField() {
        return this.field;
    }

    public CodeFieldRefExpr(CodeMemberField field) {
        this(null, null, field);
    }

    public CodeFieldRefExpr(CodeExpr target, CodeMemberField field) {
        this(null, target, field);
    }

    public CodeFieldRefExpr(CodeType declaring, CodeMemberField field) {
        this(declaring, null, field);
    }

    private CodeFieldRefExpr(CodeType declaring, CodeExpr target,
            CodeMemberField field) {
        super(field.getType());

        this.target = target;
        this.declaring = declaring;
        this.field = field;
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
        fmt.identifier(this.field.getName());
    }

    @Override
    public void accept(ICodeVisitor visitor) {
        if (this.target != null)
            this.target.accept(visitor);
    }
}
