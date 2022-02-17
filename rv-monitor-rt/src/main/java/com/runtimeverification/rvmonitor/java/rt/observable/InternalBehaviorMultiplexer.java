package com.runtimeverification.rvmonitor.java.rt.observable;

import java.util.ArrayList;
import java.util.List;

import com.runtimeverification.rvmonitor.java.rt.ref.CachedWeakReference;
import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractIndexingTree;
import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractMonitor;
import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractMonitorSet;
import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractPartitionedMonitorSet;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IDisableHolder;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IIndexingTreeValue;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IMonitor;

public class InternalBehaviorMultiplexer implements IInternalBehaviorObserver, IObservable<IInternalBehaviorObserver> {
	private final List<IInternalBehaviorObserver> observers;
	
	public InternalBehaviorMultiplexer() {
		this.observers = new ArrayList<IInternalBehaviorObserver>();
	}
	
	@Override
	public void subscribe(IInternalBehaviorObserver observer) {
		this.observers.add(observer);
	}

	@Override
	public void unsubscribe(IInternalBehaviorObserver observer) {
		this.observers.remove(observer);
	}

	@Override
	public void onEventMethodEnter(String evtname, Object... args) {
		for (IInternalBehaviorObserver o : this.observers)
			o.onEventMethodEnter(evtname, args);
	}

	@Override
	public void onIndexingTreeCacheHit(String cachename, Object cachevalue) {
		for (IInternalBehaviorObserver o : this.observers)
			o.onIndexingTreeCacheHit(cachename, cachevalue);
	}

	@Override
	public void onIndexingTreeCacheMissed(String cachename) {
		for (IInternalBehaviorObserver o : this.observers)
			o.onIndexingTreeCacheMissed(cachename);
	}

	@Override
	public void onIndexingTreeCacheUpdated(String cachename, Object cachevalue) {
		for (IInternalBehaviorObserver o : this.observers)
			o.onIndexingTreeCacheUpdated(cachename, cachevalue);
	}

	@Override
	public <TWeakRef extends CachedWeakReference, TValue extends IIndexingTreeValue> void onIndexingTreeLookup(AbstractIndexingTree<TWeakRef, TValue> tree, LookupPurpose purpose, Object retrieved, Object ... keys) {
		for (IInternalBehaviorObserver o : this.observers)
			o.onIndexingTreeLookup(tree, purpose, retrieved, keys);
	}
	
	@Override
	public <TWeakRef extends CachedWeakReference, TValue extends IIndexingTreeValue> void onTimeCheck(AbstractIndexingTree<TWeakRef, TValue> tree, IDisableHolder source, IDisableHolder candidate, boolean definable, Object ... keys) {
		for (IInternalBehaviorObserver o : this.observers)
			o.onTimeCheck(tree, source, candidate, definable, keys);
	}

	@Override
	public <TWeakRef extends CachedWeakReference, TValue extends IIndexingTreeValue> void onIndexingTreeNodeInserted(AbstractIndexingTree<TWeakRef, TValue> tree, Object inserted, Object ... keys) {
		for (IInternalBehaviorObserver o : this.observers)
			o.onIndexingTreeNodeInserted(tree, inserted, keys);
	}

	@Override
	public void onNewMonitorCreated(AbstractMonitor created) {
		for (IInternalBehaviorObserver o : this.observers)
			o.onNewMonitorCreated(created);
	}

	@Override
	public void onMonitorCloned(AbstractMonitor existing, AbstractMonitor created) {
		for (IInternalBehaviorObserver o : this.observers)
			o.onMonitorCloned(existing, created);
	}

	@Override
	public void onDisableFieldUpdated(IDisableHolder affected) {
		for (IInternalBehaviorObserver o : this.observers)
			o.onDisableFieldUpdated(affected);
	}

	@Override
	public void onMonitorTransitioned(AbstractMonitor monitor) {
		for (IInternalBehaviorObserver o : this.observers)
			o.onMonitorTransitioned(monitor);
		
	}

	@Override
	public <TMonitor extends IMonitor> void onMonitorTransitioned(AbstractMonitorSet<TMonitor> set) {
		for (IInternalBehaviorObserver o : this.observers)
			o.onMonitorTransitioned(set);
	}

	@Override
	public <TMonitor extends IMonitor> void onMonitorTransitioned(AbstractPartitionedMonitorSet<TMonitor> set) {
		for (IInternalBehaviorObserver o : this.observers)
			o.onMonitorTransitioned(set);
	}

	@Override
	public void onEventMethodLeave() {
		for (IInternalBehaviorObserver o : this.observers)
			o.onEventMethodLeave();
	}

	@Override
	public void onCompleted() {
		for (IInternalBehaviorObserver o : this.observers)
			o.onCompleted();
	}
}
