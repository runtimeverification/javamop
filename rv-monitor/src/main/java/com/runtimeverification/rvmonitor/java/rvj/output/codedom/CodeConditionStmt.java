package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import java.util.ArrayList;
import java.util.List;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.ICodeVisitor;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;

/**
 * This class represents an if (and optionally multiple else if and an else
 * branches) statement.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class CodeConditionStmt extends CodeStmt {
    private final List<Branch> branches;
    private CodeStmtCollection elsebranch;

    class Branch {
        private final CodeExpr condition;
        private final CodeStmtCollection stmts;

        Branch(CodeExpr cond, CodeStmtCollection stmts) {
            this.condition = cond;
            this.stmts = stmts;
        }
    }

    public CodeConditionStmt(CodeExpr ifcond, CodeStmt stmt) {
        this(ifcond, new CodeStmtCollection(stmt));
    }

    public CodeConditionStmt(CodeExpr ifcond, CodeStmtCollection stmts) {
        stmts.simplify();

        this.branches = new ArrayList<Branch>();

        Branch branch = new Branch(ifcond, stmts);
        this.branches.add(branch);
    }

    public void setElse(CodeStmtCollection stmts) {
        stmts.simplify();

        this.elsebranch = stmts;
    }

    @Override
    public boolean isBlock() {
        return true;
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        boolean first = true;
        for (Branch branch : this.branches) {
            if (first)
                first = false;
            else
                fmt.keyword("else");

            fmt.keyword("if");
            fmt.operator("(");
            branch.condition.getCode(fmt);
            fmt.operator(")");

            this.getCodeBranch(fmt, branch.stmts);
        }

        if (this.elsebranch != null) {
            fmt.keyword("else");
            this.getCodeBranch(fmt, this.elsebranch);
        }
    }

    private void getCodeBranch(ICodeFormatter fmt, CodeStmtCollection nested) {
        boolean needcurlybrace = nested == null || !nested.isSingle();
        // Since some back-end code in the old JavaMOP messes up with
        // a single statement without curly braces, curly braces are always put.
        needcurlybrace = true;

        if (needcurlybrace)
            fmt.openBlock();
        else
            fmt.push();

        if (nested != null)
            nested.getCode(fmt);

        if (needcurlybrace)
            fmt.closeBlock();
        else
            fmt.pop();
    }

    @Override
    public void accept(ICodeVisitor visitor) {
        for (Branch branch : this.branches) {
            branch.condition.accept(visitor);
            if (branch.stmts != null) {
                visitor.openScope();
                branch.stmts.accept(visitor);
                visitor.closeScope();
            }
        }

        if (this.elsebranch != null) {
            visitor.openScope();
            this.elsebranch.accept(visitor);
            visitor.closeScope();
        }

        for (Branch branch : this.branches)
            visitor.visit(branch.stmts);

        if (this.elsebranch != null)
            visitor.visit(this.elsebranch);
    }
}
