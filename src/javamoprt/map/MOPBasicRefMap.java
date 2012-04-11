package javamoprt.map;

import javamoprt.map.hashentry.MOPHashRefEntry;
import javamoprt.ref.MOPMultiTagWeakReference;
import javamoprt.ref.MOPTagWeakReference;
import javamoprt.ref.MOPWeakReference;

public class MOPBasicRefMap extends MOPCleanable implements MOPRefMap {
	protected static final int DEFAULT_CAPACITY = 128;
	protected static final float DEFAULT_LOAD_FACTOR = 0.75f;
	protected static final int MAXIMUM_CAPACITY = 1 << 30;
	protected static final float DEFAULT_REDUCE_FACTOR = 0.25f;
	protected static final float DEFAULT_REDUCE_THREASHOLD = 1 << 17;
	protected static final int DEFAULT_CLEANUP_THREASHOLD = 512;
	protected static final int DEFAULT_CLEANUP_FACTOR = 256;
	protected static final int DEFAULT_CLEANUP_PIECE = DEFAULT_CAPACITY / DEFAULT_CLEANUP_FACTOR;
	protected static final int DEFAULT_THREADED_CLEANUP_THREASHOLD = 1<<8;

	protected long addedMappings;
	protected long deletedMappings;

	protected int datathreshold;

	protected MOPHashRefEntry[] data;
	protected MOPHashRefEntry[] newdata;

	protected int putIndex = -1;
	protected int cleanIndex = -1;

	protected int lastsize;

	static public MOPWeakReference NULRef = new MOPWeakReference(null);

	protected Object cachedKey = null;
	protected MOPWeakReference cachedValue = NULRef;
	
	protected Object[] cachedKey2 = new Object[ref_locality_cache_size];
	protected MOPWeakReference[] cachedValue2 = new MOPWeakReference[ref_locality_cache_size];

	public MOPBasicRefMap() {
		this(DEFAULT_CAPACITY);
	}

	public MOPBasicRefMap(int size) {
		if(size > 0)
			this.data = new MOPHashRefEntry[size];
		this.newdata = null;

		this.addedMappings = 0;
		this.deletedMappings = 0;
		
		this.datathreshold = (int) (DEFAULT_CAPACITY * DEFAULT_LOAD_FACTOR);

		this.lastsize = 0;
		
		for (int i = 0; i < ref_locality_cache_size; i++) {
			cachedKey2[i] = null;
			cachedValue2[i] = NULRef;
		}
	}

	final public long size() {
		return addedMappings - deletedMappings;
	}
	
	@Override
	public MOPWeakReference getRef(Object key, int joinPointId) {
		if (key == cachedKey && cachedValue != NULRef) {
			return cachedValue;
		}

		int cacheIndex = joinPointId & (ref_locality_cache_size - 1);

		if (key == cachedKey2[cacheIndex] && cachedValue2[cacheIndex] != NULRef) {
			cachedKey = cachedKey2[cacheIndex];
			cachedValue = cachedValue2[cacheIndex];
			return cachedValue;
		}

		cachedKey = key;
		cachedKey2[cacheIndex] = key;

		MOPHashRefEntry[] data = this.data;

		int hashCode = System.identityHashCode(key);
		int index = hashIndex(hashCode, data.length);
		MOPHashRefEntry entry = data[index];

		while (entry != null) {
			if (key == entry.key.get()) {
				cachedValue = entry.key;
				cachedValue2[cacheIndex] = entry.key;

				return cachedValue;
			}
			entry = entry.next;
		}

		// create new weakreference
		MOPWeakReference keyref = new MOPWeakReference(key, hashCode);
		cachedValue = keyref;

		for (int i = 0; i < ref_locality_cache_size; i++) {
			if (cachedKey2[i] == key)
				cachedValue2[i] = keyref;
		}

		if (multicore && data.length > DEFAULT_THREADED_CLEANUP_THREASHOLD) {
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

			MOPHashRefEntry newentry = new MOPHashRefEntry(data[putIndex], keyref);
			data[putIndex] = newentry;
			addedMappings++;

			putIndex = -1;

			if (!isCleaning && this.nextInQueue == null && addedMappings - deletedMappings >= data.length / 2 && addedMappings - deletedMappings - lastsize > data.length / 10) {
				this.isCleaning = true;
				if (MOPMapManager.treeQueueTail == this) {
					this.repeat = true;
				} else {
					MOPMapManager.treeQueueTail.nextInQueue = this;
					MOPMapManager.treeQueueTail = this;
				}
			}
		} else {
			MOPHashRefEntry newentry = new MOPHashRefEntry(data[index], keyref);
			data[index] = newentry;
			addedMappings++;

			if (multicore)
				checkCapacityNoOneIter();
			else
				checkCapacity();
		}

		return keyref;
	}

