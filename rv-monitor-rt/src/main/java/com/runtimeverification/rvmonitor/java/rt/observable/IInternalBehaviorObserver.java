package com.runtimeverification.rvmonitor.java.rt.observable;

import com.runtimeverification.rvmonitor.java.rt.ref.CachedWeakReference;
import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractIndexingTree;
import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractMonitor;
import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractMonitorSet;
import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractPartitionedMonitorSet;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IDisableHolder;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IIndexingTreeValue;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IMonitor;

public interface IInternalBehaviorObserver extends IObserver {
	/**
	 * Fired when an event handling method has entered. This method is invoked
	 * even if the event is suppressed due to various reasons, such as the activator
	 * suppresses it.
	 * @param evtname the event name
	 * @param args the parameters that this event carries
	 */
	public void onEventMethodEnter(String evtname, Object ... args);
	
	/**
	 * Fired when the indexing cache has hit.
	 * @param cachename the name of the field for holding the cached value
	 * @param cachevalue the value retrieved from the cache
	 */
	public void onIndexingTreeCacheHit(String cachename, Object cachevalue);

	/**
	 * Fired when the indexing cache has missed.
	 * @param cachename the name of the field for holding the cached value
	 */
	public void onIndexingTreeCacheMissed(String cachename);
	
	/**
	 * Fired when the indexing cache has been updated.
	 * @param cachename the name of the field for holding the cached value
	 * @param cachevalue the value that is newly assigned
	 */
	public void onIndexingTreeCacheUpdated(String cachename, Object cachevalue);

	public enum LookupPurpose {
		TransitionedMonitor,
		ClonedMonitor,
		CombinedMonitor,
	}
	
	/**
	 * Fired when an indexing tree has been queried.
	 * @param tree
	 * @param purpose
	 * @param retrieved 
	 * @param keys
	 */
	public <TWeakRef extends CachedWeakReference, TValue extends IIndexingTreeValue> void onIndexingTreeLookup(AbstractIndexingTree<TWeakRef, TValue> tree, LookupPurpose purpose, Object retrieved, Object ... keys);
	
	/*
	 * Fired when a node has been inserted into an indexing tree.
	 * @param tree
	 * @param source theta' on line 2 in 'defineTo' of D(X)
	 * @param candidate theta'' on line 2 in 'defineTo' of D(X)
	 * @param definable the current value of 'definable'
	 * @param keys
	 */
	public <TWeakRef extends CachedWeakReference, TValue extends IIndexingTreeValue> void onTimeCheck(AbstractIndexingTree<TWeakRef, TValue> tree, IDisableHolder source, IDisableHolder candidate, boolean definable, Object ... keys);

	/*
	 * Fired when a node has been inserted into an indexing tree.
	 * @param tree
	 * @param inserted
	 * @param keys
	 */
	public <TWeakRef extends CachedWeakReference, TValue extends IIndexingTreeValue> void onIndexingTreeNodeInserted(AbstractIndexingTree<TWeakRef, TValue> tree, Object inserted, Object ... keys);
	
	/**
	 * Fired when a new monitor has been created through the 'defineNew' routine.
	 * @param created
	 */
	public void onNewMonitorCreated(AbstractMonitor created);

	/**
	 * Fired when a new monitor has been created, more precisely cloned, through the 'defineTo' routine.
	 * @param existing
	 * @param created
	 */
	public void onMonitorCloned(AbstractMonitor existing, AbstractMonitor created);
	
	/**
	 * Fired when the 'disable' field of a monitor or disable holder has been updated.
	 * @param affected
	 */
	public void onDisableFieldUpdated(IDisableHolder affected);
	
	/**
	 * Fired when a single monitor has transitioned.
	 * @param monitor
	 */
	public void onMonitorTransitioned(AbstractMonitor monitor);
	
	/**
	 * Fired when a set of monitors have transitioned.
	 * @param set
	 */
	public <TMonitor extends IMonitor> void onMonitorTransitioned(AbstractMonitorSet<TMonitor> set);

	/**
	 * Fired when a set of monitors have transitioned.
	 * @param set
	 */
	public <TMonitor extends IMonitor> void onMonitorTransitioned(AbstractPartitionedMonitorSet<TMonitor> set);
	
	/**
	 * Fired when an event handling method is about to leave. This method is invoked
	 * even if the event is suppressed.
	 */
	public void onEventMethodLeave();
	
	/**
	 * Fired when all the events have been handled and the monitoring system is about to
	 * shut down. 
	 */
	public void onCompleted();
}
