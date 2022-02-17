package com.runtimeverification.rvmonitor.java.rt.table;

import com.runtimeverification.rvmonitor.java.rt.ref.CachedMultiTagWeakReference;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IIndexingTree;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IWeakRefTableOperation;

public class MultiTagRefMapOfMap<TMap extends IIndexingTree> extends AbstractMapOfMap<CachedMultiTagWeakReference, TMap> implements IWeakRefTableOperation<CachedMultiTagWeakReference> {
	private final int taglen;
	
	public MultiTagRefMapOfMap(int treeid) {
		this(treeid, 1);
	}
	
	public MultiTagRefMapOfMap(int treeid, int taglen) {
		super(treeid);
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
