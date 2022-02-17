package com.runtimeverification.rvmonitor.java.rt.table;

import com.runtimeverification.rvmonitor.java.rt.ref.CachedMultiTagWeakReference;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IIndexingTree;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IMonitorSet;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IWeakRefTableOperation;

public class MultiTagRefMapOfMapSet<TMap extends IIndexingTree, TSet extends IMonitorSet> extends AbstractMapOfMapSet<CachedMultiTagWeakReference, TMap, TSet> implements IWeakRefTableOperation<CachedMultiTagWeakReference> {
	private final int taglen;
	
	public MultiTagRefMapOfMapSet(int treeid) {
		this(treeid, 1);
	}
	
	public MultiTagRefMapOfMapSet(int treeid, int taglen) {
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