package com.runtimeverification.rvmonitor.java.rvj.output.monitorset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeAssignStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeClassDef;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeExprStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeFieldRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeLiteralExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMemberField;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMemberMethod;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMemberStaticInitializer;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMethodInvokeExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeNewExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodePhantomStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeReturnStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmtCollection;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeSynchronizedBlockStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeThisRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeTryCatchFinallyStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeVarDeclStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeVarRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeFormatters;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeHelper;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeRVType;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.GlobalLock;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event.itf.WeakReferenceVariables;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.IndexingTreeManager;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingDeclNew;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeImplementation.Access;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeImplementation.Entry;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeImplementation.StmtCollectionInserter;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeInterface;
import com.runtimeverification.rvmonitor.java.rvj.output.monitor.BaseMonitor;
import com.runtimeverification.rvmonitor.java.rvj.output.monitor.MonitorFeatures;
import com.runtimeverification.rvmonitor.java.rvj.output.monitor.SuffixMonitor;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.EventDefinition;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.PropertyAndHandlers;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;

public class MonitorSet {
    private final RVMVariable setName;
    private final RVMVariable monitorName;
    private final SuffixMonitor monitor;

    private final ArrayList<EventDefinition> events;
    private boolean existSkip = false;

    private final RVMVariable loc = new RVMVariable("RVM_loc");

    private GlobalLock monitorLock;

    private final RVMonitorSpec rvmSpec;
    private IndexingTreeManager indexingTreeManager;
    private final boolean usePartitionedSet;
    private TreeMap<RVMParameter, IndexingTreeInterface> treemap;
    private TreeMap<RVMParameter, CodeMemberField> treefields;
    private TreeMap<EventDefinition, CodeMemberField> handlerfields;
    private final CodeMemberField typewiseLock;

    public boolean isPartitionedSet() {
        return this.usePartitionedSet;
    }

    public MonitorSet(String name, RVMonitorSpec rvmSpec, SuffixMonitor monitor) {
        this.rvmSpec = rvmSpec;

        this.monitorName = monitor.getOutermostName();
        this.monitor = monitor;
        this.setName = new RVMVariable(monitorName + "_Set");
        this.events = new ArrayList<EventDefinition>(rvmSpec.getEvents());

        for (PropertyAndHandlers prop : rvmSpec.getPropertiesAndHandlers()) {
            for (String handler : prop.getHandlers().values()) {
                if (handler.indexOf("__SKIP") != -1) {
                    existSkip = true;
                }
            }
        }
        for (EventDefinition event : events) {
            if (event.has__SKIP()) {
                existSkip = true;
                break;
            }
        }

        this.usePartitionedSet = this.determineIfPartitionedSetApplicable();

        if (this.usePartitionedSet) {
            MonitorFeatures features = this.monitor.getFeatures();
            features.forceKeepWeakRefsInMonitor();
        }

        {
            CodeMemberField field = null;
            if (this.usePartitionedSet)
                field = CodeHelper.RuntimeType.getSetWiseLockField("lock");
            this.typewiseLock = field;
        }
    }

    public RVMVariable getName() {
        return setName;
    }

    public void setMonitorLock(String lockName) {
        this.monitorLock = new GlobalLock(new RVMVariable(lockName));
    }

    public void setIndexingTreeManager(IndexingTreeManager manager) {
        this.indexingTreeManager = manager;
    }

    /**
     * Initializes the 'treemap' field based on the provided argument. This
     * method does not assume that each indexing tree has its own
     * implementation. Thus, what this method can do is restricted.
     *
     * @param indexingTreeDecl
     */
    public void feedIndexingTreeInterface(IndexingDeclNew indexingTreeDecl) {
        if (this.usePartitionedSet) {
            this.treemap = new TreeMap<RVMParameter, IndexingTreeInterface>();

            TreeMap<RVMParameters, IndexingTreeInterface> trees = indexingTreeDecl
                    .getIndexingTrees();
            for (Map.Entry<RVMParameters, IndexingTreeInterface> entry : trees
                    .entrySet()) {
                RVMParameters prms = entry.getKey();
                if (prms.size() != 1)
                    continue;
                RVMParameter prm = prms.get(0);

                IndexingTreeInterface treeitf = entry.getValue();
                this.treemap.put(prm, treeitf);
            }
        }
    }

