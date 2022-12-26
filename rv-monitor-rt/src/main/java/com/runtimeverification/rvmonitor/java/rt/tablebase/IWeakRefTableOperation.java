package com.runtimeverification.rvmonitor.java.rt.tablebase;

/**
 * This interface defines what a weak reference table (such as GWRT)
 * should implement.
 * 
 * @author Choonghwan Lee <clee83@illinois.edu>
 *
 * @param <TWeakRef> type of the weak reference
 */
public interface IWeakRefTableOperation<TWeakRef> {
	public TWeakRef findWeakRef(Object key);
	public TWeakRef findOrCreateWeakRef(Object key);
}
