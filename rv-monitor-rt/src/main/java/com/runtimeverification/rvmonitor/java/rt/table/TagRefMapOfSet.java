package com.runtimeverification.rvmonitor.java.rt.table;

import com.runtimeverification.rvmonitor.java.rt.ref.CachedTagWeakReference;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IMonitorSet;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IWeakRefTableOperation;

public class TagRefMapOfSet<TSet extends IMonitorSet> extends AbstractMapOfSet<CachedTagWeakReference, TSet> implements IWeakRefTableOperation<CachedTagWeakReference> {
	public TagRefMapOfSet(int treeid) {
		super(treeid);
	}

	@Override
	protected CachedTagWeakReference createWeakRef(Object key, int hashval) {
		return new CachedTagWeakReference(key, hashval);
	}
	
	@Override
	public CachedTagWeakReference findWeakRef(Object key) {
		return this.findOrCreateWeakRefInternal(key, false);
	}

	@Override
	public CachedTagWeakReference findOrCreateWeakRef(Object key) {
		return this.findOrCreateWeakRefInternal(key, true);
	}
}