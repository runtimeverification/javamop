package com.runtimeverification.rvmonitor.java.rvj.output.codedom.rvapi;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeExprStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMethodInvokeExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmtCollection;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeVarRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;

/**
 * This class generated commonly used code that invokes the RV-Monitor API.
 * Ideally, all RV-Monitor API should go through this class, rather than
 * hard-coded. At the time of writing this comment, however, this ideal has not
 * been achieved.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class GeneratedCodeAPI {
    /**
     * Generates code that inserts the newly created monitor into the given map.
     *
     * @param mapref
     *            the map where the monitor is inserted
     * @param keyref
     *            the key in the map
     * @param monitorref
     *            the value in the map
     * @return the generated code
     */
    public static CodeStmtCollection generatePutNode(CodeVarRefExpr mapref,
            CodeVarRefExpr keyref, CodeVarRefExpr valueref) {
        if (mapref == null)
            throw new IllegalArgumentException();

        CodeStmtCollection stmts = new CodeStmtCollection();

        CodeMethodInvokeExpr invoke = new CodeMethodInvokeExpr(CodeType.foid(),
                mapref, "putNode", keyref, valueref);
        stmts.add(new CodeExprStmt(invoke));

        return stmts;
    }
}