	@Override
	public MOPWeakReference getRefNonCreative(Object key, int joinPointId) {
		if (key == cachedKey) {
			return cachedValue;
		}

		int cacheIndex = joinPointId & (ref_locality_cache_size - 1);

		if (key == cachedKey2[cacheIndex]) {
			cachedKey = cachedKey2[cacheIndex];
			cachedValue = cachedValue2[cacheIndex];
			return cachedValue;
		}

		cachedKey = key;
		cachedKey2[cacheIndex] = key;

		MOPHashRefEntry[] data = this.data;

		int hashCode = System.identityHashCode(key);
		int index = hashIndex(hashCode, data.length);
		MOPHashRefEntry entry = data[index];

		while (entry != null) {
			if (key == entry.key.get()) {
				cachedValue = entry.key;
				cachedValue2[cacheIndex] = entry.key;

				return cachedValue;
			}
			entry = entry.next;
		}
		cachedValue = NULRef;
		cachedValue2[cacheIndex] = NULRef;

		return NULRef;
	}
	
	public MOPTagWeakReference getTagRef(Object key, int joinPointId){
		return null;
	}

	public MOPTagWeakReference getTagRefNonCreative(Object key, int joinPointId){
		return null;
	}
	
	public MOPMultiTagWeakReference getMultiTagRef(Object key, int joinPointId){
		return null;
	}

	public MOPMultiTagWeakReference getMultiTagRefNonCreative(Object key, int joinPointId){
		return null;
	}
	
	
	@Override
	public MOPWeakReference getRef(Object key) {
		if (key == cachedKey && cachedValue != NULRef) {
			return cachedValue;
		}

		cachedKey = key;

		MOPHashRefEntry[] data = this.data;

		int hashCode = System.identityHashCode(key);
		int index = hashIndex(hashCode, data.length);
		MOPHashRefEntry entry = data[index];

		while (entry != null) {
			if (key == entry.key.get()) {
				cachedValue = entry.key;

				return cachedValue;
			}
			entry = entry.next;
		}

		// create new weakreference
		MOPWeakReference keyref = new MOPWeakReference(key, hashCode);
		cachedValue = keyref;

		if (multicore && data.length > DEFAULT_THREADED_CLEANUP_THREASHOLD) {
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

			MOPHashRefEntry newentry = new MOPHashRefEntry(data[putIndex], keyref);
			data[putIndex] = newentry;
			addedMappings++;

			putIndex = -1;

			if (!isCleaning && this.nextInQueue == null && addedMappings - deletedMappings >= data.length / 2 && addedMappings - deletedMappings - lastsize > data.length / 10) {
				this.isCleaning = true;
				if (MOPMapManager.treeQueueTail == this) {
					this.repeat = true;
				} else {
					MOPMapManager.treeQueueTail.nextInQueue = this;
					MOPMapManager.treeQueueTail = this;
				}
			}
		} else {
			MOPHashRefEntry newentry = new MOPHashRefEntry(data[index], keyref);
			data[index] = newentry;
			addedMappings++;

			if (multicore)
				checkCapacityNoOneIter();
			else
				checkCapacity();
		}

		return keyref;
	}

	@Override
	public MOPWeakReference getRefNonCreative(Object key) {
		if (key == cachedKey) {
			return cachedValue;
		}

		cachedKey = key;

		MOPHashRefEntry[] data = this.data;

		int hashCode = System.identityHashCode(key);
		int index = hashIndex(hashCode, data.length);
		MOPHashRefEntry entry = data[index];

		while (entry != null) {
			if (key == entry.key.get()) {
				cachedValue = entry.key;

				return cachedValue;
			}
			entry = entry.next;
		}
		cachedValue = NULRef;

		return NULRef;
	}

	@Override
	public MOPTagWeakReference getTagRef(Object key){
		return null;
	}

	@Override
	public MOPTagWeakReference getTagRefNonCreative(Object key){
		return null;
	}
	
	@Override
	public MOPMultiTagWeakReference getMultiTagRef(Object key){
		return null;
	}
	
	@Override
	public MOPMultiTagWeakReference getMultiTagRefNonCreative(Object key){
		return null;
	}

	
	/* ************************************************************************************ */

	int cleancursor = -1;

	protected void cleanupchunkiter() {
		if (cleancursor < 0)
			cleancursor = data.length - 1;

		for(int i = 0; i < ref_cleanup_piece && cleancursor >= 0; i++){
			MOPHashRefEntry previous = null;
			MOPHashRefEntry entry = data[cleancursor];
			
			while (entry != null) {
				MOPHashRefEntry next = entry.next;
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
			}
			cleancursor--;
		}
	}

	protected void cleanupiter() {
		for (int i = data.length - 1; i >= 0; i--) {
			MOPHashRefEntry entry = data[i];
			MOPHashRefEntry previous = null;
			while (entry != null) {
				MOPHashRefEntry next = entry.next;
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
			cleanupchunkiter();
		}
	}

	protected void adjustCapacity(int newCapacity) {
		int oldCapacity = data.length;

		MOPHashRefEntry oldEntries[] = data;
		MOPHashRefEntry newEntries[] = new MOPHashRefEntry[newCapacity];

		for (int i = oldCapacity - 1; i >= 0; i--) {
			MOPHashRefEntry entry = oldEntries[i];
			if (entry != null) {
				oldEntries[i] = null;
				do {
					MOPHashRefEntry next = entry.next;
					int index = hashIndex(entry.key.hash, newCapacity);
					entry.next = newEntries[index];
					newEntries[index] = entry;
					entry = next;
				} while (entry != null);
			}
		}
		datathreshold = (int) (newCapacity * DEFAULT_LOAD_FACTOR);
		data = newEntries;
	}
}
