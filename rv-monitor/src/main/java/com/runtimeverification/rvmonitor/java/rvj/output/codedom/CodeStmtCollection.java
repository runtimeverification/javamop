package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import java.util.ArrayList;
import java.util.List;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.ICodeVisitable;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.ICodeVisitor;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeFormatters;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeGenerator;

/**
 * This class represents a list of statements. For example, the body of a for
 * loop is represented by an instance of this class.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class CodeStmtCollection implements ICodeGenerator, ICodeVisitable {
    private final List<CodeStmt> list;

    public List<CodeStmt> getStmts() {
        return this.list;
    }

    public boolean isSingle() {
        return this.list.size() == 1;
    }

    public CodeStmtCollection() {
        this.list = new ArrayList<CodeStmt>();
    }

    public CodeStmtCollection(CodeStmt... stmts) {
        this.list = new ArrayList<CodeStmt>();
        for (CodeStmt stmt : stmts)
            this.add(stmt);
    }

    public CodeStmtCollection(CodeStmtCollection... stmtscoll) {
        this.list = new ArrayList<CodeStmt>();
        for (CodeStmtCollection stmts : stmtscoll)
            this.add(stmts);
    }

    public static CodeStmtCollection empty() {
        return new CodeStmtCollection();
    }

    public void add(CodeStmt stmt) {
        if (stmt != null)
            this.list.add(stmt);
    }

    public void add(CodeStmtCollection stmts) {
        if (stmts != null) {
            for (CodeStmt stmt : stmts.list)
                this.add(stmt);
        }
    }

    public boolean remove(CodeStmt junk) {
        return this.list.remove(junk);
    }

    /**
     * Since adding comments is so frequently performed, a shortcut has been
     * created.
     *
     * @param cmt
     *            comment
     */
    public void comment(String cmt) {
        this.add(new CodeCommentStmt(cmt));
    }

    public static CodeStmtCollection fromLegacy(String str) {
        CodeStmtCollection coll = new CodeStmtCollection();
        coll.add(CodeStmt.fromLegacy(str));
        return coll;
    }

    public void simplify() {
        // The following flattens the structure if CodeBlockStmt is the only
        // child.
        // This is for pretty-printing.
        if (this.list.size() == 1
                && (this.list.get(0) instanceof CodeBlockStmt)) {
            CodeBlockStmt block = (CodeBlockStmt) this.list.get(0);
            block.getBody().simplify();

            this.list.clear();
            this.list.addAll(block.getBody().list);
        }
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        for (CodeStmt stmt : this.list) {
            if (stmt instanceof CodePhantomStmt)
                continue;

            stmt.getCode(fmt);
            if (!stmt.isBlock())
                fmt.endOfStatement();
        }
    }

    @Override
    public void accept(ICodeVisitor visitor) {
        for (CodeStmt stmt : this.list)
            stmt.accept(visitor);

        visitor.visit(this);
    }

    @Override
    public String toString() {
        ICodeFormatter fmt = CodeFormatters.getDefault();
        this.getCode(fmt);
        return fmt.getCode();
    }
}
