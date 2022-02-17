package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event;

import com.runtimeverification.rvmonitor.java.rvj.output.RVMVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.CombinedOutput;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event.advice.AdviceBody;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.EventDefinition;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.typepattern.TypePattern;
import com.runtimeverification.rvmonitor.util.RVMException;

public class EndObject {

    private final String endObjectVar;
    private final TypePattern endObjectType;

    private final AdviceBody eventBody;

    private final RVMVariable endObjectSupportType;

    public EndObject(RVMonitorSpec rvmSpec, EventDefinition event,
            CombinedOutput combinedOutput) throws RVMException {
        if (!event.isEndObject())
            throw new RVMException(
                    "EndObject should be defined only for endObject pointcut.");

        this.endObjectType = event.getEndObjectType();
        this.endObjectVar = event.getEndObjectVar();
        if (this.endObjectVar == null || this.endObjectVar.length() == 0)
            throw new RVMException(
                    "The variable for an endObject pointcut is not defined.");
        this.endObjectSupportType = new RVMVariable(endObjectType.toString()
                + "RVMFinalized");

        RVMParameter endParam = event.getRVMParametersOnSpec().getParam(
                event.getEndObjectVar());
        RVMParameters endParams = new RVMParameters();
        if (endParam != null)
            endParams.add(endParam);

        this.eventBody = AdviceBody.createAdviceBody(rvmSpec, event,
                combinedOutput);
    }

    public String printDecl() {
        String ret = "";

        ret += "public static abstract class " + endObjectSupportType + "{\n";
        ret += "protected void finalize() throws Throwable{\n";
        ret += "try {\n";
        ret += endObjectType + " " + endObjectVar + " = (" + endObjectType
                + ")this;\n";
        ret += eventBody;
        ret += "} finally {\n";
        ret += "super.finalize();\n";
        ret += "}\n";
        ret += "}\n"; // method
        ret += "}\n"; // abstract class
        ret += "\n";

        ret += "declare parents : " + endObjectType + " extends "
                + endObjectSupportType + ";\n";
        ret += "\n";

        ret += "after(" + endObjectType + " " + endObjectVar
                + ") : execution(void " + endObjectType
                + ".finalize()) && this(" + endObjectVar + "){\n";
        ret += eventBody;
        ret += "}\n";

        return ret;
    }

}
