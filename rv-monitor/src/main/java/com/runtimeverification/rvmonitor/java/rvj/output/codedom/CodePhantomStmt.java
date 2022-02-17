package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import java.util.Arrays;
import java.util.List;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.ICodeVisitor;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;

/**
 * This class represents a statement that is visible to only righteous humans.
 * That is, the statement never becomes a Java code. Its purpose is solely on
 * marking variables as 'used', so that dead-code elimination won't remove them.
 * This class should not be used for CodeDOM classes, as such marking should be
 * done automatically. In contrast, this class may be useful for legacy
 * string-concatenated code, as variables never get a chance to be marked in it.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 *
 */
public class CodePhantomStmt extends CodeStmt {
    private final List<CodeVariable> referred;

    public CodePhantomStmt(CodeVariable... referred) {
        this.referred = Arrays.asList(referred);
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        // Nothing to print.
    }

    @Override
    public void accept(ICodeVisitor visitor) {
        for (CodeVariable var : this.referred)
            visitor.referVariable(var);
    }

    @Override
    public boolean isBlock() {
        return false;
    }
}
