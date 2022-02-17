package com.runtimeverification.rvmonitor.java.rt.ref;

import java.lang.ref.ReferenceQueue;


public class RVMTagWeakReference extends RVMWeakReference {
	
  	public long disable = -1;
	public long tau = -1;
	
	public RVMTagWeakReference(Object r) {
		super(r);
	}

	public RVMTagWeakReference(Object r, int hash) {
		super(r, hash);
	}

	public RVMTagWeakReference(Object r, int hash, ReferenceQueue<Object> q) {
		super(r, hash, q);
	}

}
