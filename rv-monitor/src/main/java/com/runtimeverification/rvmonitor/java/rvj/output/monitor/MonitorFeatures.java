package com.runtimeverification.rvmonitor.java.rvj.output.monitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.java.rvj.output.NotImplementedException;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event.itf.EventMethodBody;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;

/**
 * This class specifies what features a monitor class should implement. In
 * general, a monitor instance should remember 'tau', which was a
 * misunderstanding of 't' in the paper, and weak references that this instance
 * is about. Also, it needs to keep track of 'disable'. For some cases, however,
 * these are unnecessary and, for the performance benefit, it would be ideal to
 * get rid of them.
 *
 * This class is set by the EventMethodBody class, and consumed by monitor
 * generating classes, such as SuffixMonitor. For example, if EventMethodBody
 * enables the 'tau' feature, then the monitor generating class adds 'tau' field
 * to the generated code accordingly.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class MonitorFeatures {
    private final RVMParameters specParams;

    private boolean stabilized;

    public boolean isStabilized() {
        return this.stabilized;
    }

    /**
     * This field tells whether the monitor should be synchronized; i.e., each
     * operation should be protected by some locks. If this field is true,
     * methods in the generated monitor class would have the 'synchronized'
     * flag. At the time of writing this method, this field is true if
     * fine-grained locking is enabled and using an atomic field for the state
     * is infeasible.
     */
    private boolean needsSelfSynchronization;

    public boolean isSelfSynchronizationNeeded() {
        return this.needsSelfSynchronization;
    }

    public void setSelfSynchroniztionNeeded(boolean on) {
        if (!this.stabilized)
            throw new IllegalAccessError();
        this.needsSelfSynchronization = on;
    }

    /**
     * This field tells whether or not the time tracking feature is needed. If
     * it is enabled, 'tau' and 'disable' should be enabled when a monitor class
     * is generated.
     */
    private boolean needsTimeTracking;

    public boolean isTimeTrackingNeeded() {
        return this.needsTimeTracking;
    }

    public void setTimeTracking(boolean on) {
        this.needsTimeTracking = on;
    }

    /**
     * This field tells whether the generated monitor class should keep weak
     * references for all parameters and define them as non-final. If this field
     * is false, the following optimizations are applied: 1. Weak references for
     * only unnecessary parameters are stored in a monitor instance, which saves
     * memory consumption; and 2. The fields for holding weak references are
     * defined as 'final', which may cause the JIT compiler to generate better
     * code. If this field is true, the 'rememberedParameters' field should not
     * be used. It should be noted that this field gets a stable value only
     * after every EventMethodBody's generateCode() and this class'
     * onCodeGenerationPass1Completed() have been invoked. Therefore, one needs
     * to consider lazy code generation.
     */
    private boolean needsNonFinalWeakRefsInMonitor;

    public boolean isNonFinalWeakRefsInMonitorNeeded() {
        if (!this.stabilized)
            throw new IllegalAccessError();
        return this.needsNonFinalWeakRefsInMonitor;
    }

    private boolean needsFinalWeakRefsInMonitor;

    public boolean isFinalWeakRefsInMonitorNeeded() {
        if (!this.stabilized)
            throw new IllegalAccessError();
        return this.needsFinalWeakRefsInMonitor;
    }

    public void forceKeepWeakRefsInMonitor() {
        if (this.stabilized)
            throw new IllegalAccessError();
        this.needsFinalWeakRefsInMonitor = true;
    }

    /**
     * This field tells what parameters should be kept in each monitor instance.
     * For many cases, not all parameters need to be kept because some of them
     * are never borrowed to clone monitor instances. This field should not be
     * used if the 'needsNonFinalWeakRefsInMonitor' field is true; if that's the
     * case, the generated monitor should consider all the parameters.
     */
    private Set<RVMParameter> rememberedParameters;

    public RVMParameters getRememberedParameters() {
        if (!this.stabilized)
            throw new IllegalAccessError();

        RVMParameters params = new RVMParameters();
        for (RVMParameter prm : this.rememberedParameters)
            params.add(prm);

        return this.specParams.sortParam(params);
    }

    public void addRememberedParameters(RVMParameter param) {
        this.rememberedParameters.add(param);
    }

    /**
     * This field tells whether or not a place holder class for 'tau' and
     * 'disable' is needed. When it is enabled, an interface, which is to be
     * implemented by both the place holder and the generated monitor class, is
     * to be created as well. For example, consider the example in the ASE'09
     * paper. If [e1<p1>, e3<p1,q1>] is observed, a monitor corresponding to
     * <p1,q1> should be created. However, if [e1<p2>, e2<q2>, e3<p2,q2>] is
     * observed, a monitor corresponding to <p2,q2> should not be created, due
     * to the disable feature. In such case, a disable holder should be created
     * instead. That is, the type of the leaf of the indexing tree for
     * <P,Q>
     * should be able to hold both a monitor and a disable holder, and this is
     * where the interface is needed.
     */
    private boolean needsDisableHolder;

    public boolean isDisableHolderNeeded() {
        return this.needsDisableHolder;
    }

    public void setDisableHolder(boolean on) {
        this.needsDisableHolder = on;
    }

    private final List<EventMethodBody> relatedEvents;

    public MonitorFeatures(RVMParameters specParams) {
        this.specParams = specParams;

        this.stabilized = false;

        this.needsSelfSynchronization = Main.useFineGrainedLock;

        this.needsTimeTracking = true;
        this.rememberedParameters = new HashSet<RVMParameter>();

        this.needsDisableHolder = this.needsTimeTracking;

        this.relatedEvents = new ArrayList<EventMethodBody>();
    }

    public void addRelatedEvent(EventMethodBody evt) {
        this.relatedEvents.add(evt);
    }

    /**
     * This method is to be invoked after every EventMethodBody instance's
     * generateCode() has been invoked.
     */
    public void onCodeGenerationPass1Completed() {
        // Things are about to be stabilized.
        this.stabilized = true;

        boolean allcovered = true;
        RVMParameters needed = this.getRememberedParameters();
        for (EventMethodBody evt : this.relatedEvents) {
            RVMParameters prms = evt.getEventParameters();
            if (!prms.contains(needed)) {
                allcovered = false;
                break;
            }
        }

        if (!allcovered)
            this.needsNonFinalWeakRefsInMonitor = true;
    }

    public String getDisableHolderName(String monitorname) {
        int i = monitorname.lastIndexOf("Monitor");
        if (i == -1)
            throw new NotImplementedException();

        String name = monitorname.substring(0, i);
        name += "DisableHolder";
        return name;
    }

    public String getInterfaceName(String monitorname) {
        int i = monitorname.lastIndexOf("Monitor");
        if (i == -1)
            throw new NotImplementedException();

        return "I" + monitorname;
    }
}
