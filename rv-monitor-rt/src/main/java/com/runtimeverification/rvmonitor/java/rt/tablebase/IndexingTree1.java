package com.runtimeverification.rvmonitor.java.rt.tablebase;

import com.runtimeverification.rvmonitor.java.rt.ref.CachedWeakReference;

/**
 * This abstract class is used when a level of an indexing tree keeps
 * a single object as a value. The value is kept as it is, without using
 * any tuple.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 *
 * @param <TWeakRef> type of the key
 * @param <TValue> type of the value
 */
public abstract class IndexingTree1<TWeakRef extends CachedWeakReference, TValue extends IIndexingTreeValue> extends AbstractIndexingTree<TWeakRef, TValue> {
	protected IndexingTree1(int treeid) {
		super(null, treeid);
	}
	
	protected final TValue get1(TWeakRef key) {
		return this.getNode(key);
	}
	
	protected final void put1(TWeakRef key, TValue value) {
		this.putNodeUnconditional(key, value);
	}
}