package javamoprt.map;

import javamoprt.map.hashentry.MOPHashEntry;
import javamoprt.ref.MOPTagWeakReference;
import javamoprt.ref.MOPWeakReference;

public class MOPTagRefMapOfMonitor extends MOPBasicRefMapOfMonitor {
	static public MOPTagWeakReference NULRef = new MOPTagWeakReference(null);

	protected MOPTagWeakReference cachedValue = NULRef;
	protected MOPTagWeakReference[] cachedValue2 = new MOPTagWeakReference[ref_locality_cache_size];

	public MOPTagRefMapOfMonitor(int idnum) {
		super(idnum);

		for (int i = 0; i < ref_locality_cache_size; i++) {
			cachedValue2[i] = NULRef;
		}
	}

	@Override
	public MOPTagWeakReference getTagRef(Object key, int joinPointId) {
		if (key == cachedKey && cachedValue != NULRef)
			return cachedValue;

		int cacheIndex = joinPointId & (ref_locality_cache_size - 1);

		if (key == cachedKey2[cacheIndex] && cachedValue2[cacheIndex] != NULRef) {
			cachedKey = cachedKey2[cacheIndex];
			cachedValue = cachedValue2[cacheIndex];
			return cachedValue;
		}

		cachedKey = key;
		cachedKey2[cacheIndex] = key;

		MOPHashEntry[] data = this.data;

		int hashCode = System.identityHashCode(key);
		int index = hashIndex(hashCode, data.length);
		MOPHashEntry entry = data[index];

		while (entry != null) {
			if (key == entry.key.get()) {
				cachedValue = (MOPTagWeakReference) entry.key;
				cachedValue2[cacheIndex] = (MOPTagWeakReference) entry.key;

				return cachedValue;
			}
			entry = entry.next;
		}

		// create new weakreference
		MOPTagWeakReference keyref = new MOPTagWeakReference(key, hashCode);
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

			MOPHashEntry newentry = new MOPHashEntry(data[putIndex], hashCode, keyref);
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
			MOPHashEntry newentry = new MOPHashEntry(data[index], hashCode, keyref);
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
	public MOPTagWeakReference getTagRefNonCreative(Object key, int joinPointId) {
		if (key == cachedKey)
			return cachedValue;

		int cacheIndex = joinPointId & (ref_locality_cache_size - 1);

		if (key == cachedKey2[cacheIndex]) {
			cachedKey = cachedKey2[cacheIndex];
			cachedValue = cachedValue2[cacheIndex];
			return cachedValue;
		}

		cachedKey = key;
		cachedKey2[cacheIndex] = key;

		MOPHashEntry[] data = this.data;

		int hashCode = System.identityHashCode(key);
		int index = hashIndex(hashCode, data.length);
		MOPHashEntry entry = data[index];

		while (entry != null) {
			if (key == entry.key.get()) {
				cachedValue = (MOPTagWeakReference) entry.key;
				cachedValue2[cacheIndex] = (MOPTagWeakReference) entry.key;

				return cachedValue;
			}
			entry = entry.next;
		}

		cachedValue = NULRef;
		cachedValue2[cacheIndex] = NULRef;

		return NULRef;
	}

	@Override
	public MOPTagWeakReference getTagRef(Object key) {
		if (key == cachedKey && cachedValue != NULRef)
			return cachedValue;

		cachedKey = key;

		MOPHashEntry[] data = this.data;

		int hashCode = System.identityHashCode(key);
		int index = hashIndex(hashCode, data.length);
		MOPHashEntry entry = data[index];

		while (entry != null) {
			if (key == entry.key.get()) {
				cachedValue = (MOPTagWeakReference) entry.key;

				return cachedValue;
			}
			entry = entry.next;
		}

		// create new weakreference
		MOPTagWeakReference keyref = new MOPTagWeakReference(key, hashCode);
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

			MOPHashEntry newentry = new MOPHashEntry(data[putIndex], hashCode, keyref);
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
			MOPHashEntry newentry = new MOPHashEntry(data[index], hashCode, keyref);
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
	public MOPTagWeakReference getTagRefNonCreative(Object key) {
		if (key == cachedKey)
			return cachedValue;

		cachedKey = key;

		MOPHashEntry[] data = this.data;

		int hashCode = System.identityHashCode(key);
		int index = hashIndex(hashCode, data.length);
		MOPHashEntry entry = data[index];

		while (entry != null) {
			if (key == entry.key.get()) {
				cachedValue = (MOPTagWeakReference) entry.key;

				return cachedValue;
			}
			entry = entry.next;
		}

		cachedValue = NULRef;

		return NULRef;
	}
}
