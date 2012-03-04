package javamoprt;

import java.lang.ref.Reference;

public class MOPMultiMapOfMap extends MOPMap {
	protected static final int DEFAULT_CAPACITY = 16;
	protected static final float DEFAULT_LOAD_FACTOR = 0.75f;
	protected static final int MAXIMUM_CAPACITY = 1 << 30;
	protected static final float DEFAULT_REDUCE_FACTOR = 0.25f;
	protected static final float DEFAULT_REDUCE_THREASHOLD = 1 << 17;
	protected static final int DEFAULT_CLEANUP_THREASHOLD = 512;
	protected static final int DEFAULT_CLEANUP_FACTOR = 256;
	protected static final int DEFAULT_CLEANUP_PIECE = DEFAULT_CAPACITY / DEFAULT_CLEANUP_FACTOR;
	protected static final int DEFAULT_THREADED_CLEANUP_THREASHOLD = 1<<8;

	protected static final int cleanup_piece = 8;

	protected static final boolean multicore = Runtime.getRuntime().availableProcessors() > 1;
	//protected static final boolean multicore = false;

	protected long addedMappings;
	protected long deletedMappings;

	protected int datathreshold;
	// protected int datalowthreshold;

	protected MOPHashEntry[] data;
	protected MOPHashEntry[] newdata;

	protected int putIndex = -1;
	protected int cleanIndex = -1;

	protected Object lastValue = null;
	protected int lastsize;

	// protected int cleanupThreshold;
	
	MOPMultiMapSignature[] valuePattern;
	protected int valueSize;

	public MOPMultiMapOfMap(MOPMultiMapSignature[] signatures) {
		this.data = new MOPHashEntry[DEFAULT_CAPACITY];
		this.newdata = null;

		this.addedMappings = 0;
		this.deletedMappings = 0;
		
		this.datathreshold = (int) (DEFAULT_CAPACITY * DEFAULT_LOAD_FACTOR);
		// this.datalowthreshold = (int) (DEFAULT_CAPACITY *
		// DEFAULT_REDUCE_FACTOR);

		this.lastsize = 0;
		// this.cleanupThreshold = this.data.length / 5;
		
		this.valuePattern = signatures;
		this.valueSize = signatures.length;
	}

	final public long size() {
		return addedMappings - deletedMappings;
	}

	public MOPWeakReference cachedKey;

	final public Object get(Object key) {
		if (key == null) {
			return null;
		}

		MOPHashEntry[] data = this.data;

		int hashCode = System.identityHashCode(key);
		int index = hashIndex(hashCode, data.length);
		MOPHashEntry entry = data[index];

		while (entry != null) {
			if (entry.hashCode == hashCode && (key == entry.key.get())) {
				lastValue = entry.getValue();
				cachedKey = entry.key;

				return entry.getValue();
			}
			entry = entry.next;
		}
		return null;
	}
	
	public Object get(Object key, int pos){
		return null;
	}
	
	public Object[] getAll(Object key){
		return null;
	}
	
	public boolean put(MOPWeakReference keyref, Object value) {
		lastValue = value;

		if (multicore && data.length > DEFAULT_THREADED_CLEANUP_THREASHOLD) {
			MOPHashEntry[] data = this.data;

			int hashCode = keyref.hash;
			putIndex = hashIndex(hashCode, data.length);
			
			while (this.newdata != null) {
				putIndex = -1;
				while (this.newdata != null) {
					Thread.yield();
				}
				data = this.data;
				putIndex = hashIndex(hashCode, data.length);
			}

			while (cleanIndex == putIndex) {
				Thread.yield();
			}
			
			MOPHashEntry newentry = new MOPHashEntry(data[putIndex], hashCode, keyref, value);
			data[putIndex] = newentry;
			addedMappings++;

			putIndex = -1;

			if (!isCleaning && this.nextInQueue == null && addedMappings - deletedMappings >= data.length / 2
					&& addedMappings - deletedMappings - lastsize > data.length / 10) {
				this.isCleaning = true;
				if (MOPMapManager.treeQueueTail == this) {
					this.repeat = true;
				} else {
					MOPMapManager.treeQueueTail.nextInQueue = this;
					MOPMapManager.treeQueueTail = this;
				}
			}
		} else {
			int hashCode = keyref.hash;
			int index = hashIndex(hashCode, data.length);

			MOPHashEntry newentry = new MOPHashEntry(data[index], hashCode, keyref, value);
			data[index] = newentry;
			addedMappings++;

			//if (multicore)
			//	checkCapacityNoOneIter();
			//else
			checkCapacity();
		}

		return true;
	}
	
	public boolean put(MOPWeakReference keyref, Object value, int pos){
		return put(keyref, value);
	}


	/* ************************************************************************************ */

