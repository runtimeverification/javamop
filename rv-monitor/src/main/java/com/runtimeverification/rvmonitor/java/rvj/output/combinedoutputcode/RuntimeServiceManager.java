package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode;

import java.util.ArrayList;
import java.util.List;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeClassDef;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeCommentStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeExprStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeFieldRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeLiteralExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMemberField;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMemberMethod;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMemberStaticInitializer;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMethodInvokeExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeNewExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeReturnStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmtCollection;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeTryCatchFinallyStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeVarDeclStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeVarRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeWhileStmt;
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
                Main.options.internalBehaviorObserving);

        this.services = new ArrayList<>();

        this.services.add(this.addCleanerService());
        this.services.add(this.addRuntimeBehaviorOption());
        if (Main.options.internalBehaviorObserving)
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

        CodeStmtCollection init = createObserverRegisterCode();

        return new ServiceDefinition(desc, fields, methods, init);
    }

    private CodeStmtCollection createObserverRegisterCode() {
        CodeCommentStmt comment = new CodeCommentStmt("Register observers");
        CodeTryCatchFinallyStmt observerCode = getObserverCode();
        CodeExprStmt shutdownHookCode = getShutDownHookCode();
        CodeStmtCollection init = new CodeStmtCollection();
        init.add(comment);
        init.add(observerCode);
        init.add(shutdownHookCode);
        return init;
    }

    private CodeExprStmt getShutDownHookCode() {
        // List<IInternalBehaviorObserver> obs = observer.getObservers();
        CodeType iObserverType = new CodeType("IInternalBehaviorObserver");
        CodeType listType = new CodeType(null,"List", iObserverType);
        CodeVariable obs = new CodeVariable(listType, "obs");
        CodeVarDeclStmt createWriter = new CodeVarDeclStmt(obs,
                new CodeMethodInvokeExpr(null, new CodeFieldRefExpr(this.observer.getField()), "getObservers"));
        // for (IInternalBehaviorObserver o : obs) { o.onCompleted(); }
        CodeType iteratorType = new CodeType(null,"Iterator", iObserverType);
        CodeVariable iterator = new CodeVariable(iteratorType, "iter");
        CodeVarDeclStmt createIterator = new CodeVarDeclStmt(iterator,
                new CodeMethodInvokeExpr(null, new CodeVarRefExpr(obs), "iterator"));
        CodeMethodInvokeExpr loopHeader = new CodeMethodInvokeExpr(null, new CodeVarRefExpr(iterator), "hasNext");
        CodeMethodInvokeExpr nextElem = new CodeMethodInvokeExpr(null, new CodeVarRefExpr(iterator), "next");
        CodeMethodInvokeExpr onCompleted = new CodeMethodInvokeExpr(null, nextElem, "onCompleted");
        CodeStmtCollection body = new CodeStmtCollection();
        body.add(new CodeExprStmt(onCompleted));
        CodeWhileStmt loop =  new CodeWhileStmt(loopHeader, body);
        CodeStmtCollection methodBody = new CodeStmtCollection();
        methodBody.add(createWriter);
        methodBody.add(createIterator);
        methodBody.add(loop);
        CodeMemberMethod run = new CodeMemberMethod("run", true, false, false, CodeType.foid(), true, methodBody);
        CodeType threadType = new CodeType("Thread");
        CodeClassDef thread = CodeClassDef.anonymous(threadType);
        thread.addMethod(run);
        CodeType runtimeType = new CodeType("Runtime");
        CodeMethodInvokeExpr getRuntime = new CodeMethodInvokeExpr(runtimeType, CodeExpr.fromLegacy(runtimeType, "Runtime"), "getRuntime");
        CodeMethodInvokeExpr addShutdownHook = new CodeMethodInvokeExpr(null, getRuntime, "addShutdownHook", new CodeNewExpr(threadType, thread));
        return new CodeExprStmt(addShutdownHook);
    }

    private CodeTryCatchFinallyStmt getObserverCode() {
        CodeType fileType = new CodeType("File");
        CodeStmtCollection tryBlock = getTryBlock(fileType);
        CodeTryCatchFinallyStmt observerCode = new CodeTryCatchFinallyStmt(tryBlock, null, getCatchBlock(fileType));
        return observerCode;
    }

    private CodeStmtCollection getTryBlock(CodeType fileType) {
        CodeType writer = new CodeType("PrintWriter");
        CodeVariable dumpWriterVar = new CodeVariable(writer, "dumpWriter");
        CodeType behaviorDumper = new CodeType("InternalBehaviorDumper");
        CodeVarDeclStmt createDumpWriter = new CodeVarDeclStmt(dumpWriterVar,
                new CodeNewExpr(writer, new CodeNewExpr(fileType, CodeLiteralExpr.string("/tmp/internal.txt"))));
        CodeVariable dumper = new CodeVariable(behaviorDumper, "dumper");
        CodeVarDeclStmt createDumper = new CodeVarDeclStmt(dumper,
                new CodeNewExpr(behaviorDumper, new CodeVarRefExpr(dumpWriterVar)));
        CodeExprStmt registerDumperStatement =
                new CodeExprStmt(new CodeMethodInvokeExpr(null, null, "subscribe", new CodeVarRefExpr(dumper)));


        CodeVariable traceWriterVar = new CodeVariable(writer, "traceWriter");
        CodeType traceWriter = new CodeType("MonitorTraceCollector");
        CodeVarDeclStmt createTraceWriter = new CodeVarDeclStmt(traceWriterVar,
                new CodeNewExpr(writer, new CodeNewExpr(fileType, CodeLiteralExpr.string("/tmp/traces.txt"))));
        CodeVariable tracer = new CodeVariable(traceWriter, "tracer");
        CodeVarDeclStmt createTracer = new CodeVarDeclStmt(tracer,
                new CodeNewExpr(traceWriter, new CodeVarRefExpr(traceWriterVar)));
        CodeExprStmt registerTracerStatement =
                new CodeExprStmt(new CodeMethodInvokeExpr(null, null, "subscribe", new CodeVarRefExpr(tracer)));


        CodeStmtCollection tryBlock = new CodeStmtCollection();
        tryBlock.add(createDumpWriter);
        tryBlock.add(createTraceWriter);
        tryBlock.add(createDumper);
        tryBlock.add(createTracer);
        tryBlock.add(registerDumperStatement);
        tryBlock.add(registerTracerStatement);
        return tryBlock;
    }

    private CodeTryCatchFinallyStmt.CatchBlock getCatchBlock(CodeType fileType) {
        CodeStmtCollection catchCode = new CodeStmtCollection();
        CodeVariable fnf = new CodeVariable(new CodeType("FileNotFoundException"), "fnf");
        CodeStmt printStackTrace = new CodeExprStmt(
                new CodeMethodInvokeExpr(fileType, new CodeVarRefExpr(fnf), "printStackTrace"));
        catchCode.add(printStackTrace);
        CodeTryCatchFinallyStmt.CatchBlock catchBlock = new CodeTryCatchFinallyStmt.CatchBlock(fnf, catchCode);
        return catchBlock;
    }

    private ServiceDefinition addRuntimeBehaviorOption() {
        String desc = "Setting the behavior of the runtime library according to the compile-time option";

        CodeStmtCollection init = new CodeStmtCollection();
        {
            CodeType type = CodeHelper.RuntimeType.getRuntimeOption();
            CodeExpr enabled = CodeLiteralExpr.bool(Main.options.finegrainedlock);
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
