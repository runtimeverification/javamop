package com.runtimeverification.rvmonitor.java.rt.table;

import com.runtimeverification.rvmonitor.java.rt.ref.CachedWeakReference;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IMonitorSet;
import com.runtimeverification.rvmonitor.java.rt.tablebase.ISetOperation;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IndexingTree1;

abstract class AbstractMapOfSet<TWeakRef extends CachedWeakReference, TSet extends IMonitorSet> extends IndexingTree1<TWeakRef, TSet> implements ISetOperation<TWeakRef, TSet> {
	protected AbstractMapOfSet(int treeid) {
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
}

public class MapOfSet<TSet extends IMonitorSet> extends AbstractMapOfSet<CachedWeakReference, TSet> {
	public MapOfSet(int treeid) {
		super(treeid);
	}

	@Override
	protected CachedWeakReference createWeakRef(Object key, int hashval) {
		assert false;
		return null;
	}
}

