package com.runtimeverification.rvmonitor.java.rt.map;

import com.runtimeverification.rvmonitor.java.rt.ref.RVMWeakReference;

public abstract class RVMAbstractMap extends RVMMap {
	protected static final int DEFAULT_CAPACITY = 16;
	protected static final float DEFAULT_LOAD_FACTOR = 0.75f;
	protected static final int MAXIMUM_CAPACITY = 1 << 30;
	protected static final float DEFAULT_REDUCE_FACTOR = 0.25f;
	protected static final float DEFAULT_REDUCE_THREASHOLD = 1 << 17;
	protected static final int DEFAULT_CLEANUP_THREASHOLD = 512;
	protected static final int DEFAULT_CLEANUP_FACTOR = 256;
	protected static final int DEFAULT_CLEANUP_PIECE = DEFAULT_CAPACITY / DEFAULT_CLEANUP_FACTOR;
	protected static final int DEFAULT_THREADED_CLEANUP_THREASHOLD = 1 << 10;

	protected static final int cleanup_piece = 5;

	public int idnum;

	public long addedMappings;
	public long deletedMappings;

	protected int datathreshold;

	public int putIndex = -1;
	public int cleanIndex = -1;

	protected Object lastValue = null;
	protected int lastsize;
	
	public RVMAbstractMap() {
		this.addedMappings = 0;
		this.deletedMappings = 0;
		this.datathreshold = (int) (DEFAULT_CAPACITY * DEFAULT_LOAD_FACTOR);

		this.lastsize = 0;
	}

	final public long size() {
		return addedMappings - deletedMappings;
	}

	public Object getMap(RVMWeakReference key) {
		return null;
	}

	public Object getSet(RVMWeakReference key) {
		return null;
	}

	public Object getNode(RVMWeakReference key) {
		return null;
	}

	public boolean putMap(RVMWeakReference key, Object value) {
		return false;
	}

	public boolean putSet(RVMWeakReference key, Object value) {
		return false;
	}

	public boolean putNode(RVMWeakReference key, Object value) {
		return false;
	}

	/* ************************************************************************************ */

	abstract protected void endObject(int idnum);

	int cleancursor = -1;

	abstract protected void cleanupchunkiter();

	abstract protected void cleanupiter();

	final protected int hashIndex(int hashCode, int dataSize) {
		return hashCode & (dataSize - 1);
	}

	abstract protected void checkCapacityNoOneIter();

	abstract protected void checkCapacity();

	abstract protected void adjustCapacity(int newCapacity);

}