    /**
     * Initializes the 'treefields' field based on the provided argument. This
     * method assumes that implementations of indexing trees are initialized.
     *
     * @param indexingTreeDecl
     */
    public void feedIndexingTreeImplementation(IndexingDeclNew indexingTreeDecl) {
        if (this.usePartitionedSet) {
            this.treefields = new TreeMap<RVMParameter, CodeMemberField>();

            TreeMap<RVMParameters, IndexingTreeInterface> trees = indexingTreeDecl
                    .getIndexingTrees();
            for (Map.Entry<RVMParameters, IndexingTreeInterface> entry : trees
                    .entrySet()) {
                RVMParameters prms = entry.getKey();
                if (prms.size() != 1)
                    continue;
                RVMParameter prm = prms.get(0);

                IndexingTreeInterface treeitf = entry.getValue();
                CodeMemberField treefield = treeitf.getImplementation()
                        .getField();
                CodeMemberField setfield = new CodeMemberField(
                        treefield.getName(), false, false, true,
                        treefield.getType());
                this.treefields.put(prm, setfield);
            }
        }

        this.initializeHandlerFields();
    }

    /**
     * Initializes the 'handlerfields' field. This method assumes that the
     * 'treefields' field is initialized.
     */
    private void initializeHandlerFields() {
        if (this.usePartitionedSet) {
            this.handlerfields = new TreeMap<EventDefinition, CodeMemberField>();
            for (EventDefinition evt : this.events) {
                CodeType monitortype = new CodeType(
                        this.monitorName.getVarName());
                CodeType pairmaptype = this.getPairMapType(evt);
                CodeType type = CodeHelper.RuntimeType.getSetEventDelegator(
                        monitortype, pairmaptype);
                String name = evt.getId() + "Handler";
                CodeMemberField field = new CodeMemberField(name, false, true,
                        true, type);
                this.handlerfields.put(evt, field);
            }
        }
    }

    private boolean determineIfPartitionedSetApplicable() {
        if (!Main.usePartitionedSet)
            return false;

        // This checks whether each starting event carries all the parameters,
        // which is necessary to apply the partitioned set.
        if (this.rvmSpec.isGeneral())
            return false;

        // Probably, partitioned sets can be used for a specification that uses
        // more than two parameters, but, at this moment, its use is restricted
        // to a very special case, when there are exactly two parameters. When
        // there is only one parameter, a set is unnecessary, anyway.
        RVMParameters params = this.rvmSpec.getParameters();
        if (params.size() != 2)
            return false;

        // Additionally, non-creation events should carry exactly one parameter.
        // This is necessary, in the current assumption, because the set
        // corresponding
        // to the other parameter should be invalidated. I believe this
        // restriction
        // can be relaxed.
        for (EventDefinition evt : this.events) {
            if (!evt.isStartEvent()) {
                if (evt.getParameters().size() != 1)
                    return false;
            }
        }

        // Also, I assume that there is at least one event that carries each
        // parameter only.
        {
            RVMParameters accumulated = null;
            for (EventDefinition evt : this.events) {
                if (!evt.isStartEvent()) {
                    RVMParameters evtprms = evt.getParameters();
                    if (evtprms.size() == 1) {
                        if (accumulated == null)
                            accumulated = evtprms;
                        else
                            accumulated = RVMParameters.unionSet(accumulated,
                                    evtprms);
                    }
                }
            }
            if (accumulated == null
                    || !accumulated.equals(this.rvmSpec.getParameters()))
                return false;
        }

        return true;
    }

