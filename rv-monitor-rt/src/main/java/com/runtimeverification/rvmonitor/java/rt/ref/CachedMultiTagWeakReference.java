package com.runtimeverification.rvmonitor.java.rt.ref;

/**
 * This class should be no longer used.
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class CachedMultiTagWeakReference extends CachedWeakReference {
  	private final long[] disabled;
	private final long[] tau;
	
	public long getDisabled(int index) {
		return this.disabled[index];
	}
	
	public void setDisabled(int index, long d) {
		this.disabled[index] = d;
	}
	
	public long getTau(int index) {
		return this.tau[index];
	}
	
	public void setTau(int index, long t) {
		this.tau[index] = t;
	}
	
	public CachedMultiTagWeakReference(Object ref, int taglen) {
		this(ref, System.identityHashCode(ref), taglen);
	}
	
	public CachedMultiTagWeakReference(Object ref, int hashval, int taglen) {
		super(ref, hashval);
		this.disabled = new long[taglen];
		this.tau = new long[taglen];
	}
}