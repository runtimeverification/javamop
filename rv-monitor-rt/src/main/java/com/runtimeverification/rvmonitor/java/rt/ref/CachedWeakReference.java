package com.runtimeverification.rvmonitor.java.rt.ref;

import java.lang.ref.WeakReference;

/**
 * This class extends Java's basic weak reference class by caching the hash value
 * returned by System.identityHashCode(). This class was introduced after Dongyun
 * realized that System.identityHashCode() is surprisingly expensive.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class CachedWeakReference extends WeakReference<Object> {
	private final int hashval;
	
	public CachedWeakReference(Object ref) {
		this(ref, System.identityHashCode(ref));
	}
	
	public CachedWeakReference(Object ref, int hashval) {
		super(ref);
		this.hashval = hashval;
	}
	
	@Override
	public int hashCode() {
		return this.hashval;
	}
	
	@Override
	public String toString() {
		Object ref = this.get();
		if (ref == null)
			return "nil";
		
		String r = ref.getClass().getSimpleName();
		r += "#";
		r += String.format("%03x", this.hashval & 0xFFF);
		return r;
	}
}