    public CodeStmtCollection generateMonitoringCode(CodeVarRefExpr setref,
            EventDefinition event, GlobalLock enforcelock) {
        CodeStmtCollection stmts = new CodeStmtCollection();

        /*
         * The following code is just fine, but, for the sake of keeping the
         * legacy code, which might do something important, let's use the legacy
         * code. String methodname = "event_" + event.getId(); List<CodeExpr>
         * args = new ArrayList<CodeExpr>(); RVMParameters passing; if
         * (Main.stripUnusedParameterInMonitor) passing =
         * event.getReferredParameters(event.getRVMParameters()); else passing =
         * event.getRVMParameters(); for (RVMParameter param : passing)
         * args.add(CodeExpr.fromLegacy(CodeType.object(), param.getName()));
         * CodeExpr invoke = new CodeMethodInvokeExpr(CodeType.foid(), setref,
         * methodname, args); stmts.add(new CodeExprStmt(invoke));
         */

        RVMVariable monitorvar = setref.getVariable().toLegacy();
        String mntcode = this.Monitoring(monitorvar, event, null, enforcelock);
        stmts.add(CodeStmtCollection.fromLegacy(mntcode));

        // The referred variable is marked so that the dead-code elimination
        // step
        // won't remove the definition of the variable.
        stmts.add(new CodePhantomStmt(setref.getVariable()));

        return stmts;
    }

    public String Monitoring(RVMVariable monitorSetVar, EventDefinition event,
            RVMVariable loc, GlobalLock lock) {
        this.monitorLock = lock;
        String ret = "";

        // Let's check this at the caller.
        // ret += "if(" + monitorSetVar + " != null) {\n";

        // if (has__LOC) {
        // if (loc != null)
        // ret += monitorSetVar + "." + this.loc + " = " + loc + ";\n";
        // else
        // ret += monitorSetVar + "." + this.loc + " = " +
        // "Thread.currentThread().getStackTrace()[2].toString()"
        // + ";\n";
        // }

        ret += monitorSetVar + ".event_" + event.getId() + "(";
        {
            RVMParameters passing;
            if (Main.stripUnusedParameterInMonitor)
                passing = event.getReferredParameters(event.getRVMParameters());
            else
                passing = event.getRVMParameters();
            ret += passing.parameterString();
        }
        ret += ");\n";

        if (!Main.eliminatePresumablyRemnantCode) {
            // What's the point of this?
            for (RVMVariable var : monitor.getCategoryVars()) {
                ret += BaseMonitor.getNiceVariable(var) + " = " + monitorSetVar
                        + "." + BaseMonitor.getNiceVariable(var) + ";\n";
            }
        }

        // ret += "}\n";

        return ret;
    }

    public CodeRVType.MonitorSet getCodeType() {
        String typename = this.setName.getVarName();
        if (this.usePartitionedSet) {
            List<RVMParameter> ctorparams = this.getSortedParameterList();
            Map<RVMParameter, List<IndexingTreeInterface>> ctorargsmap = new HashMap<RVMParameter, List<IndexingTreeInterface>>();

            for (Map.Entry<RVMParameter, IndexingTreeInterface> entry : this.treemap
                    .entrySet()) {
                RVMParameter prm = entry.getKey();
                List<IndexingTreeInterface> ctorargs = new ArrayList<IndexingTreeInterface>();
                for (RVMParameter ctorprm : ctorparams) {
                    IndexingTreeInterface ctorarg = null;
                    if (!prm.equals(ctorprm))
                        ctorarg = this.treemap.get(ctorprm);
                    ctorargs.add(ctorarg);
                }
                ctorargsmap.put(prm, ctorargs);
            }

            return CodeRVType.forPartitionedMonitorSet(typename, ctorargsmap);
        } else
            return CodeRVType.forBasicMonitorSet(typename);
    }

    public CodeType getMonitorCodeType() {
        return new CodeType(this.monitorName.getVarName());
    }

