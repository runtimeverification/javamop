package com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec;

import java.util.ArrayList;
import java.util.List;

import com.runtimeverification.rvmonitor.java.rvj.parser.ast.Node;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.typepattern.TypePattern;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.visitor.GenericVisitor;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.visitor.VoidVisitor;

public class EventDefinition extends Node implements
Comparable<EventDefinition> {

    private final String id;

    private String purePointCutStr;

    private final RVMParameters parameters;

    final RVMParameters rvmParameters;

    private final String block;
    private RVMParameters usedParameter = null;

    // will be modified by RVMonitorSpec when creation events are not specified
    boolean startEvent = false;

    boolean blockingEvent = false;

    private String condition;
    private String threadVar;
    private ArrayList<String> threadBlockedVars;
    private TypePattern endObjectType;
    private String endObjectId;
    private boolean endProgram = false;
    private boolean endThread = false;
    private boolean startThread = false;
    private boolean endObject = false;
    String countCond;

    // things that should be defined afterward
    int idnum; // will be defined in RVMonitorSpec
    boolean duplicated = false; // will be defined in RVMonitorSpec
    String uniqueId = null; // will be defined in RVMonitorSpec
    RVMParameters rvmParametersOnSpec; // will be defined in RVMonitorSpec

    public EventDefinition(int beginLine, int beginColumn, String id,
            List<RVMParameter> rvmParameters, String block, boolean startEvent,
            boolean blockingEvent) {
        super(beginLine, beginColumn);
        this.id = id;
        this.parameters = new RVMParameters(rvmParameters);
        this.rvmParameters = new RVMParameters(rvmParameters);
        this.block = block;
        this.startEvent = startEvent;
        this.blockingEvent = blockingEvent;
    }

    public String getId() {
        return id;
    }

    public int getIdNum() {
        return idnum;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public RVMParameters getParameters() {
        return parameters;
    }

    RVMParameters parametersWithoutThreadVar = null;

    public RVMParameters getParametersWithoutThreadVar() {
        if (parametersWithoutThreadVar != null)
            return parametersWithoutThreadVar;

        parametersWithoutThreadVar = new RVMParameters();
        for (RVMParameter param : parameters) {
            if (getThreadVar() != null && getThreadVar().length() != 0
                    && param.getName().equals(getThreadVar()))
                continue;
            parametersWithoutThreadVar.add(param);
        }

        return parametersWithoutThreadVar;
    }

    public RVMParameters getRVMParameters() {
        return rvmParameters;
    }

    RVMParameters rvmParametersWithoutThreadVar = null;

    public RVMParameters getRVMParametersWithoutThreadVar() {
        if (rvmParametersWithoutThreadVar != null)
            return rvmParametersWithoutThreadVar;

        rvmParametersWithoutThreadVar = new RVMParameters();
        for (RVMParameter param : rvmParameters) {
            if (getThreadVar() != null && getThreadVar().length() != 0
                    && param.getName().equals(getThreadVar()))
                continue;
            rvmParametersWithoutThreadVar.add(param);
        }
        return rvmParametersWithoutThreadVar;
    }

    public RVMParameters getRVMParametersOnSpec() {
        return rvmParametersOnSpec;
    }

    public String getAction() {
        return block;
    }

    public RVMParameters getUsedParametersIn(RVMParameters specParam) {
        // All of them. If you want to have a property that doesn't use all the
        // parameters,
        // put it in another specification.
        return specParam;
    }

    public String getThreadVar() {
        return threadVar;
    }

    public ArrayList<String> getThreadBlockedVar() {
        return threadBlockedVars;
    }

    public String getCondition() {
        return condition;
    }

    public String getCountCond() {
        return countCond;
    }

    public String getPurePointCutString() {
        return purePointCutStr;
    }

    public String getEndObjectVar() {
        if (this.endObject)
            return endObjectId;
        else
            return null;
    }

    public TypePattern getEndObjectType() {
        if (this.endObject)
            return endObjectType;
        else
            return null;
    }

    public boolean isStartEvent() {
        return this.startEvent;
    }

    public boolean isBlockingEvent() {
        return this.blockingEvent;
    }

    public boolean isEndProgram() {
        return this.endProgram;
    }

    public boolean isEndThread() {
        return this.endThread;
    }

    public boolean isEndObject() {
        return this.endObject;
    }

    public boolean isStartThread() {
        return this.startThread;
    }

    private Boolean cachedHas__SKIP = null;

    public boolean has__SKIP() {
        if (cachedHas__SKIP != null)
            return cachedHas__SKIP.booleanValue();

        if (this.getAction() != null) {
            String eventAction = this.getAction().toString();
            if (eventAction.indexOf("__SKIP") != -1) {
                cachedHas__SKIP = new Boolean(true);
                return true;
            }
        }
        cachedHas__SKIP = new Boolean(false);
        return false;
    }

    private Boolean cachedHas__LOC = null;

    public boolean has__LOC() {
        if (cachedHas__LOC != null)
            return cachedHas__LOC.booleanValue();

        if (this.getAction() != null) {
            String eventAction = this.getAction().toString();
            if (eventAction.indexOf("__LOC") != -1
                    || eventAction.indexOf("__DEFAULT_MESSAGE") != -1) {
                cachedHas__LOC = new Boolean(true);
                return true;
            }
        }
        cachedHas__LOC = new Boolean(false);
        return false;
    }

    public RVMParameters getReferredParameters(RVMParameters projectedon) {
        return projectedon;
    }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }

    @Override
    public int compareTo(EventDefinition that) {
        return this.id.compareTo(that.id);
    }
}
