package com.runtimeverification.rvmonitor.java.rt.table;

import com.runtimeverification.rvmonitor.java.rt.ref.CachedMultiTagWeakReference;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IMonitor;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IMonitorSet;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IWeakRefTableOperation;

public class MultiTagRefMapOfSetMonitor<TSet extends IMonitorSet, TLeaf extends IMonitor> extends AbstractMapOfSetMonitor<CachedMultiTagWeakReference, TSet, TLeaf> implements IWeakRefTableOperation<CachedMultiTagWeakReference> {
	private final int taglen;
	
	public MultiTagRefMapOfSetMonitor(int treeid) {
		this(treeid, 1);
	}
	
	public MultiTagRefMapOfSetMonitor(int treeid, int taglen) {
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