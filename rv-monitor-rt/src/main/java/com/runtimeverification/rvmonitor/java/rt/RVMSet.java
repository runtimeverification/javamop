package com.runtimeverification.rvmonitor.java.rt;

public abstract class RVMSet implements RVMObject {

	public int size = 0;
	
	abstract public int size();

	abstract public boolean add(RVMMonitor e);
	
	abstract public void endObject(int idnum);
	
	abstract public boolean alive();
	
	abstract public void endObjectAndClean(int idnum);
	
	abstract public void ensureCapacity();
	
}