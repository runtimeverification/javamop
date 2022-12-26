package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import java.util.ArrayList;
import java.util.List;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeGenerator;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;

public class CodeClassDef implements ICodeGenerator {
    private final String name;
    private final CodeType superclass;
    private final List<CodeMemberField> fields;
    private final List<CodeMemberMethod> methods;

    public boolean isAnonymous() {
        return this.name == null;
    }

    public CodeType getSuperType() {
        return this.superclass;
    }

    public CodeClassDef(String name, CodeType superclass) {
        this.name = name;
        this.superclass = superclass;
        this.fields = new ArrayList<CodeMemberField>();
        this.methods = new ArrayList<CodeMemberMethod>();
    }

    public static CodeClassDef anonymous(CodeType supertype) {
        return new CodeClassDef(null, supertype);
    }

    public void addField(CodeMemberField field) {
        this.fields.add(field);
    }

    public void addMethod(CodeMemberMethod method) {
        this.methods.add(method);
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        if (!this.isAnonymous()) {
            fmt.keyword("class");
            fmt.identifier(this.name);
        }

        fmt.openBlock();
        for (CodeMemberField field : this.fields)
            field.getCode(fmt);
        for (CodeMemberMethod method : this.methods)
            method.getCode(fmt);
        fmt.closeBlock();
    }
}
