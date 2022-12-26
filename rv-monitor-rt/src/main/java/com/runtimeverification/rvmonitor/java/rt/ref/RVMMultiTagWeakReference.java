package com.runtimeverification.rvmonitor.java.rt.ref;

import java.lang.ref.ReferenceQueue;



public class RVMMultiTagWeakReference extends RVMWeakReference {
	
	public long[] disable;
	public long[] tau;

	public RVMMultiTagWeakReference(int taglen, Object r) {
		super(r);
		disable = new long[taglen];
		tau = new long[taglen];
	}

	public RVMMultiTagWeakReference(int taglen, Object r, int hash) {
		super(r, hash);
		disable = new long[taglen];
		tau = new long[taglen];
	}

	public RVMMultiTagWeakReference(int taglen, Object r, int hash, ReferenceQueue<Object> q) {
		super(r, hash, q);
		disable = new long[taglen];
		tau = new long[taglen];
	}

}
