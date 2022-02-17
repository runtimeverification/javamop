package com.runtimeverification.rvmonitor.java.rt.tablebase;
import com.runtimeverification.rvmonitor.java.rt.ref.CachedWeakReference;
import com.runtimeverification.rvmonitor.java.rt.tablebase.TableAdopter.Tuple0;

/**
 * This class represents a weak reference table, more specifically a global
 * weak reference table (GWRT). Since this table needs to hold only weak
 * references, there is no notion of value in this table, although it inherits
 * WeakRefHashTable.
 * 
 * @author Choonghwan Lee <clee83@illinois.edu>
 *
 * @param <TWeakRef> type of the reference
 */
public abstract class AbstractPureWeakRefTable<TWeakRef extends CachedWeakReference> extends WeakRefHashTable<TWeakRef, Tuple0> implements IWeakRefTableOperation<TWeakRef> {
	protected AbstractPureWeakRefTable() {
		super(-1, null);
	}
}
