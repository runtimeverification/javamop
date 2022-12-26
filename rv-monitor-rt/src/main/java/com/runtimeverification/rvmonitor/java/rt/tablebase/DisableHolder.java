package com.runtimeverification.rvmonitor.java.rt.tablebase;

/**
 * This class represents a disable holder. A disable holder is used
 * in place of a monitor instance when an event does not create a monitor
 * but the timestamp needs to be kept.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 * @see IDisableHolder
 */
public class DisableHolder implements IIndexingTreeValue, IDisableHolder {
	private long disable = -1;
	private final long tau;
	
	protected DisableHolder(long tau) {
		this.tau = tau;
	}

	@Override
	public long getTau() {
		return this.tau;
	}
	
	@Override
	public long getDisable() {
		return this.disable;
	}

	@Override
	public void setDisable(long value) {
		this.disable = value;
	}

	@Override
	public void terminate(int treeid) {
	}
}
