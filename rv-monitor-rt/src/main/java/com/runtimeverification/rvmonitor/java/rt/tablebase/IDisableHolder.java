package com.runtimeverification.rvmonitor.java.rt.tablebase;

/**
 * This interface defines methods that any disable holder should
 * implement. Since this is not fully-fledged monitor instance,
 * it does not hold any state; it only keeps 't' and 'disable'.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 * @see DisableHolder
 */
public interface IDisableHolder {
	public long getTau();

	public long getDisable();
	public void setDisable(long value);
}