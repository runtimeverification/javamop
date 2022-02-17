package com.runtimeverification.rvmonitor.java.rt.ref;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import com.runtimeverification.rvmonitor.java.rt.RVMObject;

public class RVMWeakReference extends WeakReference<Object> implements RVMObject {
	public int hash = 0;
	
	public RVMWeakReference(Object r) {
		super(r);
		this.hash = System.identityHashCode(r);
	}

	public RVMWeakReference(Object r, int hash) {
		super(r);
		this.hash = hash;
	}

	public RVMWeakReference(Object r, int hash, ReferenceQueue<Object> q) {
		super(r, q);
		this.hash = hash;
	}

	public int hashCode() {
		return hash;
	}
}
