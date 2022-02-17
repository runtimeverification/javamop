package com.runtimeverification.rvmonitor.java.rt.table;

import com.runtimeverification.rvmonitor.java.rt.ref.CachedMultiTagWeakReference;
import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractPureWeakRefTable;

public class MultiTagRefMap extends AbstractPureWeakRefTable<CachedMultiTagWeakReference> {
	private final int taglen;
	
	public MultiTagRefMap() {
		this(1);
	}

	public MultiTagRefMap(int taglen) {
		this.taglen = taglen;
	}

	@Override
	protected CachedMultiTagWeakReference createWeakRef(Object key, int hashval) {
		return new CachedMultiTagWeakReference(key, hashval, this.taglen);
	}

	@Override
	public CachedMultiTagWeakReference findWeakRef(Object key) {
		return this.findOrCreateWeakRefInternal(key, false);
	}

	@Override
	public CachedMultiTagWeakReference findOrCreateWeakRef(Object key) {
		return this.findOrCreateWeakRefInternal(key, true);
	}
}