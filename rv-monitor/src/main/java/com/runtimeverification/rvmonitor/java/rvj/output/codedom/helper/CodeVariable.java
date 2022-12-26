package com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper;

import com.runtimeverification.rvmonitor.java.rvj.output.RVMVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;

/**
 * This class represents a local variable or parameter. A variable keeps both
 * the variable name and the type. Additionally, the description can be kept,
 * which will be printed in a comment, only if an option is enabled.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class CodeVariable {
    private final CodeType type;
    private final String name;
    private String description;

    public final CodeType getType() {
        return this.type;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public String getName() {
        return this.name;
    }

    public CodeVariable(CodeType type, String name) {
        this(type, name, null);
    }

    public CodeVariable(CodeType type, String name, String desc) {
        this.type = type;
        this.name = name;
        this.description = desc;

        if (this.type == null || this.name == null)
            throw new IllegalArgumentException();
    }

    public RVMVariable toLegacy() {
        // This legacy type is untyped. Ideally, this legacy should be
        // eliminated, but it requires too much work.
        return new RVMVariable(this.name);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(this.type);
        s.append(' ');
        s.append(this.name);
        if (this.description != null) {
            s.append('\n');
            s.append(this.description);
        }
        return s.toString();
    }

    public void getDeclarationCode(ICodeFormatter fmt) {
        fmt.type(this.type);
        fmt.identifier(this.name);
    }
}
