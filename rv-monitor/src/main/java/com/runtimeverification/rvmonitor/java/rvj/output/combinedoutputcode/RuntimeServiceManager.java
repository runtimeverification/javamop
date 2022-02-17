package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode;

import java.util.ArrayList;
import java.util.List;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeExprStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeFieldRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeLiteralExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMemberField;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMemberMethod;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMemberStaticInitializer;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMethodInvokeExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeReturnStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmtCollection;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeVarRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeHelper;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeGenerator;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;

public class RuntimeServiceManager implements ICodeGenerator {
    private InternalBehaviorObservableCodeGenerator observer;
    private final List<ServiceDefinition> services;

    public InternalBehaviorObservableCodeGenerator getObserver() {
        return this.observer;
    }

    public RuntimeServiceManager() {
        this.observer = new InternalBehaviorObservableCodeGenerator(
                Main.internalBehaviorObserving);

        this.services = new ArrayList<ServiceDefinition>();

        this.services.add(this.addCleanerService());
        this.services.add(this.addRuntimeBehaviorOption());
        if (Main.internalBehaviorObserving)
            this.services.add(this.addObserverService());
    }

    private ServiceDefinition addCleanerService() {
        String desc = "Removing terminated monitors from partitioned sets";

        CodeStmtCollection init = new CodeStmtCollection();
        {
            CodeType type = CodeHelper.RuntimeType
                    .getTerminatedMonitorCleaner();
            CodeExpr start = new CodeMethodInvokeExpr(CodeType.foid(), type,
                    null, "start");
            init.add(new CodeExprStmt(start));
        }

        return new ServiceDefinition(desc, null, null, init);
    }

    private ServiceDefinition addObserverService() {
        String desc = "Observing internal behaviors";

        List<CodeMemberField> fields = new ArrayList<CodeMemberField>();
        {
            fields.add(this.observer.getField());
        }

        List<CodeMemberMethod> methods = new ArrayList<CodeMemberMethod>();
        {
            CodeFieldRefExpr fieldref = new CodeFieldRefExpr(
                    this.observer.getField());
            // getObservable()
            {
                CodeType rettype = CodeHelper.RuntimeType
                        .getObserverable(CodeHelper.RuntimeType
                                .getInternalBehaviorObserver());
                CodeStmt body = new CodeReturnStmt(fieldref);
                CodeMemberMethod method = new CodeMemberMethod("getObservable",
                        true, true, true, rettype, false, body);
                methods.add(method);
            }
            // subscribe(o)
            {
                CodeVariable param1 = new CodeVariable(
                        CodeHelper.RuntimeType.getInternalBehaviorObserver(),
                        "o");
                CodeExpr invoke = new CodeMethodInvokeExpr(CodeType.foid(),
                        fieldref, "subscribe", new CodeVarRefExpr(param1));
                CodeStmt body = new CodeExprStmt(invoke);
                CodeMemberMethod method = new CodeMemberMethod("subscribe",
                        true, true, true, CodeType.foid(), false, body, param1);
                methods.add(method);
            }
            // unsubscribe(o)
            {
                CodeVariable param1 = new CodeVariable(
                        CodeHelper.RuntimeType.getInternalBehaviorObserver(),
                        "o");
                CodeExpr invoke = new CodeMethodInvokeExpr(CodeType.foid(),
                        fieldref, "unsubscribe", new CodeVarRefExpr(param1));
                CodeStmt body = new CodeExprStmt(invoke);
                CodeMemberMethod method = new CodeMemberMethod("unsubscribe",
                        true, true, true, CodeType.foid(), false, body, param1);
                methods.add(method);
            }
        }

        return new ServiceDefinition(desc, fields, methods, null);
    }

    private ServiceDefinition addRuntimeBehaviorOption() {
        String desc = "Setting the behavior of the runtime library according to the compile-time option";

        CodeStmtCollection init = new CodeStmtCollection();
        {
            CodeType type = CodeHelper.RuntimeType.getRuntimeOption();
            CodeExpr enabled = CodeLiteralExpr.bool(Main.useFineGrainedLock);
            CodeExpr invoke = new CodeMethodInvokeExpr(CodeType.foid(), type,
                    null, "enableFineGrainedLock", enabled);
            init.add(new CodeExprStmt(invoke));
        }

        return new ServiceDefinition(desc, null, null, init);
    }

    static class ServiceDefinition {
        protected final String description;
        protected final List<CodeMemberField> fields;
        protected final List<CodeMemberMethod> methods;
        protected final CodeMemberStaticInitializer initializer;

        protected ServiceDefinition(String desc, List<CodeMemberField> fields,
                List<CodeMemberMethod> methods, CodeStmtCollection init) {
            this.description = desc;
            this.fields = fields;
            this.methods = methods;
            this.initializer = new CodeMemberStaticInitializer(
                    init == null ? new CodeStmtCollection() : init);
        }
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        for (ServiceDefinition def : this.services) {
            fmt.comment(def.description);

            if (def.fields != null) {
                for (CodeMemberField field : def.fields)
                    field.getCode(fmt);
            }

            if (def.methods != null) {
                for (CodeMemberMethod method : def.methods)
                    method.getCode(fmt);
            }

            if (def.initializer != null)
                def.initializer.getCode(fmt);
        }
    }
}
