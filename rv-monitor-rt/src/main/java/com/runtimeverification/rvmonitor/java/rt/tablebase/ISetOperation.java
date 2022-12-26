package com.runtimeverification.rvmonitor.java.rt.tablebase;

/**
 * This interface defines what a level in an indexing tree should
 * implement when it holds a set.
 *
 * Two operations are defined: retrieving a set, and inserting a set.
 * 
 * @author Choonghwan Lee <clee83@illinois.edu>
 *
 * @param <TWeakRef> type of the key
 * @param <TSet> type of the set
 */
public interface ISetOperation<TWeakRef, TSet> {
	public TSet getSet(TWeakRef key);
	public void putSet(TWeakRef key, TSet value);
}
