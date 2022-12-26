package com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec;

import java.util.List;

import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.typepattern.TypePattern;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.ExtNode;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.visitor.GenericVisitor;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.visitor.VoidVisitor;

public class EventDefinitionExt extends ExtNode {

    private final String id;

    private String purePointCutStr;

    private final RVMParameters parameters;

    final RVMParameters rvmParameters;

    private final String block;

    // will be modified by RVMonitorSpec when creation events are not specified
    boolean startEvent = false;
    boolean blockingEvent = false;

    private String condition;
    private String threadVar;
    private TypePattern endObjectType;
    private String endObjectId;
    private boolean endProgram = false;
    private boolean endThread = false;
    private boolean endObject = false;

    // things that should be defined afterward
    int idnum; // will be defined in RVMonitorSpec
    boolean duplicated = false; // will be defined in RVMonitorSpec
    String uniqueId = null; // will be defined in RVMonitorSpec
    RVMParameters rvmParametersOnSpec; // will be defined in RVMonitorSpec

    private RVMParameters parametersWithoutThreadVar = null;
    private Boolean cachedHas__SKIP = null;
    private Boolean cachedHas__LOC = null;

    /*
     * EventDefinitionExt Event() { String name; List parameters; BlockStmt
     * block = null; int line = -1; int column = 0; boolean startEvent = false;
     * } { { return new EventDefinitionExt(line, column, name, parameters,
     * block, startEvent); } }
     */
    /**
     *
     * A new constructor for blocking event
     *
     * */
    public EventDefinitionExt(int line, int column, String id,
            List<RVMParameter> parameters, String block, boolean startEvent,
            boolean isBlockingEvent)
                    throws com.runtimeverification.rvmonitor.java.rvj.parser.main_parser.ParseException {
        super(line, column);
        this.id = id;
        this.parameters = new RVMParameters(parameters);
        this.block = block;
        this.startEvent = startEvent;
        this.rvmParameters = new RVMParameters();
        this.rvmParameters.addAll(this.parameters);
        this.blockingEvent = isBlockingEvent;
    }

    public EventDefinitionExt(int line, int column, EventDefinitionExt e) {
        super(line, column);
        this.id = e.getId();
        this.parameters = e.getParameters();
        this.block = e.getBlock();
        this.startEvent = e.getStartEvent();
        this.blockingEvent = e.isBlockingEvent();
        this.rvmParameters = e.getRVMParameters();
        this.condition = e.getCondition();
        this.threadVar = e.getThreadVar();
        this.endObjectType = e.getEndObjectType();
        this.endObjectId = e.getEndObjectId();

        this.endProgram = e.isEndProgram();
        this.endThread = e.isEndThread();
        this.endObject = e.isEndObject();

        this.idnum = e.getIdNum(); // will be defined in RVMonitorSpec
        this.duplicated = e.isDuplicated(); // will be defined in RVMonitorSpec
        this.uniqueId = e.getUniqueId(); // will be defined in RVMonitorSpec
        this.rvmParametersOnSpec = e.getRVMParametersOnSpec(); // will be
        // defined in
        // RVMonitorSpec

        this.parametersWithoutThreadVar = e.getParametersWithoutThreadVar();
        this.cachedHas__SKIP = e.isCashedHas__SKIP();
        this.cachedHas__LOC = e.isCachedHas__LOC();

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

    public String getThreadVar() {
        return threadVar;
    }

    public String getCondition() {
        return condition;
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

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }

    public String getBlock() {
        return this.block;
    }

    public boolean getStartEvent() {
        return this.startEvent;
    }

    public String getEndObjectId() {
        return this.endObjectId;
    }

    public boolean isDuplicated() {
        return this.duplicated;
    }

    public Boolean isCashedHas__SKIP() {
        return this.cachedHas__SKIP;
    }

    public Boolean isCachedHas__LOC() {
        return this.cachedHas__LOC;
    }

    public boolean isImplementing(EventDefinitionExt absEvent) {
        if (!this.getId().equals(absEvent.getId()))
            return false;
        if (this.getParameters().matchTypes(absEvent.getParameters()))
            return false;

        return true;
    }
}
