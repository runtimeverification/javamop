package com.runtimeverification.rvmonitor.java.rvj.output.monitor;

import java.util.HashMap;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMJavaCode;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.Util;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.PropertyAndHandlers;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;

public class HandlerMethod {
    private final PropertyAndHandlers prop;
    private final RVMVariable methodName;
    private final RVMJavaCode handlerCode;
    private final RVMParameters specParam;
    private final RVMVariable categoryVar;
    private final String category;
    private final Monitor monitor;

    private final RVMParameters varsToRestore;
    private final HashMap<RVMParameter, RVMVariable> savedParams;

    // local variables for now
    private final boolean has__SKIP;

    public HandlerMethod(PropertyAndHandlers prop, String category,
            RVMParameters specParam, RVMParameters commonParamInEvents,
            HashMap<RVMParameter, RVMVariable> savedParams, String handlerBody,
            RVMVariable categoryVar, Monitor monitor) {
        this.prop = prop;
        this.category = category;
        this.methodName = new RVMVariable("Prop_" + prop.getPropertyId()
                + "_handler_" + category);
        if (handlerBody != null) {
            has__SKIP = handlerBody.indexOf("__SKIP") != -1;

            handlerBody = handlerBody.replaceAll("__RESET", "this.reset()");
            handlerBody = handlerBody.replaceAll("__DEFAULT_MESSAGE",
                    monitor.getDefaultMessage());
            // __DEFAULT_MESSAGE may contain __LOC, make sure to sub in
            // __DEFAULT_MESSAGE first
            // -P
            handlerBody = handlerBody.replaceAll("__LOC", Util.defaultLocation);
            handlerBody = handlerBody.replaceAll("__SKIP",
                    BaseMonitor.skipEvent + " = true");

            this.handlerCode = new RVMJavaCode(handlerBody);
        } else {
            this.handlerCode = null;
            has__SKIP = false;
        }
        this.specParam = specParam;
        this.categoryVar = categoryVar;
        this.monitor = monitor;
        this.varsToRestore = new RVMParameters();
        this.savedParams = savedParams;

        for (RVMParameter p : prop.getUsedParametersIn(category, specParam)) {
            if (!commonParamInEvents.contains(p)) {
                this.varsToRestore.add(p);
            }
        }
    }

    public boolean has__SKIP() {
        return has__SKIP;
    }

    public RVMVariable getMethodName() {
        return methodName;
    }

    @Override
    public String toString() {
        // String synch = Main.useFineGrainedLock ? "synchronized " : "";
        String synch = "";
        String ret = "";

        ret += "final ";
        ret += synch;

        // if we want a handler to return some value, change it.
        ret += "void ";

        ret += methodName + " (";
        if (!Main.stripUnusedParameterInMonitor)
            ret += this.specParam.parameterDeclString();
        ret += "){\n";

        if (Main.statistics) {
            ret += "if(" + categoryVar + ") {\n";
            ret += monitor.stat.categoryInc(prop, category);
            ret += "}\n";
        }

        for (RVMParameter p : this.varsToRestore) {
            RVMVariable v = this.savedParams.get(p);

            ret += "if(" + p.getName() + " == null && " + v + " != null){\n";
            ret += p.getName() + " = (" + p.getType() + ")" + v + ".get();\n";
            ret += "}\n";
        }

        ret += handlerCode + "\n";

        ret += "}\n";

        return ret;
    }

}
