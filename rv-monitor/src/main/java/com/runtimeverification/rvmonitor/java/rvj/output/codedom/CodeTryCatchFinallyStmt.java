package com.runtimeverification.rvmonitor.java.rvj.output.codedom;

import java.util.Arrays;
import java.util.List;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.ICodeVisitor;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;

/**
 * This class represents a try-catch block.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class CodeTryCatchFinallyStmt extends CodeStmt {
    private final CodeStmtCollection tryblock;
    private final List<CatchBlock> catchblocks;
    private final CodeStmtCollection finallyblock;

    public static class CatchBlock {
        private final CodeVariable expr;
        private final CodeStmtCollection stmts;

        CatchBlock(CodeVariable expr, CodeStmtCollection stmts) {
            this.expr = expr;
            this.stmts = stmts;
        }
    }

    public CodeTryCatchFinallyStmt(CodeStmtCollection tryblock,
            CodeStmtCollection finallyblock, CatchBlock... catchblocks) {
        this(tryblock, finallyblock, Arrays.asList(catchblocks));
    }

    public CodeTryCatchFinallyStmt(CodeStmtCollection tryblock,
            CodeStmtCollection finallyblock, List<CatchBlock> catchblocks) {
        this.tryblock = tryblock;
        this.catchblocks = catchblocks;
        this.finallyblock = finallyblock;

        this.validate();
    }

    private void validate() {
        if (this.tryblock == null)
            throw new IllegalArgumentException();
        if (this.catchblocks == null)
            throw new IllegalArgumentException();
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        fmt.keyword("try");
        fmt.openBlock();
        this.tryblock.getCode(fmt);
        fmt.closeBlock();

        for (CatchBlock catchblock : this.catchblocks) {
            fmt.keyword("catch");
            fmt.operator("(");
            catchblock.expr.getDeclarationCode(fmt);
            fmt.operator(")");
            fmt.openBlock();
            catchblock.stmts.getCode(fmt);
            fmt.closeBlock();
        }

        if (this.finallyblock != null) {
            fmt.keyword("finally");
            fmt.openBlock();
            this.finallyblock.getCode(fmt);
            fmt.closeBlock();
        }
    }

    @Override
    public void accept(ICodeVisitor visitor) {
        visitor.openScope();
        this.tryblock.accept(visitor);
        visitor.closeScope();

        for (CatchBlock catchblock : this.catchblocks) {
            visitor.openScope();
            catchblock.stmts.accept(visitor);
            visitor.closeScope();
        }

        if (this.finallyblock != null) {
            visitor.openScope();
            this.finallyblock.accept(visitor);
            visitor.closeScope();
        }
    }

    @Override
    public boolean isBlock() {
        return true;
    }
}