    private void generatePartitionedSetConstructorBody(ICodeFormatter fmt) {
        CodeStmtCollection stmts = new CodeStmtCollection();

        CodeType monitortype = this.getMonitorCodeType();
        CodeType settype = this.getCodeType();

        // Call the constructor of the superclass.
        CodeExpr invokesuper = new CodeMethodInvokeExpr(CodeType.foid(), null,
                "super", new CodeMethodInvokeExpr(CodeType.integer(),
                        monitortype, null, "getNumberOfStates"),
                        new CodeMethodInvokeExpr(CodeType.integer(), monitortype, null,
                                "getNumberOfEvents"));
        stmts.add(new CodeExprStmt(invokesuper));

        // Keep the maps that point to the pair set.
        for (CodeMemberField field : this.treefields.values()) {
            CodeStmt assign = new CodeAssignStmt(new CodeFieldRefExpr(
                    new CodeThisRefExpr(settype), field), new CodeVarRefExpr(
                            new CodeVariable(field.getType(), field.getName())));
            stmts.add(assign);
        }

        // Register this set to the TerminatedMonitorCleaner, so that
        // unnecessary monitors in the set
        // can be reclaimed.
        {
            CodeType cleanertype = CodeHelper.RuntimeType
                    .getTerminatedMonitorCleaner();
            CodeExpr lockref = new CodeFieldRefExpr(this.typewiseLock);
            CodeExpr addset = new CodeMethodInvokeExpr(CodeType.foid(),
                    cleanertype, null, "addSet", new CodeThisRefExpr(
                            this.getCodeType()), lockref);
            stmts.add(new CodeExprStmt(addset));
        }

        if (Main.internalBehaviorObserving) {
            // TODO: Some code should be added.
        }

        stmts.getCode(fmt);
    }

    private CodeMemberStaticInitializer generatePartitionedSetStaticInitializer() {
        CodeStmtCollection stmts = new CodeStmtCollection();

        // Initialize handlers.
        for (Map.Entry<EventDefinition, CodeMemberField> entry : this.handlerfields
                .entrySet()) {
            EventDefinition evt = entry.getKey();
            CodeMemberField field = entry.getValue();
            CodeClassDef klass = this
                    .generateHandlerClass(evt, field.getType());
            CodeExpr create = new CodeNewExpr(field.getType(), klass);
            CodeStmt assign = new CodeAssignStmt(new CodeFieldRefExpr(field),
                    create);
            stmts.add(assign);
        }

        return new CodeMemberStaticInitializer(stmts);
    }

    private CodeClassDef generateHandlerClass(EventDefinition evt,
            CodeType klasstype) {
        CodeClassDef klass = CodeClassDef.anonymous(klasstype);

        CodeType monitortype = new CodeType(this.monitorName.getVarName());
        CodeType pairmaptype = this.getPairMapType(evt);

        // Defines commit().
        {
            final CodeVarRefExpr monitorref = new CodeVarRefExpr(
                    new CodeVariable(monitortype, "monitor"));
            final CodeVarRefExpr pairmapref = new CodeVarRefExpr(
                    new CodeVariable(pairmaptype, "pairmap"));
            CodeStmtCollection body = this.monitor
                    .generateMonitorTransitionedCode(monitorref, evt, null);

            if (!evt.isStartEvent()) {
                // Manipulates the pair set.
                RVMParameter pairparam = this.getPairParameter(evt);
                WeakReferenceVariables weakrefs = new WeakReferenceVariables(
                        this.indexingTreeManager, pairparam);

                CodeMemberField weakreffield = CodeHelper.VariableName
                        .getWeakRefInMonitor(pairparam,
                                CodeHelper.RuntimeType.getWeakReference());
                CodeVariable weakrefvar = weakrefs.getWeakRef(pairparam);
                body.add(new CodeVarDeclStmt(weakrefvar, new CodeFieldRefExpr(
                        monitorref, weakreffield)));

                IndexingTreeInterface pairtree = this.treemap.get(pairparam);
                // CodeFieldRefExpr pairmapref = new CodeFieldRefExpr(new
                // CodeThisRefExpr(klasstype), pairmapfield);
                StmtCollectionInserter<CodeExpr> inserter = new StmtCollectionInserter<CodeExpr>() {
                    @Override
                    public CodeStmtCollection insertLastField(Entry entry,
                            CodeExpr setref) {
                        CodeExpr invalidate = new CodeMethodInvokeExpr(
                                CodeType.foid(), setref, "invalidate",
                                monitorref);
                        return new CodeStmtCollection(new CodeExprStmt(
                                invalidate));
                    }
                };
                body.add(pairtree.generateFindCode(pairmapref, Access.Set,
                        weakrefs, inserter));
            }

            CodeMemberMethod method = new CodeMemberMethod("commit", true,
                    false, true, CodeType.foid(), true, body,
                    monitorref.getVariable(), pairmapref.getVariable());
            klass.addMethod(method);
        }

        return klass;
    }

