package com.runtimeverification.rvmonitor.java.rt.table;

import com.runtimeverification.rvmonitor.java.rt.ref.CachedWeakReference;
import com.runtimeverification.rvmonitor.java.rt.tablebase.ILeafOperation;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IMonitor;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IndexingTree1;

abstract class AbstractMapOfMonitor<TWeakRef extends CachedWeakReference, TLeaf extends IMonitor> extends IndexingTree1<TWeakRef, TLeaf> implements ILeafOperation<TWeakRef, TLeaf> {
	protected AbstractMapOfMonitor(int treeid) {
		super(treeid);
	}

	@Override
	public TLeaf getLeaf(TWeakRef key) {
		return this.get1(key);
	}

	@Override
	public void putLeaf(TWeakRef key, TLeaf value) {
		this.put1(key, value);
	}
}

public class MapOfMonitor<TLeaf extends IMonitor> extends AbstractMapOfMonitor<CachedWeakReference, TLeaf> {
	public MapOfMonitor(int treeid) {
		super(treeid);
	}

	@Override
	protected CachedWeakReference createWeakRef(Object key, int hashval) {
		assert false;
		return null;
	}
}
