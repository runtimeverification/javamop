package com.runtimeverification.rvmonitor.java.rt.map.hashentry;

import com.runtimeverification.rvmonitor.java.rt.ref.RVMWeakReference;

public class RVMHashEntry {
	public RVMHashEntry next;
	public RVMWeakReference key;
	public Object value;

	public RVMHashEntry(RVMHashEntry next, RVMWeakReference keyref) {
		this.next = next;
		this.key = keyref;
		this.value = null;
	}

	public RVMHashEntry(RVMHashEntry next, RVMWeakReference keyref, Object value) {
		this.next = next;
		this.key = keyref;
		this.value = value;
	}

}