    private CodeStmtCollection generateSynchronizedBlock(CodeStmt... body) {
        return this.generateSynchronizedBlock(new CodeStmtCollection(body));
    }

    private CodeStmtCollection generateSynchronizedBlock(CodeStmtCollection body) {
        // To use more professional lock operations, such as tryLock(),
        // synchronized
        // blocks do not seem proper.
        boolean useclasslock = false;

        if (useclasslock) {
            // synchronized (type.class) { ... }
            CodeExpr lockobj = new CodeFieldRefExpr(this.getCodeType(),
                    new CodeMemberField("class", true, false, true,
                            CodeType.klass()));
            CodeStmt synchblock = new CodeSynchronizedBlockStmt(lockobj, body);
            return new CodeStmtCollection(synchblock);
        } else {
            // try { lock.lock(); ... } finally { lock.unlock(); }
            CodeStmtCollection tryblock = new CodeStmtCollection();
            {
                CodeExpr lock = new CodeMethodInvokeExpr(CodeType.foid(),
                        new CodeFieldRefExpr(this.typewiseLock), "lock");
                tryblock.add(new CodeExprStmt(lock));

                tryblock.add(body);
            }

            CodeStmtCollection finallyblock = new CodeStmtCollection();
            {
                CodeExpr unlock = new CodeMethodInvokeExpr(CodeType.foid(),
                        new CodeFieldRefExpr(this.typewiseLock), "unlock");
                finallyblock.add(new CodeExprStmt(unlock));
            }

            CodeStmt tryfinallyblock = new CodeTryCatchFinallyStmt(tryblock,
                    finallyblock);
            return new CodeStmtCollection(tryfinallyblock);
        }
    }

    private void generateTerminateMethod(ICodeFormatter fmt) {
        CodeVariable param1 = new CodeVariable(CodeType.integer(), "treeid");

        CodeStmtCollection body = new CodeStmtCollection();
        {
            CodeExpr invokesuper = new CodeMethodInvokeExpr(CodeType.foid(),
                    new CodeThisRefExpr(this.getCodeType()),
                    "terminateInternal", new CodeVarRefExpr(param1));
            body.add(new CodeExprStmt(invokesuper));

            CodeType cleanertype = CodeHelper.RuntimeType
                    .getTerminatedMonitorCleaner();
            CodeExpr addset = new CodeMethodInvokeExpr(CodeType.foid(),
                    cleanertype, null, "removeSet", new CodeThisRefExpr(
                            this.getCodeType()));
            body.add(new CodeExprStmt(addset));
        }

        CodeMemberMethod method = new CodeMemberMethod("terminate", true,
                false, true, CodeType.foid(), true, body, param1);
        method.getCode(fmt);
    }

    private void generateAddMethod(ICodeFormatter fmt) {
        CodeVariable param1 = new CodeVariable(this.getMonitorCodeType(),
                "monitor");

        CodeExpr invokeadd = new CodeMethodInvokeExpr(CodeType.foid(),
                new CodeThisRefExpr(this.getMonitorCodeType()),
                "addUnprotected", new CodeVarRefExpr(param1));
        CodeStmtCollection body = this
                .generateSynchronizedBlock(new CodeExprStmt(invokeadd));

        CodeMemberMethod method = new CodeMemberMethod("add", true, false,
                true, CodeType.foid(), false, body, param1);
        method.getCode(fmt);
    }

