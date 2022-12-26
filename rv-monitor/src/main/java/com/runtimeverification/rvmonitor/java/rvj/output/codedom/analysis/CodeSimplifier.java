package com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis;

import java.util.Set;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmtCollection;

/**
 * This class takes statements (an instance of CodeStmtCollection) and
 * simplifies them. For example, while generating code at the first stage, it is
 * not trivial to see whether or not a variable will be actually referred.
 * Instead of creating only necessary variables, RV-Monitor first creates all
 * possibly used variables and then uses this class to eliminate unused
 * variables.
 *
 * Currently, this class does the following simplification: 1. Unused variable
 * elimination: if a variable is defined but never used, all the relevant code
 * is eliminated.
 *
 * The followings should be done as well: 1. Empty body elimination: if the body
 * of a branch or loop is empty, that block is eliminated. 2. Redundant check
 * elimination: if the same check is performed without any potential update, the
 * redundant check is eliminated.
 *
 * This class is by no means a complete static analyzer, and I do not have any
 * interest in making it a fully-fledged analyzer. It focuses on simple things
 * with many assumptions that would not hold in general case.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class CodeSimplifier {
    private final CodeStmtCollection block;
    private Trial current;

    public CodeSimplifier(CodeStmtCollection block) {
        this.block = block;
    }

    public void simplify() {
        do {
            this.current = new Trial();
            this.block.accept(this.current.visitor);

            UnusedVariableElimination elim = new UnusedVariableElimination();
            elim.run();
        } while (this.current.changed);
    }

    class UnusedVariableElimination {
        public void run() {
            Set<CodeStmt> junks = this.collect();
            this.eliminate(junks);
        }

        private Set<CodeStmt> collect() {
            Set<CodeStmt> junks = CodeSimplifier.this.current.visitor
                    .collectUnusedDeclarationsAndAssignments();
            return junks;
        }

        private void eliminate(Set<CodeStmt> junks) {
            StmtEliminationVisitor visitor = new StmtEliminationVisitor(junks);
            CodeSimplifier.this.block.accept(visitor);

            Set<CodeStmt> remaining = visitor.getRemainingJunks();
            if (remaining.size() > 0) {
                // This probably indicates that some code was unreachable due to
                // some missing
                // part in the visitor or visitable objects.
                throw new IllegalArgumentException();
            }

            Set<CodeStmt> eliminated = visitor.getEliminatedJunks();
            if (eliminated.size() > 0)
                CodeSimplifier.this.current.changed = true;
        }
    }

    static class Trial {
        private final ReferredVariableVisitor visitor;
        private boolean changed;

        Trial() {
            this.visitor = new ReferredVariableVisitor();
            this.changed = false;
        }
    }
}
