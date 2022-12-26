package com.runtimeverification.rvmonitor.java.rt.table;

import com.runtimeverification.rvmonitor.java.rt.ref.CachedWeakReference;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IIndexingTree;
import com.runtimeverification.rvmonitor.java.rt.tablebase.ILeafOperation;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IMapOperation;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IMonitor;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IMonitorSet;
import com.runtimeverification.rvmonitor.java.rt.tablebase.ISetOperation;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IndexingTree3;

abstract class AbstractMapOfAll<TWeakRef extends CachedWeakReference, TMap extends IIndexingTree, TSet extends IMonitorSet, TLeaf extends IMonitor> extends IndexingTree3<TWeakRef, TMap, TSet, TLeaf> implements IMapOperation<TWeakRef, TMap>, ISetOperation<TWeakRef, TSet>, ILeafOperation<TWeakRef, TLeaf> {
	protected AbstractMapOfAll(int treeid) {
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

	@Override
	public TLeaf getLeaf(TWeakRef key) {
		return this.get3(key);
	}

	@Override
	public void putLeaf(TWeakRef key, TLeaf value) {
		this.put3(key, value);
	}
}

public class MapOfAll<TMap extends IIndexingTree, TSet extends IMonitorSet, TLeaf extends IMonitor> extends AbstractMapOfAll<CachedWeakReference, TMap, TSet, TLeaf> {
	public MapOfAll(int treeid) {
		super(treeid);
	}
	
	@Override
	protected CachedWeakReference createWeakRef(Object key, int hashval) {
		assert false;
		return null;
	}
}