package com.runtimeverification.rvmonitor.java.rt.tablebase;

import com.runtimeverification.rvmonitor.java.rt.ref.CachedWeakReference;
import com.runtimeverification.rvmonitor.java.rt.tablebase.TableAdopter.Tuple3;

/**
 * This abstract class is used when a level of an indexing tree keeps
 * three objects as a value. Since a level can hold a single object as a value,
 * a Tuple3<TValue1, TValue2, TValue3> instance is created to put three objects
 * together.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 *
 * @param <TWeakRef> type of the key
 * @param <TValue1> type of the first value
 * @param <TValue2> type of the second value
 * @param <TValue3> type of the third value
 */
public abstract class IndexingTree3<TWeakRef extends CachedWeakReference, TValue1 extends IIndexingTreeValue, TValue2 extends IIndexingTreeValue, TValue3 extends IIndexingTreeValue> extends AbstractIndexingTree<TWeakRef, Tuple3<TValue1, TValue2, TValue3>> {
	protected IndexingTree3(int treeid) {
		super(new Tuple3Trait<TValue1, TValue2, TValue3>(), treeid);
	}
	
	protected final TValue1 get1(TWeakRef key) {
		Tuple3<TValue1, TValue2, TValue3> tuple = this.getNode(key);
		if (tuple == null) return null;
		return tuple.getValue1();
	}
	
	protected final void put1(TWeakRef key, TValue1 value1) {
		Tuple3<TValue1, TValue2, TValue3> tuple = new Tuple3<TValue1, TValue2, TValue3>(value1, null, null);
		this.putNodeAdditive(key, tuple, 1);
	}
	
	protected final TValue2 get2(TWeakRef key) {
		Tuple3<TValue1, TValue2, TValue3> tuple = this.getNode(key);
		if (tuple == null) return null;
		return tuple.getValue2();
	}
	
	protected final void put2(TWeakRef key, TValue2 value2) {
		Tuple3<TValue1, TValue2, TValue3> tuple = new Tuple3<TValue1, TValue2, TValue3>(null, value2, null);
		this.putNodeAdditive(key, tuple, 2);
	}
	
	protected final TValue3 get3(TWeakRef key) {
		Tuple3<TValue1, TValue2, TValue3> tuple = this.getNode(key);
		if (tuple == null) return null;
		return tuple.getValue3();
	}
	
	protected final void put3(TWeakRef key, TValue3 value3) {
		Tuple3<TValue1, TValue2, TValue3> tuple = new Tuple3<TValue1, TValue2, TValue3>(null, null, value3);
		this.putNodeAdditive(key, tuple, 3);
	}
}