	public void endObject(MOPMultiMapSignature[] signatures) {
		if(signatures.length != valueSize){
			System.err.println("[javamoprt] endObject of MOPMultiMap error.");
			return;
		}
		
		this.isDeleted = true;

		for (int i = data.length - 1; i >= 0; i--) {
			MOPHashEntry entry = data[i];
			data[i] = null;
			while (entry != null) {
				MOPHashEntry next = entry.next;
				MOPMap map = (MOPMap)entry.getValue();
				map.endObject(signatures);
				entry.next = null;
				entry = next;
			}
		}

		this.deletedMappings = this.addedMappings;
	}
	
	int cleancursor = -1;

	protected void cleanuponeiter() {
		if (cleancursor < 0)
			cleancursor = data.length - 1;

		for (int i = 0; i < cleanup_piece && cleancursor >= 0; i++) {
			MOPHashEntry entry = data[cleancursor];
			MOPHashEntry previous = null;
			if (entry != null) {
				do {
					MOPHashEntry next = entry.next;
					MOPMap map = (MOPMap)entry.getValue();
					if (entry.key.get() == null) {
						if (previous == null) {
							data[cleancursor] = entry.next;
						} else {
							previous.next = entry.next;
						}
						
						map.endObject(valuePattern);
						
						entry.next = null;
						this.deletedMappings++;
					} else if (map != lastValue && map.size() == 0) {
						if (previous == null) {
							data[cleancursor] = entry.next;
						} else {
							previous.next = entry.next;
						}
						entry.next = null;
						this.deletedMappings++;
					} else {
						previous = entry;
					}
					entry = next;
				} while (entry != null);
				cleancursor--;
				return;
			}
			cleancursor--;
		}
	}

	protected void cleanupiter() {
		for (int i = data.length - 1; i >= 0; i--) {
			MOPHashEntry entry = data[i];
			MOPHashEntry previous = null;
			while (entry != null) {
				MOPHashEntry next = entry.next;
				MOPMap map = (MOPMap)entry.getValue();
				if (entry.key.get() == null) {
					if (previous == null) {
						data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}
					
					map.endObject(valuePattern);

					entry.next = null;
					this.deletedMappings++;
				} else if (map != lastValue && map.size() == 0) {
					if (previous == null) {
						data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}
					entry.next = null;
					this.deletedMappings++;
				} else {
					previous = entry;
				}
				entry = next;
			}
		}
	}

	final protected int hashIndex(int hashCode, int dataSize) {
		return hashCode & (dataSize - 1);
	}

	protected void checkCapacityNoOneIter() {
		if (addedMappings - deletedMappings >= datathreshold) {
			cleanupiter();
			if (addedMappings - deletedMappings >= data.length / 2) {
				int newCapacity = data.length * 2;
				if (newCapacity <= MAXIMUM_CAPACITY) {
					adjustCapacity(newCapacity);
				}
			}
		}
	}

	protected void checkCapacity() {
		if (addedMappings - deletedMappings >= datathreshold) {
			cleanupiter();
			if (addedMappings - deletedMappings >= data.length / 2) {
				int newCapacity = data.length * 2;
				if (newCapacity <= MAXIMUM_CAPACITY) {
					adjustCapacity(newCapacity);
				}
			}
		} else if (addedMappings - deletedMappings >= DEFAULT_CLEANUP_THREASHOLD && addedMappings - deletedMappings >= data.length / 8) {
			// } else {
			cleanuponeiter();
		}
	}

	protected void adjustCapacity(int newCapacity) {
		int oldCapacity = data.length;

		MOPHashEntry oldEntries[] = data;
		MOPHashEntry newEntries[] = new MOPHashEntry[newCapacity];

		for (int i = oldCapacity - 1; i >= 0; i--) {
			MOPHashEntry entry = oldEntries[i];
			if (entry != null) {
				oldEntries[i] = null;
				do {
					MOPHashEntry next = entry.next;
					int index = hashIndex(entry.hashCode, newCapacity);
					entry.next = newEntries[index];
					newEntries[index] = entry;
					entry = next;
				} while (entry != null);
			}
		}
		datathreshold = (int) (newCapacity * DEFAULT_LOAD_FACTOR);
		// datalowthreshold = (int) (newCapacity * DEFAULT_REDUCE_FACTOR);
		// cleanup_piece = (int) (newCapacity / DEFAULT_CLEANUP_FACTOR);
		data = newEntries;
	}

	/* ************************************************************************************ */

	static protected class MOPHashEntry {
		protected MOPHashEntry next;
		protected int hashCode;
		protected MOPWeakReference key;
		protected Object value;

		protected MOPHashEntry(MOPHashEntry next, int hashCode, MOPWeakReference keyref, Object value) {
			this.next = next;
			this.hashCode = hashCode;
			this.key = keyref;
			this.value = value;
		}

		final public Object getValue() {
			return value;
		}

		final public Object getKey() {
			return key.get();
		}

		final public Reference getKeyRef() {
			return key;
		}
	}

}
