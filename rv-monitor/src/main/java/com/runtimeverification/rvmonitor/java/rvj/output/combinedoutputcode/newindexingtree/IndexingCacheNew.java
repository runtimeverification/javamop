package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree;

import java.util.Map;
import java.util.TreeMap;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.java.rvj.output.CodeGenerationOption;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeAssignStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeBinOpExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeExprStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeFieldRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMemberField;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMethodInvokeExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeNewExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmtCollection;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeVarRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeFormatters;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeHelper;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeGenerator;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event.itf.WeakReferenceVariables;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeImplementation.Entry;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;

/**
 * This class represents a single-entry indexing tree cache. As its name
 * implies, the cache allows the caller to avoid accessing the indexing tree,
 * when this cache hits. Since an indexing tree has multiple levels, the key of
 * this cache may consist of multiple objects, and each is a parameter carried
 * by the event. The value that is associated with the key is the node, which
 * can be a monitor, set, or tuple.
 *
 * The current implements employs a single entry cache; i.e., only the most
 * recently accessed key-value pair is hold. A preliminary experience showed
 * that having multiple entries do not increase hit ratio significantly.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class IndexingCacheNew implements ICodeGenerator {
    private final TreeMap<RVMParameter, CodeCacheField> keys;
    private final CodeCacheField valueField;

    public CodeMemberField getValueField() {
        return this.valueField.getField();
    }

    private IndexingCacheNew(TreeMap<RVMParameter, CodeCacheField> keys,
            Entry valueEntry, CodeCacheField valueField) {
        this.keys = keys;
        this.valueField = valueField;

        this.validate();
    }

    private void validate() {
        if (this.keys == null)
            throw new IllegalArgumentException();
        if (this.valueField == null)
            throw new IllegalArgumentException();
    }

    public static IndexingCacheNew fromTree(String treename,
            IndexingTreeInterface itf) {
        TreeMap<RVMParameter, CodeCacheField> keys = new TreeMap<RVMParameter, CodeCacheField>();
        for (RVMParameter key : itf.getQueryParams()) {
            CodeType keytype;
            if (CodeGenerationOption.isCacheKeyWeakReference())
                keytype = CodeHelper.RuntimeType.getWeakReference();
            else {
                // The legacy code does not provide type information of
                // parameters.
                // Also, it does not enforce a specific type; everything is
                // treated
                // as an Object object.
                keytype = CodeType.object();
            }
            String fieldname = CodeHelper.VariableName
                    .getIndexingTreeCacheKeyName(treename, key);
            CodeCacheField field = CodeCacheField.create(fieldname, keytype);
            keys.put(key, field);
        }

        if (keys.size() == 0) {
            // If this indexing tree does not have any keys, a cache is
            // meaningless.
            return null;
        }

        Entry lastentry = itf.lookupEntry(itf.getQueryParams());

        String fieldname = CodeHelper.VariableName
                .getIndexingTreeCacheValueName(treename);
        CodeCacheField field = CodeCacheField.create(fieldname,
                lastentry.getCodeType());

        return new IndexingCacheNew(keys, lastentry, field);
    }

    /**
     * Generates code for comparing the key of this cache with the parameters
     * carried by the event. The generated code can be used to check whether the
     * cache hits.
     *
     * @return generated code
     */
    public CodeExpr getKeyCompareCode() {
        CodeExpr prev = null;
        for (Map.Entry<RVMParameter, CodeCacheField> entry : this.keys
                .entrySet()) {
            RVMParameter param = entry.getKey();
            CodeCacheField field = entry.getValue();

            CodeVarRefExpr lhs;
            {
                // The legacy code does not provide any type information of
                // parameters.
                CodeType type = CodeType.object();
                CodeVariable paramvar = new CodeVariable(type, param.getName());
                lhs = new CodeVarRefExpr(paramvar);
            }

            CodeExpr rhs = field.generateGetCode();
            if (CodeGenerationOption.isCacheKeyWeakReference())
                rhs = new CodeMethodInvokeExpr(CodeType.object(), rhs, "get");

            CodeExpr eqexpr = CodeBinOpExpr.identical(lhs, rhs);
            if (prev == null)
                prev = eqexpr;
            else
                prev = CodeBinOpExpr.logicalAnd(prev, eqexpr);
        }
        return prev;
    }

    /**
     * Generates code for retrieving the cached value.
     *
     * @param weakrefs
     *            weak references, each of which corresponds to one parameter in
     *            the event
     * @param destref
     *            reference to the variable for holding the cached value
     * @return generated code
     */
    public CodeStmtCollection getCacheRetrievalCode(
            WeakReferenceVariables weakrefs, CodeVarRefExpr destref) {
        CodeStmtCollection stmts = new CodeStmtCollection();

        {
            CodeExpr fieldref = this.valueField.generateGetCode();
            CodeAssignStmt assign = new CodeAssignStmt(destref, fieldref);
            stmts.add(assign);
        }

        if (CodeGenerationOption.isCacheKeyWeakReference()) {
            // wr_p = cacheKey_p
            for (Map.Entry<RVMParameter, CodeCacheField> entry : this.keys
                    .entrySet()) {
                RVMParameter param = entry.getKey();
                CodeCacheField field = entry.getValue();
                CodeVariable weakref = weakrefs.getWeakRef(param);

                CodeAssignStmt assign = new CodeAssignStmt(new CodeVarRefExpr(
                        weakref), field.generateGetCode());
                stmts.add(assign);
            }
        }

        return stmts;
    }

    /**
     * Generates code that stores the keys and the value of the cache. The
     * generated code should guarantee that the stored value is always non-null.
     * At the time of writing this code, non-null is guaranteed by the caller.
     *
     * @param valueref
     *            reference to the value to be stored
     * @return generated code
     */
    public CodeStmtCollection getCacheUpdateCode(CodeVarRefExpr valueref) {
        CodeStmtCollection stmts = new CodeStmtCollection();

        for (Map.Entry<RVMParameter, CodeCacheField> entry : this.keys
                .entrySet()) {
            CodeCacheField field = entry.getValue();

            RVMParameter param = entry.getKey();
            CodeVariable paramvar = new CodeVariable(
                    field.getField().getType(), param.getName());
            CodeVarRefExpr keyref = new CodeVarRefExpr(paramvar);

            stmts.add(field.generateSetCode(keyref));
        }

        stmts.add(this.valueField.generateSetCode(valueref));

        return stmts;
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        for (CodeCacheField field : this.keys.values())
            field.getCode(fmt);

        this.valueField.getCode(fmt);
    }

    @Override
    public String toString() {
        ICodeFormatter fmt = CodeFormatters.getDefault();
        this.getCode(fmt);
        return fmt.getCode();
    }

    /**
     * This class represents a field for holding one sub-key or associated
     * value. For example, if the indexing tree is two-level, then the key would
     * consists of two sub-keys, one for each parameter. One instance of this
     * class holds one sub-key. Two different implementations are used:
     * CodeOrdinaryCacheField and CodeTLSCacheField. The former stores the
     * sub-key or value at a typical field, whereas the latter stores it at a
     * thread-local storage (TLS).
     *
     * @author Choonghwan Lee <clee83@illinois.edu>
     */
    static abstract class CodeCacheField implements ICodeGenerator {
        protected CodeMemberField field;

        public CodeMemberField getField() {
            return this.field;
        }

        public static CodeCacheField create(String fieldname, CodeType type) {
            if (Main.useFineGrainedLock)
                return new CodeTLSCacheField(fieldname, type);
            return new CodeOrdinaryCacheField(fieldname, type);
        }

        public abstract CodeExpr generateGetCode();

        public abstract CodeStmt generateSetCode(CodeVarRefExpr valueref);
    }

    /**
     * This class stores a sub-key or associated value at a typical field, which
     * is shared among all the threads. Any code that reads or updates this
     * field should be careful about data race.
     *
     * @author Choonghwan Lee <clee83@illinois.edu>
     */
    static class CodeOrdinaryCacheField extends CodeCacheField {
        CodeOrdinaryCacheField(String fieldname, CodeType type) {
            this.field = new CodeMemberField(fieldname, false, true, false,
                    type);
        }

        @Override
        public CodeExpr generateGetCode() {
            CodeFieldRefExpr fieldref = new CodeFieldRefExpr(this.field);
            return fieldref;
        }

        @Override
        public CodeStmt generateSetCode(CodeVarRefExpr rhs) {
            CodeFieldRefExpr fieldref = new CodeFieldRefExpr(this.field);
            CodeAssignStmt assign = new CodeAssignStmt(fieldref, rhs);
            return assign;
        }

        @Override
        public void getCode(ICodeFormatter fmt) {
            this.field.getCode(fmt);
        }
    }

    /**
     * This class stores a sub-key or associated value at TLS, to promote
     * concurrency.
     *
     * @author Choonghwan Lee <clee83@illinois.edu>
     */
    static class CodeTLSCacheField extends CodeCacheField {
        CodeTLSCacheField(String fieldname, CodeType type) {
            CodeType tlstype = CodeType.ThreadLocal(type);
            CodeExpr init = new CodeNewExpr(tlstype);
            this.field = new CodeMemberField(fieldname, false, true, false,
                    tlstype, init);
        }

        @Override
        public CodeExpr generateGetCode() {
            CodeFieldRefExpr fieldref = new CodeFieldRefExpr(this.field);
            CodeExpr invoke = new CodeMethodInvokeExpr(CodeType.object(),
                    fieldref, "get");
            return invoke;
        }

        @Override
        public CodeStmt generateSetCode(CodeVarRefExpr rhs) {
            CodeFieldRefExpr fieldref = new CodeFieldRefExpr(this.field);
            CodeExpr invoke = new CodeMethodInvokeExpr(CodeType.foid(),
                    fieldref, "set", rhs);
            return new CodeExprStmt(invoke);
        }

        @Override
        public void getCode(ICodeFormatter fmt) {
            this.field.getCode(fmt);
        }
    }
}