    private CodeStmtCollection generateEventHandler(EventDefinition evt) {
        CodeMemberField handlerfield = this.handlerfields.get(evt);
        CodeExpr target = new CodeFieldRefExpr(handlerfield);
        CodeExpr arg1 = new CodeThisRefExpr(this.getCodeType());
        CodeExpr arg2;
        if (evt.isStartEvent())
            arg2 = CodeLiteralExpr.nul();
        else {
            CodeMemberField pairmapfield = this.getPairMapField(evt);
            arg2 = new CodeFieldRefExpr(
                    new CodeThisRefExpr(this.getCodeType()), pairmapfield);
        }
        CodeExpr invoke = new CodeMethodInvokeExpr(CodeType.foid(), target,
                "fireEvent", arg1, arg2);
        CodeStmtCollection body = new CodeStmtCollection(new CodeExprStmt(
                invoke));

        boolean synch = true;
        if (!synch)
            return body;
        return this.generateSynchronizedBlock(body);
    }

    private Map.Entry<RVMParameter, CodeMemberField> getPair(EventDefinition evt) {
        // Since we apply the partition-set optimization only when there are two
        // parameters,
        // finding the pair set is straightforward.
        for (Map.Entry<RVMParameter, CodeMemberField> entry : this.treefields
                .entrySet()) {
            RVMParameter setprm = entry.getKey();
            RVMParameters evtprms = evt.getParameters();

            if (!evtprms.contains(setprm))
                return entry;
        }
        throw new IllegalArgumentException();
    }

    private RVMParameter getPairParameter(EventDefinition evt) {
        Map.Entry<RVMParameter, CodeMemberField> entry = this.getPair(evt);
        return entry.getKey();
    }

    private CodeMemberField getPairMapField(EventDefinition evt) {
        Map.Entry<RVMParameter, CodeMemberField> entry = this.getPair(evt);
        return entry.getValue();
    }

    private CodeType getPairMapType(EventDefinition evt) {
        if (evt.isStartEvent())
            return CodeHelper.RuntimeType.getAbstractIndexingTree();
        else {
            CodeMemberField field = this.getPairMapField(evt);
            return field.getType();
        }
    }

    private List<RVMParameter> getSortedParameterList() {
        ArrayList<RVMParameter> prmlist = new ArrayList<RVMParameter>(
                this.treemap.keySet());
        Collections.sort(prmlist);
        return prmlist;
    }

