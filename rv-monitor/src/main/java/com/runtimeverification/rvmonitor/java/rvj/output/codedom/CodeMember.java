package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;

/**
 * This class is the super class of any member of a class, such as a field and a
 * method.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public abstract class CodeMember {
    protected final String name;
    protected final boolean publik;
    protected final boolean statik;
    protected final boolean finale;
    protected final CodeType type;

    public String getName() {
        return this.name;
    }

    protected CodeMember(String name, boolean publik, boolean statik,
            boolean finale, CodeType type) {
        this.name = name;
        this.publik = publik;
        this.statik = statik;
        this.finale = finale;
        this.type = type;
    }

    protected void getCodeCommon(ICodeFormatter fmt) {
        fmt.keyword(this.publik ? "public" : "private");
        if (this.statik)
            fmt.keyword("static");
        if (this.finale)
            fmt.keyword("final");
        fmt.type(this.type);
        fmt.identifier(this.name);
    }
}
