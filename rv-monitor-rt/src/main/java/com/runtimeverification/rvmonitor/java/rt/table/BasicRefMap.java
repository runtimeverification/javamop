package com.runtimeverification.rvmonitor.java.rt.table;

import com.runtimeverification.rvmonitor.java.rt.ref.CachedWeakReference;
import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractPureWeakRefTable;

public class BasicRefMap extends AbstractPureWeakRefTable<CachedWeakReference> {
	@Override
	protected CachedWeakReference createWeakRef(Object key, int hashval) {
		return new CachedWeakReference(key, hashval);
	}
	
	@Override
	public CachedWeakReference findWeakRef(Object key) {
		return this.findOrCreateWeakRefInternal(key, false);
	}

	@Override
	public CachedWeakReference findOrCreateWeakRef(Object key) {
		return this.findOrCreateWeakRefInternal(key, true);
	}
}