    @Override
    public String toString() {
        boolean synch = Main.useFineGrainedLock;
        // A partitioned-set is capable of synchronizing itself. Thus, an event
        // handling method does not need to be synchronized.
        if (this.usePartitionedSet)
            synch = false;

        String ret = "";

        RVMVariable monitor = new RVMVariable("monitor");
        // RVMVariable num_terminated_monitors = new
        // RVMVariable("num_terminated_monitors");
        RVMVariable numAlive = new RVMVariable("numAlive");
        RVMVariable loopindex = new RVMVariable("i");
        // elementData and size are safe since they will be accessed by the
        // prefix "this.".

        ret += "final class " + setName + " extends ";
        if (this.usePartitionedSet)
            ret += "com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractPartitionedMonitorSet<"
                    + monitorName + ">";
        else
            ret += "com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractMonitorSet<"
                    + monitorName + ">";
        ret += " {\n";

        // if (has__LOC)
        // ret += "String " + loc + " = null;\n";

        if (existSkip)
            ret += "boolean " + BaseMonitor.skipEvent + " = false;\n";

        if (!Main.eliminatePresumablyRemnantCode) {
            for (RVMVariable var : this.monitor.getCategoryVars()) {
                ret += "boolean " + BaseMonitor.getNiceVariable(var) + ";\n";
            }
        }

        ret += "\n";

        // Fields : event handler
        if (this.usePartitionedSet) {
            ICodeFormatter fmt = CodeFormatters.getDefault();
            for (Map.Entry<RVMParameter, CodeMemberField> entry : this.treefields
                    .entrySet()) {
                CodeMemberField field = entry.getValue();
                field.getCode(fmt);
            }
            for (Map.Entry<EventDefinition, CodeMemberField> entry : this.handlerfields
                    .entrySet()) {
                CodeMemberField field = entry.getValue();
                field.getCode(fmt);
            }
            ret += fmt.getCode();
        }

        // Field & property : set-wise lock
        if (this.usePartitionedSet) {
            ICodeFormatter fmt = CodeFormatters.getDefault();
            CodeMemberField field = this.typewiseLock;
            field.getCode(fmt);
            CodeStmt get = new CodeReturnStmt(new CodeFieldRefExpr(field));
            CodeMemberMethod property = new CodeMemberMethod("getLock", true,
                    true, false, field.getType(), false, get);
            property.getCode(fmt);
            ret += fmt.getCode();
        }

        // static initializer
        if (this.usePartitionedSet) {
            ICodeFormatter fmt = CodeFormatters.getDefault();
            CodeMemberStaticInitializer init = this
                    .generatePartitionedSetStaticInitializer();
            init.getCode(fmt);
            ret += fmt.getCode();
        }

        // Constructor
        if (this.usePartitionedSet) {
            ret += this.setName + "(";
            List<RVMParameter> prmlist = this.getSortedParameterList();
            for (int i = 0; i < prmlist.size(); ++i) {
                if (i > 0)
                    ret += ", ";
                CodeMemberField field = this.treefields.get(prmlist.get(i));
                ret += field.getType().toString();
                ret += " ";
                ret += field.getName();
            }
            ret += ") {\n";

            ICodeFormatter fmt = CodeFormatters.getDefault();
            this.generatePartitionedSetConstructorBody(fmt);
            ret += fmt.getCode();
            ret += "}\n";
        } else {
            ret += setName + "(){\n";
            ret += "this.size = 0;\n";
            ret += "this.elements = new " + monitorName + "[4];\n";
            ret += "}\n";
        }

        if (this.usePartitionedSet) {
            ICodeFormatter fmt = CodeFormatters.getDefault();
            this.generateTerminateMethod(fmt);
            this.generateAddMethod(fmt);
            ret += fmt.getCode();
        }

        for (EventDefinition event : this.events) {
            String eventName = event.getId();
            RVMParameters parameters;
            if (Main.stripUnusedParameterInMonitor)
                parameters = event.getReferredParameters(event
                        .getRVMParameters());
            else
                parameters = event.getRVMParameters();

            ret += "final" + (synch ? " synchronized " : " ") + "void event_"
                    + eventName + "(";
            ret += parameters.parameterDeclString();
            ret += ") {\n";

            if (this.usePartitionedSet) {
                ICodeFormatter fmt = CodeFormatters.getDefault();
                CodeStmtCollection stmts = this.generateEventHandler(event);
                stmts.getCode(fmt);
                ret += fmt.getCode();
            } else {
                if (!Main.eliminatePresumablyRemnantCode) {
                    for (RVMVariable var : this.monitor.getCategoryVars()) {
                        ret += "this." + BaseMonitor.getNiceVariable(var)
                                + " = " + "false;\n";
                    }
                }

                ret += "int " + numAlive + " = 0 ;\n";
                ret += "for(int " + loopindex + " = 0; " + loopindex
                        + " < this.size; " + loopindex + "++){\n";
                ret += monitorName + " " + monitor + " = this.elements["
                        + loopindex + "];\n";
                ret += "if(!" + monitor + ".isTerminated()){\n";
                ret += "elements[" + numAlive + "] = " + monitor + ";\n";
                ret += numAlive + "++;\n";
                ret += "\n";
                ret += this.monitor.Monitoring(monitor, event, loc,
                        this.monitorLock, this.monitor.getOutputName(), true);
                ret += "}\n";
                ret += "}\n";

                ret += "for(int " + loopindex + " = " + numAlive + "; "
                        + loopindex + " < this.size; " + loopindex + "++){\n";
                ret += "this.elements[" + loopindex + "] = null;\n";
                ret += "}\n";
                ret += "size = numAlive;\n";
            }
            ret += "}\n";
        }

        ret += "}\n";

        return ret;
    }

    public Set<RVMVariable> getCategoryVars() {
        return this.monitor.getCategoryVars();
    }
}
