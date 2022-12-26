package com.runtimeverification.rvmonitor.java.rt.observable;

import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractPartitionedMonitorSet;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IMonitor;

public interface ISetBehaviorObserver<TSender extends AbstractPartitionedMonitorSet<?>> extends IObserver {
	public void onSetMonitorAdded(TSender set, IMonitor monitor);
	public void onSetMonitorInvalidated(TSender set, IMonitor monitor);
	public void onSetArranged(TSender set, int numInvalidated, int numSearchedSlots, int numSearchedNodes);
	public void onSetTransitioned(TSender set, int receiver, int donor);
	
	public static final boolean observeAdded = false;
	public static final boolean observeInvalidated = false;
	public static final boolean observeArranged = true;
	public static final boolean observeTransitioned = false;
}
