package javamoprt.map;

import javamoprt.map.hashentry.MOPHashDualEntry;
import javamoprt.ref.MOPMultiTagWeakReference;
import javamoprt.ref.MOPTagWeakReference;
import javamoprt.ref.MOPWeakReference;

public class MOPMultiTagRefMapOfSetMon extends MOPBasicRefMapOfSetMon {
	int taglen = 1;

	static public MOPMultiTagWeakReference NULRef = new MOPMultiTagWeakReference(1, null);

	protected MOPMultiTagWeakReference cachedValue = NULRef;

	protected MOPMultiTagWeakReference[] cachedValue2 = new MOPMultiTagWeakReference[ref_locality_cache_size];

	public MOPMultiTagRefMapOfSetMon(int idnum) {
		super(idnum);

		for (int i = 0; i < ref_locality_cache_size; i++) {
			cachedValue2[i] = NULRef;
		}
	}

	public MOPMultiTagRefMapOfSetMon(int idnum, int taglen) {
		super(idnum);

		this.taglen = taglen;

		for (int i = 0; i < ref_locality_cache_size; i++) {
			cachedValue2[i] = NULRef;
		}
	}

	@Override
	public MOPMultiTagWeakReference getMultiTagRef(Object key, int joinPointId) {
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

		MOPHashDualEntry[] data = this.data;

		int hashCode = System.identityHashCode(key);
		int index = hashIndex(hashCode, data.length);
		MOPHashDualEntry entry = data[index];

		while (entry != null) {
			if (key == entry.key.get()) {
				cachedValue = (MOPMultiTagWeakReference) entry.key;
				cachedValue2[cacheIndex] = (MOPMultiTagWeakReference) entry.key;

				return cachedValue;
			}
			entry = entry.next;
		}

		// create new weakreference
		MOPMultiTagWeakReference keyref = new MOPMultiTagWeakReference(taglen, key, hashCode);
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

			MOPHashDualEntry newentry = new MOPHashDualEntry(data[putIndex], keyref);
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
			MOPHashDualEntry newentry = new MOPHashDualEntry(data[index], keyref);
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
	public MOPMultiTagWeakReference getMultiTagRefNonCreative(Object key, int joinPointId) {
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

		MOPHashDualEntry[] data = this.data;

		int hashCode = System.identityHashCode(key);
		int index = hashIndex(hashCode, data.length);
		MOPHashDualEntry entry = data[index];

		while (entry != null) {
			if (key == entry.key.get()) {
				cachedValue = (MOPMultiTagWeakReference) entry.key;
				cachedValue2[cacheIndex] = (MOPMultiTagWeakReference) entry.key;

				return cachedValue;
			}
			entry = entry.next;
		}
		cachedValue = NULRef;
		cachedValue2[cacheIndex] = NULRef;

		return NULRef;
	}

	@Override
	public MOPMultiTagWeakReference getMultiTagRef(Object key) {
		if (key == cachedKey && cachedValue != NULRef) {
			return cachedValue;
		}

		cachedKey = key;

		MOPHashDualEntry[] data = this.data;

		int hashCode = System.identityHashCode(key);
		int index = hashIndex(hashCode, data.length);
		MOPHashDualEntry entry = data[index];

		while (entry != null) {
			if (key == entry.key.get()) {
				cachedValue = (MOPMultiTagWeakReference) entry.key;

				return cachedValue;
			}
			entry = entry.next;
		}

		// create new weakreference
		MOPMultiTagWeakReference keyref = new MOPMultiTagWeakReference(taglen, key, hashCode);
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

			MOPHashDualEntry newentry = new MOPHashDualEntry(data[putIndex], keyref);
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
			MOPHashDualEntry newentry = new MOPHashDualEntry(data[index], keyref);
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
	public MOPMultiTagWeakReference getMultiTagRefNonCreative(Object key) {
		if (key == cachedKey) {
			return cachedValue;
		}

		cachedKey = key;

		MOPHashDualEntry[] data = this.data;

		int hashCode = System.identityHashCode(key);
		int index = hashIndex(hashCode, data.length);
		MOPHashDualEntry entry = data[index];

		while (entry != null) {
			if (key == entry.key.get()) {
				cachedValue = (MOPMultiTagWeakReference) entry.key;

				return cachedValue;
			}
			entry = entry.next;
		}
		cachedValue = NULRef;

		return NULRef;
	}
}
