package com.runtimeverification.rvmonitor.java.rt.tablebase;

/**
 * This interface defines what a level in an indexing tree should
 * implement when it holds a leaf.
 *
 * Two operations are defined: retrieving a leaf, and inserting a leaf.
 * 
 * @author Choonghwan Lee <clee83@illinois.edu>
 *
 * @param <TWeakRef> type of the key
 * @param <TLeaf> type of the leaf
 */
public interface ILeafOperation<TWeakRef, TLeaf> {
	public TLeaf getLeaf(TWeakRef key);
	public void putLeaf(TWeakRef key, TLeaf value);
}
