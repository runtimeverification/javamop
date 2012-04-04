package javamoprt.map;

import javamoprt.ref.MOPWeakReference;

public abstract class MOPAbstractMap extends MOPMap {
	protected static final int DEFAULT_CAPACITY = 16;
	protected static final float DEFAULT_LOAD_FACTOR = 0.75f;
	protected static final int MAXIMUM_CAPACITY = 1 << 30;
	protected static final float DEFAULT_REDUCE_FACTOR = 0.25f;
	protected static final float DEFAULT_REDUCE_THREASHOLD = 1 << 17;
	protected static final int DEFAULT_CLEANUP_THREASHOLD = 512;
	protected static final int DEFAULT_CLEANUP_FACTOR = 256;
	protected static final int DEFAULT_CLEANUP_PIECE = DEFAULT_CAPACITY / DEFAULT_CLEANUP_FACTOR;
	protected static final int DEFAULT_THREADED_CLEANUP_THREASHOLD = 1 << 10;

	protected int cleanup_piece = 5;

	public int idnum;

	protected long addedMappings;
	protected long deletedMappings;

	protected int datathreshold;

	public int putIndex = -1;
	public int cleanIndex = -1;

	protected Object lastValue = null;
	protected int lastsize;
	
	public MOPAbstractMap() {
		this.addedMappings = 0;
		this.deletedMappings = 0;
		this.datathreshold = (int) (DEFAULT_CAPACITY * DEFAULT_LOAD_FACTOR);

		this.lastsize = 0;
	}

	final public long size() {
		return addedMappings - deletedMappings;
	}

	public Object getMap(MOPWeakReference key) {
		return null;
	}

	public Object getSet(MOPWeakReference key) {
		return null;
	}

	public Object getNode(MOPWeakReference key) {
		return null;
	}

	public boolean putMap(MOPWeakReference key, Object value) {
		return false;
	}

	public boolean putSet(MOPWeakReference key, Object value) {
		return false;
	}

	public boolean putNode(MOPWeakReference key, Object value) {
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
