package com.runtimeverification.rvmonitor.java.rt.table;

import com.runtimeverification.rvmonitor.java.rt.ref.CachedWeakReference;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IIndexingTree;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IMapOperation;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IMonitorSet;
import com.runtimeverification.rvmonitor.java.rt.tablebase.ISetOperation;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IndexingTree2;

abstract class AbstractMapOfMapSet<TWeakRef extends CachedWeakReference, TMap extends IIndexingTree, TSet extends IMonitorSet> extends IndexingTree2<TWeakRef, TMap, TSet> implements IMapOperation<TWeakRef, TMap>, ISetOperation<TWeakRef, TSet> {
	protected AbstractMapOfMapSet(int treeid) {
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

	@Override
	public TSet getSet(TWeakRef key) {
		return this.get2(key);
	}

	@Override
	public void putSet(TWeakRef key, TSet value) {
		this.put2(key, value);
	}
}

public class MapOfMapSet<TMap extends IIndexingTree, TSet extends IMonitorSet> extends AbstractMapOfMapSet<CachedWeakReference, TMap, TSet> {
	public MapOfMapSet(int treeid) {
		super(treeid);
	}
	
	@Override
	protected CachedWeakReference createWeakRef(Object key, int hashval) {
		assert false;
		return null;
	}
}
