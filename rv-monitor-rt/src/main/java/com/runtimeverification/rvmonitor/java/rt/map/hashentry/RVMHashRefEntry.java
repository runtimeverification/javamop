package com.runtimeverification.rvmonitor.java.rt.map.hashentry;

import com.runtimeverification.rvmonitor.java.rt.ref.RVMWeakReference;

public class RVMHashRefEntry {
	public RVMHashRefEntry next;
	public RVMWeakReference key;

	public RVMHashRefEntry(RVMHashRefEntry next, RVMWeakReference keyref) {
		this.next = next;
		this.key = keyref;
	}

}