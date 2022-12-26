package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.ICodeVisitor;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;

/**
 * This class represents a literal, such as 1, 2.3, "Hello world", and so on.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class CodeLiteralExpr extends CodeExpr {
    private final String literal;

    protected CodeLiteralExpr(CodeType type, String literal) {
        super(type);
        this.literal = literal;
    }

    private final static CodeLiteralExpr nativeNull;

    static {
        nativeNull = new CodeLiteralExpr(CodeType.object(), "null");
    }

    public static CodeLiteralExpr nul() {
        return nativeNull;
    }

    public static CodeLiteralExpr bool(boolean b) {
        return new CodeLiteralExpr(CodeType.bool(), b ? "true" : "false");
    }

    public static CodeLiteralExpr integer(int i) {
        return new CodeLiteralExpr(CodeType.integer(), Integer.toString(i));
    }

    public static CodeLiteralExpr rong(long l) {
        return new CodeLiteralExpr(CodeType.rong(), Long.toString(l));
    }

    public static CodeLiteralExpr string(String s) {
        String quoted = "\"" + s + "\"";
        return new CodeLiteralExpr(CodeType.string(), quoted);
    }

    public static CodeLiteralExpr enumValue(Enum<?> value) {
        String pkgname = value.getClass().getPackage().getName();
        String clsname = value.getClass().getName();

        int i = clsname.indexOf('$');
        if (i != -1) {
            // '$' implies that the enum is probably defined as a nested class.
            // Since I don't care about the precise distinction between the
            // package name
            // and class name, this code considers the package name plus the
            // enclosing
            // class name as the package name, and the nested class name as the
            // class name.
            // This will make it easier to generate "import" statements; one can
            // simply
            // iterate over used types and collect their package names.
            pkgname += "." + clsname.substring(0, i);
            clsname = clsname.substring(i + 1);
        }

        CodeType type = new CodeType(pkgname, clsname);
        String literal = clsname + "." + value.toString();
        return new CodeLiteralExpr(type, literal);
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        fmt.literal(this.literal);
    }

    @Override
    public void accept(ICodeVisitor visitor) {
    }
}
