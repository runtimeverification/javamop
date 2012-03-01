package javamoprt;

import java.lang.ref.Reference;

import javamoprt.MOPMap.MOPHashEntry;

public class MOPMultiMap extends MOPCleanable implements MOPObject {
	protected static final int DEFAULT_CAPACITY = 16;
	protected static final float DEFAULT_LOAD_FACTOR = 0.75f;
	protected static final int MAXIMUM_CAPACITY = 1 << 30;
	protected static final float DEFAULT_REDUCE_FACTOR = 0.25f;
	protected static final float DEFAULT_REDUCE_THREASHOLD = 1 << 17;
	protected static final int DEFAULT_CLEANUP_THREASHOLD = 512;
	protected static final int DEFAULT_CLEANUP_FACTOR = 256;
	protected static final int DEFAULT_CLEANUP_PIECE = DEFAULT_CAPACITY / DEFAULT_CLEANUP_FACTOR;
	protected static final int DEFAULT_THREADED_CLEANUP_THREASHOLD = 1<<8;

	protected static final int cleanup_piece = 12;

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

	protected boolean isDeleted = false;
	protected MOPMultiMap nextInQueue = null;

	protected boolean isCleaning = false;
	protected boolean repeat = false;

	protected Object lastValue = null;
	protected int lastsize;

	// protected int cleanupThreshold;
	
	MOPMultiMapSignature[] valuePattern;
	protected int valueSize;

	public MOPMultiMap(MOPMultiMapSignature[] signatures) {
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

	/*
	 * To avoid a race condition, it keeps two numbers separately.
	 * Thus, the result might be incorrect sometimes.
	 * This method is only for statistics.
	 */
	final public long size() {
		return addedMappings - deletedMappings;
	}

	public MOPWeakReference cachedKey;

	final public Object[] getAll(Object key) {
		if (key == null) {
			return null;
		}

		MOPHashEntry[] data = this.data;

		int hashCode = System.identityHashCode(key);
		int index = hashIndex(hashCode, data.length);
		MOPHashEntry entry = data[index];

		while (entry != null) {
			if (entry.hashCode == hashCode && (key == entry.key.get())) {
				lastValue = null;
				cachedKey = entry.key;

				return entry.getValue();
			}
			entry = entry.next;
		}
		return null;
	}

	public Object get(Object key) {
		return null;
	}
	
	final public Object get(Object key, int pos) {
		if (key == null) {
			return null;
		}

		MOPHashEntry[] data = this.data;

		int hashCode = System.identityHashCode(key);
		int index = hashIndex(hashCode, data.length);
		MOPHashEntry entry = data[index];

		while (entry != null) {
			if (entry.hashCode == hashCode && (key == entry.key.get())) {
				lastValue = entry.getValue()[pos];
				cachedKey = entry.key;

				return entry.getValue()[pos];
			}
			entry = entry.next;
		}
		return null;
	}

	public boolean put(MOPWeakReference keyref, Object value, int pos) {
		lastValue = value;

		if (multicore && data.length > DEFAULT_THREADED_CLEANUP_THREASHOLD) {
			MOPHashEntry[] data = this.data;

			int hashCode = keyref.hash;
			putIndex = hashIndex(hashCode, data.length);
			
			MOPHashEntry entry = data[putIndex];
			
			while (entry != null) {
				if (entry.hashCode == hashCode && (keyref.get() == entry.key.get())) {
					entry.value[pos] = value;
					
					putIndex = -1;
					return true;
				}
				entry = entry.next;
			}

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
			
			MOPHashEntry newentry = new MOPHashEntry(data[putIndex], hashCode, keyref, valueSize);
			newentry.value[pos] = value;
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

			MOPHashEntry entry = data[index];
			
			while (entry != null) {
				if (entry.hashCode == hashCode && (keyref.get() == entry.key.get())) {
					entry.value[pos] = value;
					
					return true;
				}
				entry = entry.next;
			}
			
			MOPHashEntry newentry = new MOPHashEntry(data[index], hashCode, keyref, valueSize);
			newentry.value[pos] = value;
			data[index] = newentry;
			addedMappings++;

			//if (multicore)
			//	checkCapacityNoOneIter();
			//else
			checkCapacity();
		}

		return true;
	}

	/* ************************************************************************************ */

	protected void endObject(MOPMultiMapSignature[] signatures) {
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
				Object[] values = entry.getValue();
				
				for(int j = 0; j < valueSize; j++){
					if(valuePattern[j].type == MOPMultiMapSignature.MAP_OF_MONITOR){
						MOPMonitor monitor = (MOPMonitor) values[j];
						monitor.endObject(signatures[j].idnum);
					} else if(valuePattern[j].type == MOPMultiMapSignature.MAP_OF_SET){
						MOPSet set = (MOPSet) values[j];
						set.endObjectAndClean(signatures[j].idnum);
					}
				}
								
				entry.next = null;
				entry = next;
			}
		}

		this.deletedMappings = this.addedMappings;
	}
	
	
	//worked until here.

	int cleancursor = -1;

	protected void cleanuponeiter() {
		if (cleancursor < 0)
			cleancursor = data.length - 1;

		while (cleancursor >= 0) {
			MOPHashEntry entry = data[cleancursor];
			MOPHashEntry previous = null;
			if (entry != null) {
				do {
					MOPHashEntry next = entry.next;
					if (entry.key.get() == null) {
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
				if (entry.key.get() == null) {
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
		protected Object[] value;

		protected MOPHashEntry(MOPHashEntry next, int hashCode, MOPWeakReference keyref, int len) {
			this.next = next;
			this.hashCode = hashCode;
			this.key = keyref;
			this.value = new Object[len];
		}

		final public Object[] getValue() {
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
