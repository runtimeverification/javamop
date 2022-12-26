package com.runtimeverification.rvmonitor.java.rt.tablebase;

/**
 * This interface defines what a level in an indexing tree should
 * implement when it holds a map.
 *
 * Two operations are defined: retrieving a map, and inserting a map.
 * 
 * @author Choonghwan Lee <clee83@illinois.edu>
 *
 * @param <TWeakRef> type of the key
 * @param <TMap> type of the map
 */
public interface IMapOperation<TWeakRef, TMap> {
	public TMap getMap(TWeakRef key);
	public void putMap(TWeakRef key, TMap value);
}
