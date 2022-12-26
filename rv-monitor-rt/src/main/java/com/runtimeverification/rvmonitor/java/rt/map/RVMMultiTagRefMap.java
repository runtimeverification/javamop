package com.runtimeverification.rvmonitor.java.rt.map;

import com.runtimeverification.rvmonitor.java.rt.map.hashentry.RVMHashRefEntry;
import com.runtimeverification.rvmonitor.java.rt.ref.RVMMultiTagWeakReference;

public class RVMMultiTagRefMap extends RVMBasicRefMap {
	int taglen = 1;
	
	static public RVMMultiTagWeakReference NULRef = new RVMMultiTagWeakReference(1, null);

	protected RVMMultiTagWeakReference cachedValue = NULRef;

	protected RVMMultiTagWeakReference[] cachedValue2 = new RVMMultiTagWeakReference[ref_locality_cache_size];

	public RVMMultiTagRefMap() {
		super();
		
		for (int i = 0; i < ref_locality_cache_size; i++) {
			cachedValue2[i] = NULRef;
		}
	}

	public RVMMultiTagRefMap(int taglen) {
		super();
		
		for (int i = 0; i < ref_locality_cache_size; i++) {
			cachedValue2[i] = NULRef;
		}

		this.taglen = taglen;
	}
	
	@Override
	public RVMMultiTagWeakReference getMultiTagRef(Object key, int joinPointId) {
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

		RVMHashRefEntry[] data = this.data;

		int hashCode = System.identityHashCode(key);
		int index = hashIndex(hashCode, data.length);
		RVMHashRefEntry entry = data[index];

		while (entry != null) {
			if (key == entry.key.get()) {
				cachedValue = (RVMMultiTagWeakReference) entry.key;
				cachedValue2[cacheIndex] = (RVMMultiTagWeakReference) entry.key;

				return cachedValue;
			}
			entry = entry.next;
		}

		// create new weakreference
		RVMMultiTagWeakReference keyref = new RVMMultiTagWeakReference(taglen, key, hashCode);
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

			RVMHashRefEntry newentry = new RVMHashRefEntry(data[putIndex], keyref);
			data[putIndex] = newentry;
			addedMappings++;

			putIndex = -1;

			if (!isCleaning && this.nextInQueue == null && addedMappings - deletedMappings >= data.length / 2 && addedMappings - deletedMappings - lastsize > data.length / 10) {
				this.isCleaning = true;
				if (RVMMapManager.treeQueueTail == this) {
					this.repeat = true;
				} else {
					RVMMapManager.treeQueueTail.nextInQueue = this;
					RVMMapManager.treeQueueTail = this;
				}
			}
		} else {
			RVMHashRefEntry newentry = new RVMHashRefEntry(data[index], keyref);
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
	public RVMMultiTagWeakReference getMultiTagRefNonCreative(Object key, int joinPointId) {
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

		RVMHashRefEntry[] data = this.data;

		int hashCode = System.identityHashCode(key);
		int index = hashIndex(hashCode, data.length);
		RVMHashRefEntry entry = data[index];

		while (entry != null) {
			if (key == entry.key.get()) {
				cachedValue = (RVMMultiTagWeakReference) entry.key;
				cachedValue2[cacheIndex] = (RVMMultiTagWeakReference) entry.key;

				return cachedValue;
			}
			entry = entry.next;
		}
		cachedValue = NULRef;
		cachedValue2[cacheIndex] = NULRef;

		return NULRef;
	}

	@Override
	public RVMMultiTagWeakReference getMultiTagRef(Object key) {
		if (key == cachedKey && cachedValue != NULRef) {
			return cachedValue;
		}

		cachedKey = key;

		RVMHashRefEntry[] data = this.data;

		int hashCode = System.identityHashCode(key);
		int index = hashIndex(hashCode, data.length);
		RVMHashRefEntry entry = data[index];

		while (entry != null) {
			if (key == entry.key.get()) {
				cachedValue = (RVMMultiTagWeakReference) entry.key;

				return cachedValue;
			}
			entry = entry.next;
		}

		// create new weakreference
		RVMMultiTagWeakReference keyref = new RVMMultiTagWeakReference(taglen, key, hashCode);
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

			RVMHashRefEntry newentry = new RVMHashRefEntry(data[putIndex], keyref);
			data[putIndex] = newentry;
			addedMappings++;

			putIndex = -1;

			if (!isCleaning && this.nextInQueue == null && addedMappings - deletedMappings >= data.length / 2 && addedMappings - deletedMappings - lastsize > data.length / 10) {
				this.isCleaning = true;
				if (RVMMapManager.treeQueueTail == this) {
					this.repeat = true;
				} else {
					RVMMapManager.treeQueueTail.nextInQueue = this;
					RVMMapManager.treeQueueTail = this;
				}
			}
		} else {
			RVMHashRefEntry newentry = new RVMHashRefEntry(data[index], keyref);
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
	public RVMMultiTagWeakReference getMultiTagRefNonCreative(Object key) {
		if (key == cachedKey) {
			return cachedValue;
		}

		cachedKey = key;

		RVMHashRefEntry[] data = this.data;

		int hashCode = System.identityHashCode(key);
		int index = hashIndex(hashCode, data.length);
		RVMHashRefEntry entry = data[index];

		while (entry != null) {
			if (key == entry.key.get()) {
				cachedValue = (RVMMultiTagWeakReference) entry.key;

				return cachedValue;
			}
			entry = entry.next;
		}
		cachedValue = NULRef;

		return NULRef;
	}
}
