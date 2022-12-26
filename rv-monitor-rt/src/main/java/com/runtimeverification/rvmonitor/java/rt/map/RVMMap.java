package com.runtimeverification.rvmonitor.java.rt.map;

import com.runtimeverification.rvmonitor.java.rt.ref.RVMWeakReference;

public abstract class RVMMap extends RVMCleanable {

	abstract public long size();

//	abstract public Object get(RVMWeakReference key);
//	abstract public boolean put(RVMWeakReference keyref, Object value);

	abstract public Object getMap(RVMWeakReference key);
	abstract public boolean putMap(RVMWeakReference key, Object value);

	abstract public Object getSet(RVMWeakReference key);
	abstract public boolean putSet(RVMWeakReference key, Object value);

	abstract public Object getNode(RVMWeakReference key);
	abstract public boolean putNode(RVMWeakReference key, Object value);

}
