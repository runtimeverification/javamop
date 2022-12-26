package com.runtimeverification.rvmonitor.java.rt.tablebase;

/**
 * This interface defines what a level in an indexing tree should
 * implement. This interface defines the way to access value, which is
 * more low-level and generic than IMapOperation, ISetOperation and
 * ILeafOperation.
 *
 * Two operations are defined: retrieving a node, and inserting a node.
 * More specifically, there are three ways to retrieve a node:
 * 1. getNode(): retrieves the associated value using a weak reference
 * 2. getNodeEquivalent(): retrieves the associated value using a weak reference
 * 3. getNodeWithStrongRef(): retrieves the associated value using a strong reference
 * 
 * The difference between 1. and 2. is that 1. assumes that GWRTs are used; i.e.,
 * there is one weak reference for each strong reference. Therefore, it checks
 * equivalence between two weak references using reference equality. In contrast,
 * 2. does not assume that; thus, to check equivalence, it compares referents.
 * 
 * @author Choonghwan Lee <clee83@illinois.edu>
 *
 * @param <TWeakRef> type of the key
 * @param <TValue> type of the value
 */
public interface INodeOperation<TWeakRef, TValue> {
	public TValue getNode(TWeakRef key);
	public TValue getNodeEquivalent(TWeakRef key);
	public TValue getNodeWithStrongRef(Object key);

	public void putNode(TWeakRef key, TValue value);
}
