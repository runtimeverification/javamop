package com.runtimeverification.rvmonitor.java.rt.tablebase;

/**
 * This interface implements what any monitor should implement. 
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public interface IMonitor extends IIndexingTreeValue {
	/**
	 * Queries whether this monitor has been terminated.
	 * @return true if terminated
	 */
	public boolean isTerminated();
	
	/**
	 * Returns the id of the most recently occurring event
	 * @return the event id
	 */
	public int getLastEvent();
	
	/**
	 * Returns the current state
	 * @return the state id
	 */
	public int getState();
}
