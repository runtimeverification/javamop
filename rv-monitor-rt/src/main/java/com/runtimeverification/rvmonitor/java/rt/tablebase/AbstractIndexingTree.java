package com.runtimeverification.rvmonitor.java.rt.tablebase;

import com.runtimeverification.rvmonitor.java.rt.observable.IObservableObject;
import com.runtimeverification.rvmonitor.java.rt.ref.CachedWeakReference;

public abstract class AbstractIndexingTree<TWeakRef extends CachedWeakReference, TValue extends IIndexingTreeValue> extends WeakRefHashTable<TWeakRef, TValue> implements IIndexingTree, IObservableObject {
	private String observableDescription;
	
	@Override
	public int getTreeId() {
		return this.treeid;
	}
	
	protected AbstractIndexingTree(TupleTrait<TValue> valuetrait, int treeid) {
		super(treeid, valuetrait);
	}

	@Override
	public final void terminate(int treeid) {
		this.terminateValues(treeid);
	}
	
	/*
	@Override
	public final boolean checkTerminatedWhileCleaningUp() {
		return this.getNumElements() == 0;
	}
	*/
	
	/*
	@Override
	protected final int cleanUpInBucket(Bucket<TWeakRef, TValue> bucket) {
		int removed = bucket.cleanUpUnnecessaryMappings(true, this.treeid);
		return removed;
	}

	@Override
	protected void requestCleanUpInBucket(Bucket<TWeakRef, TValue> bucket) {
		bucket.requestCleanUp(true, this.treeid);
	}

	@Override
	protected final Bucket<TWeakRef, TValue> createBucket(int capacity) {
		return new Bucket<TWeakRef, TValue>(this.treeid, capacity);
	}
	*/
	
	public void setObservableObjectDescription(String desc) {
		this.observableDescription = desc;
	}
	
	@Override
	public String getObservableObjectDescription() {
		return this.observableDescription;
	}
}
