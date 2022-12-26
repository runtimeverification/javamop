package com.runtimeverification.rvmonitor.java.rt.map;

import com.runtimeverification.rvmonitor.java.rt.RVMObject;

public abstract class RVMCleanable implements RVMObject {
	public boolean isDeleted = false;
	public boolean repeat = false;
	public RVMCleanable nextInQueue = null;
	public boolean isCleaning = false;

	/*
	 *  concurrent cleaner is disabled since it requires a serious revising.
	 */
	// protected static final boolean multicore = Runtime.getRuntime().availableProcessors() > 1;
	protected static final boolean multicore = false;

}
