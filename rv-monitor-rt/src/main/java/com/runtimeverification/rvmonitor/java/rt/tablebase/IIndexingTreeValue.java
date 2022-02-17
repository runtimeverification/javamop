package com.runtimeverification.rvmonitor.java.rt.tablebase;

/**
 * This interface defines what anything that an indexing tree holds
 * as a value should implement.
 * 
 * A value can be one of the followings:
 * 1. monitor (IMonitor implementation)
 * 2. monitor set (IMonitorSet implementation)
 * 3. level of an indexing tree (IIndexingTree implementation)
 * 4. disable holder (DisableHolder subclass)
 * Or,
 * 5. tuple (Tuple2 or Tuple3)
 * 
 * Currently, there is only one requirement: defining what to do when
 * the value is terminated. The purpose of having this method, at this
 * moment, is to terminate monitors. A monitor instance should implement
 * terminate() according to the specification. Other types of values,
 * such as sets and maps, should propagate terminate() to their elements,
 * so that all the contained monitors can be eventually terminated.
 * 
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public interface IIndexingTreeValue {
	public void terminate(int treeid);
}
