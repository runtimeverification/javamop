package com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeObject;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmtCollection;

/**
 * This class represents a pair of statements and (additional) code object. This
 * can be useful when a method needs to return not only the generated statements
 * but also a notable code object created during code generation.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 *
 * @param <T>
 *            the additional code object that is related to the statements
 */
public class CodePair<T extends CodeObject> {
    private final CodeStmtCollection generated;
    private final T logicalReturn;

    public final CodeStmtCollection getGeneratedCode() {
        return this.generated;
    }

    public final T getLogicalReturn() {
        return this.logicalReturn;
    }

    public CodePair(T logret) {
        this.generated = null;
        this.logicalReturn = logret;

        this.validate();
    }

    public CodePair(CodeStmt stmt, T logret) {
        this(new CodeStmtCollection(stmt), logret);
    }

    public CodePair(CodeStmtCollection gen, T logret) {
        this.generated = gen;
        this.logicalReturn = logret;

        this.validate();
    }

    private void validate() {
        // this.generated can be null.
        // this.logicalReturn can be null.
    }
}
