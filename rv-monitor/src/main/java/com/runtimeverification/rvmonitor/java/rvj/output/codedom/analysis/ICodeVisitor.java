package com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeAssignStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmtCollection;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeVarDeclStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeVarRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeVariable;

/**
 * This interface defines what a code visitor should implement.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public interface ICodeVisitor {
    public void visit(CodeStmtCollection stmts);

    public void declareVariable(CodeVarDeclStmt decl);

    public void assignVariable(CodeAssignStmt assign);

    public void referVariable(CodeVariable referred);

    public void openScope();

    public void closeScope();
}

/**
 * This class implements a visitor that eliminates the given junks from all the
 * visited statements.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
class StmtEliminationVisitor implements ICodeVisitor {
    /**
     * Keeps all the remaining junk statements. Initially, junk statements are
     * given by the caller.
     */
    private final Set<CodeStmt> junks;
    /**
     * Keeps all the eliminated statements.
     */
    private final Set<CodeStmt> eliminated;

    public Set<CodeStmt> getRemainingJunks() {
        return this.junks;
    }

    public Set<CodeStmt> getEliminatedJunks() {
        return this.eliminated;
    }

    public StmtEliminationVisitor(Set<CodeStmt> junks) {
        this.junks = junks;
        this.eliminated = new HashSet<CodeStmt>();
    }

    @Override
    public void visit(CodeStmtCollection stmts) {
        Set<CodeStmt> removed = new HashSet<CodeStmt>();
        for (CodeStmt junk : this.junks) {
            if (stmts.remove(junk))
                removed.add(junk);
        }

        this.junks.removeAll(removed);
        this.eliminated.addAll(removed);
    }

    @Override
    public void declareVariable(CodeVarDeclStmt decl) {
    }

    @Override
    public void assignVariable(CodeAssignStmt assign) {
    }

    @Override
    public void referVariable(CodeVariable referred) {
    }

    @Override
    public void openScope() {
    }

    @Override
    public void closeScope() {
    }
}

/**
 * This class implements a visitor that collects unused variables and their
 * declarations and assignments.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
class ReferredVariableVisitor implements ICodeVisitor {
    private final Block topblock;
    private Block currentblock;

    public ReferredVariableVisitor() {
        this.topblock = new Block();
        this.currentblock = this.topblock;
    }

    @Override
    public void visit(CodeStmtCollection stmts) {
    }

    @Override
    public void declareVariable(CodeVarDeclStmt decl) {
        boolean newlyadded = this.currentblock.declare(decl);

        // The generated code is supposed not to have any duplicated declaration
        // in the same scope.
        if (!newlyadded)
            throw new IllegalArgumentException();
    }

    @Override
    public void assignVariable(CodeAssignStmt assign) {
        this.currentblock.assign(assign);
    }

    @Override
    public void referVariable(CodeVariable referred) {
        this.currentblock.refer(referred);
    }

    @Override
    public void openScope() {
        this.currentblock = this.currentblock.open();
    }

    @Override
    public void closeScope() {
        this.currentblock = this.currentblock.close();
    }

    public Set<CodeStmt> collectUnusedDeclarationsAndAssignments() {
        Set<CodeStmt> junks = new HashSet<CodeStmt>();
        this.topblock.collectUnusedDeclarationsAndAssignmentsRecursively(junks);
        return junks;
    }

    static class Block {
        private final Block parent;
        private final List<Block> children;
        private final Map<String, CodeVarDeclStmt> declared;
        private final Map<CodeVarDeclStmt, List<CodeAssignStmt>> assigned;
        private final Set<CodeVarDeclStmt> referred;

        Block() {
            this(null);
        }

        private Block(Block parent) {
            this.parent = parent;
            this.children = new ArrayList<Block>();
            this.declared = new HashMap<String, CodeVarDeclStmt>();
            this.assigned = new HashMap<CodeVarDeclStmt, List<CodeAssignStmt>>();
            this.referred = new HashSet<CodeVarDeclStmt>();
        }

        public boolean declare(CodeVarDeclStmt decl) {
            String varname = decl.getVariable().getName();
            CodeVarDeclStmt prev = this.declared.put(varname, decl);
            return prev == null;
        }

        public boolean assign(CodeAssignStmt assign) {
            CodeExpr lhs = assign.getLHS();
            if (lhs instanceof CodeVarRefExpr) {
                CodeVarRefExpr varref = (CodeVarRefExpr) lhs;
                CodeVariable assigned = varref.getVariable();
                Block declaring = this.lookup(assigned);
                if (declaring == null)
                    return false;

                CodeVarDeclStmt decl = declaring.declared.get(assigned
                        .getName());
                List<CodeAssignStmt> assigns = declaring.assigned.get(decl);
                if (assigns == null) {
                    assigns = new ArrayList<CodeAssignStmt>();
                    declaring.assigned.put(decl, assigns);
                }
                assigns.add(assign);
            }
            return true;
        }

        public boolean refer(CodeVariable referred) {
            Block declaring = this.lookup(referred);
            if (declaring == null)
                return false;

            CodeVarDeclStmt decl = declaring.declared.get(referred.getName());
            declaring.referred.add(decl);
            return true;
        }

        public Block open() {
            Block nested = new Block(this);
            this.children.add(nested);
            return nested;
        }

        public Block close() {
            return this.parent;
        }

        private Block lookup(CodeVariable referred) {
            CodeVarDeclStmt decl = this.declared.get(referred.getName());
            if (decl != null)
                return this;

            if (this.parent == null)
                return null;
            return this.parent.lookup(referred);
        }

        @SuppressWarnings("unused")
        private Block searchRecursively(CodeVarDeclStmt decl) {
            for (Map.Entry<String, CodeVarDeclStmt> entry : this.declared
                    .entrySet()) {
                if (entry.getValue() == decl)
                    return this;
            }

            for (Block child : this.children) {
                Block result = child.searchRecursively(decl);
                if (result != null)
                    return result;
            }
            return null;
        }

        public void collectUnusedDeclarationsAndAssignmentsRecursively(
                Set<CodeStmt> junks) {
            for (Map.Entry<String, CodeVarDeclStmt> entry : this.declared
                    .entrySet()) {
                if (!this.referred.contains(entry.getValue())) {
                    junks.add(entry.getValue());
                    List<CodeAssignStmt> assigns = this.assigned.get(entry
                            .getValue());
                    if (assigns != null)
                        junks.addAll(assigns);
                }
            }

            for (Block child : this.children)
                child.collectUnusedDeclarationsAndAssignmentsRecursively(junks);
        }
    }
}
