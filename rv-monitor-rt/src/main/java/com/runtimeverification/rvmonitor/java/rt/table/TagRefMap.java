package com.runtimeverification.rvmonitor.java.rt.table;

import com.runtimeverification.rvmonitor.java.rt.ref.CachedTagWeakReference;
import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractPureWeakRefTable;

public class TagRefMap extends AbstractPureWeakRefTable<CachedTagWeakReference> {
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