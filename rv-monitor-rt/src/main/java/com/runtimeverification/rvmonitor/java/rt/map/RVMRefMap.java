package com.runtimeverification.rvmonitor.java.rt.map;

import com.runtimeverification.rvmonitor.java.rt.ref.RVMMultiTagWeakReference;
import com.runtimeverification.rvmonitor.java.rt.ref.RVMTagWeakReference;
import com.runtimeverification.rvmonitor.java.rt.ref.RVMWeakReference;

public interface RVMRefMap {
	static final int ref_cleanup_piece = 16;
	static final int ref_locality_cache_size = 32;
	
	public RVMWeakReference getRef(Object key, int joinPointId);

	public RVMWeakReference getRefNonCreative(Object key, int joinPointId);

	public RVMTagWeakReference getTagRef(Object key, int joinPointId);

	public RVMTagWeakReference getTagRefNonCreative(Object key, int joinPointId);
	
	public RVMMultiTagWeakReference getMultiTagRef(Object key, int joinPointId);

	public RVMMultiTagWeakReference getMultiTagRefNonCreative(Object key, int joinPointId);


	public RVMWeakReference getRef(Object key);

	public RVMWeakReference getRefNonCreative(Object key);

	public RVMTagWeakReference getTagRef(Object key);

	public RVMTagWeakReference getTagRefNonCreative(Object key);
	
	public RVMMultiTagWeakReference getMultiTagRef(Object key);

	public RVMMultiTagWeakReference getMultiTagRefNonCreative(Object key);
	
}
