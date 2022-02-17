package com.runtimeverification.rvmonitor.java.rt.table;

import com.runtimeverification.rvmonitor.java.rt.ref.CachedWeakReference;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IIndexingTree;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IMapOperation;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IndexingTree1;

abstract class AbstractMapOfMap<TWeakRef extends CachedWeakReference, TMap extends IIndexingTree> extends IndexingTree1<TWeakRef, TMap> implements IMapOperation<TWeakRef, TMap> {
	protected AbstractMapOfMap(int treeid) {
		super(treeid);
	}

	@Override
	public TMap getMap(TWeakRef key) {
		return this.get1(key);
	}

	@Override
	public void putMap(TWeakRef key, TMap value) {
		this.put1(key, value);
	}
}

public class MapOfMap<TMap extends IIndexingTree> extends AbstractMapOfMap<CachedWeakReference, TMap> {
	public MapOfMap(int treeid) {
		super(treeid);
	}

	@Override
	protected CachedWeakReference createWeakRef(Object key, int hashval) {
		assert false;
		return null;
	}
}
	