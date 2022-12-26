package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event.itf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.runtimeverification.rvmonitor.java.rvj.output.NotImplementedException;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeFieldRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeLiteralExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMemberField;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmtCollection;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeVarDeclStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeVarRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeHelper;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.IndexingTreeManager;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.reftree.RefTree;
import com.runtimeverification.rvmonitor.java.rvj.output.monitor.SuffixMonitor;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;

/**
 * This class is used to hold parameters carried by an event and their weak
 * references.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class WeakReferenceVariables {
    /**
     * Keeps the list of parameters carried by the event. The order is
     * significant.
     */
    private final List<RVMParameter> params;
    /**
     * Keeps the mapping from a parameter carried by the event to its weak
     * reference.
     */
    private final Map<RVMParameter, CodeVariable> mapping;

    public List<RVMParameter> getParams() {
        return this.params;
    }

    public Map<RVMParameter, CodeVariable> getMapping() {
        return this.mapping;
    }

    private WeakReferenceVariables(List<RVMParameter> params,
            Map<RVMParameter, CodeVariable> mapping) {
        this.params = params;
        this.mapping = mapping;
    }

    public WeakReferenceVariables(IndexingTreeManager trees,
            RVMParameter... params) {
        this(trees, Arrays.asList(params));
    }

    public WeakReferenceVariables(IndexingTreeManager trees,
            RVMParameters params) {
        this(trees, params.toList());
    }

    private WeakReferenceVariables(IndexingTreeManager trees,
            List<RVMParameter> params) {
        this.params = new ArrayList<RVMParameter>();
        this.mapping = new HashMap<RVMParameter, CodeVariable>();

        for (RVMParameter param : params) {
            RefTree gwrt = trees.refTrees.get(param.getType().toString());
            CodeType type = gwrt.getResultFQType();
            CodeVariable var = CodeHelper.VariableName.getWeakRef(type, param);

            this.params.add(param);
            this.mapping.put(param, var);
        }
    }

    public static WeakReferenceVariables merge(WeakReferenceVariables... lst) {
        List<RVMParameter> params = new ArrayList<RVMParameter>();
        Map<RVMParameter, CodeVariable> mapping = new HashMap<RVMParameter, CodeVariable>();

        for (WeakReferenceVariables weakrefs : lst) {
            Set<RVMParameter> setl = mapping.keySet();
            Set<RVMParameter> setr = weakrefs.mapping.keySet();
            if (!Collections.disjoint(setl, setr))
                throw new NotImplementedException();

            params.addAll(weakrefs.params);
            mapping.putAll(weakrefs.mapping);
        }

        return new WeakReferenceVariables(params, mapping);
    }

    public CodeVariable getWeakRef(RVMParameter param) {
        return this.mapping.get(param);
    }

    public CodeStmtCollection getDeclarationCode() {
        CodeStmtCollection stmts = new CodeStmtCollection();
        for (Map.Entry<RVMParameter, CodeVariable> entry : this.mapping
                .entrySet()) {
            CodeVarDeclStmt decl = new CodeVarDeclStmt(entry.getValue(),
                    CodeLiteralExpr.nul());
            stmts.add(decl);
        }
        return stmts;
    }

    public CodeStmtCollection getDeclarationCode(SuffixMonitor monitorClass,
            CodeVarRefExpr monitorref) {
        CodeStmtCollection stmts = new CodeStmtCollection();
        for (Map.Entry<RVMParameter, CodeVariable> entry : this.mapping
                .entrySet()) {
            RVMVariable wrfieldname = monitorClass.getRVMonitorRef(entry
                    .getKey());
            CodeMemberField wrfield = new CodeMemberField(
                    wrfieldname.getVarName(), false, false, false, entry
                    .getValue().getType());
            CodeFieldRefExpr wrfieldref = new CodeFieldRefExpr(monitorref,
                    wrfield);

            CodeVarDeclStmt decl = new CodeVarDeclStmt(entry.getValue(),
                    wrfieldref);
            stmts.add(decl);
        }
        return stmts;
    }
}
