package com.runtimeverification.rvmonitor.java.rt.table;

import com.runtimeverification.rvmonitor.java.rt.ref.CachedWeakReference;
import com.runtimeverification.rvmonitor.java.rt.tablebase.ILeafOperation;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IMonitor;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IMonitorSet;
import com.runtimeverification.rvmonitor.java.rt.tablebase.ISetOperation;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IndexingTree2;

abstract class AbstractMapOfSetMonitor<TWeakRef extends CachedWeakReference, TSet extends IMonitorSet, TLeaf extends IMonitor> extends IndexingTree2<TWeakRef, TSet, TLeaf> implements ISetOperation<TWeakRef, TSet>, ILeafOperation<TWeakRef, TLeaf> {
	protected AbstractMapOfSetMonitor(int treeid) {
		super(treeid);
	}
	
	@Override
	public TSet getSet(TWeakRef key) {
		return this.get1(key);
	}

	@Override
	public void putSet(TWeakRef key, TSet value) {
		this.put1(key, value);
	}

	@Override
	public TLeaf getLeaf(TWeakRef key) {
		return this.get2(key);
	}

	@Override
	public void putLeaf(TWeakRef key, TLeaf value) {
		this.put2(key, value);
	}
}

public class MapOfSetMonitor<TSet extends IMonitorSet, TLeaf extends IMonitor> extends AbstractMapOfSetMonitor<CachedWeakReference, TSet, TLeaf> {
	public MapOfSetMonitor(int treeid) {
		super(treeid);
	}
	
	@Override
	protected CachedWeakReference createWeakRef(Object key, int hashval) {
		assert false;
		return null;
	}
}