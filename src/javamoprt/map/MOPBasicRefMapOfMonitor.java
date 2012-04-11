package javamoprt.map;

import javamoprt.MOPMonitor;
import javamoprt.map.hashentry.MOPHashEntry;
import javamoprt.ref.MOPMultiTagWeakReference;
import javamoprt.ref.MOPTagWeakReference;
import javamoprt.ref.MOPWeakReference;

public class MOPBasicRefMapOfMonitor extends MOPMapOfMonitor implements MOPRefMap {
	static public MOPWeakReference NULRef = new MOPWeakReference(null);

	protected Object cachedKey = null;
	protected MOPWeakReference cachedValue = NULRef;

	protected Object[] cachedKey2 = new Object[ref_locality_cache_size];
	protected MOPWeakReference[] cachedValue2 = new MOPWeakReference[ref_locality_cache_size];

	public MOPBasicRefMapOfMonitor(int idnum) {
		super(idnum);

		for (int i = 0; i < ref_locality_cache_size; i++) {
			cachedKey2[i] = null;
			cachedValue2[i] = NULRef;
		}
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

		MOPHashEntry[] data = this.data;

		int hashCode = System.identityHashCode(key);
		int index = hashIndex(hashCode, data.length);
		MOPHashEntry entry = data[index];

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

			MOPHashEntry newentry = new MOPHashEntry(data[putIndex], keyref);
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
			MOPHashEntry newentry = new MOPHashEntry(data[index], keyref);
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

		MOPHashEntry[] data = this.data;

		int hashCode = System.identityHashCode(key);
		int index = hashIndex(hashCode, data.length);
		MOPHashEntry entry = data[index];

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

	@Override
	public MOPTagWeakReference getTagRef(Object key, int joinPointId) {
		return null;
	}

	@Override
	public MOPTagWeakReference getTagRefNonCreative(Object key, int joinPointId) {
		return null;
	}

	@Override
	public MOPMultiTagWeakReference getMultiTagRef(Object key, int joinPointId) {
		return null;
	}

	@Override
	public MOPMultiTagWeakReference getMultiTagRefNonCreative(Object key, int joinPointId) {
		return null;
	}

	@Override
	public MOPWeakReference getRef(Object key) {
		if (key == cachedKey && cachedValue != NULRef) {
			return cachedValue;
		}

		cachedKey = key;

		MOPHashEntry[] data = this.data;

		int hashCode = System.identityHashCode(key);
		int index = hashIndex(hashCode, data.length);
		MOPHashEntry entry = data[index];

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

			MOPHashEntry newentry = new MOPHashEntry(data[putIndex], keyref);
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
			MOPHashEntry newentry = new MOPHashEntry(data[index], keyref);
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

		MOPHashEntry[] data = this.data;

		int hashCode = System.identityHashCode(key);
		int index = hashIndex(hashCode, data.length);
		MOPHashEntry entry = data[index];

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

	@Override
	protected void endObject(int idnum) {
		this.isDeleted = true;
		for (int i = data.length - 1; i >= 0; i--) {
			MOPHashEntry entry = data[i];
			data[i] = null;
			while (entry != null) {
				MOPHashEntry next = entry.next;

				MOPMonitor monitor = (MOPMonitor) entry.value;

				if (monitor != null && !monitor.MOP_terminated)
					monitor.endObject(idnum);

				entry.next = null;
				entry = next;
			}
		}

		this.deletedMappings = this.addedMappings;
	}

	@Override
	protected void cleanupchunkiter() {
		if (cleancursor < 0)
			cleancursor = data.length - 1;

		for (int i = 0; i < ref_cleanup_piece && cleancursor >= 0; i++) {
			MOPHashEntry previous = null;
			MOPHashEntry entry = data[cleancursor];

			while (entry != null) {
				MOPHashEntry next = entry.next;

				MOPMonitor monitor = (MOPMonitor) entry.value;

				if (entry.key.get() == null) {
					if (previous == null) {
						data[cleancursor] = entry.next;
					} else {
						previous.next = entry.next;
					}

					if (monitor != null && !monitor.MOP_terminated)
						monitor.endObject(idnum);

					entry.next = null;
					this.deletedMappings++;
				} else {
					if (monitor != null && monitor.MOP_terminated){
						entry.value = null;
					}
					
					previous = entry;
				}
				entry = next;
			}
			cleancursor--;
		}
	}

	@Override
	protected void cleanupiter() {
		for (int i = data.length - 1; i >= 0; i--) {
			MOPHashEntry entry = data[i];
			MOPHashEntry previous = null;
			while (entry != null) {
				MOPHashEntry next = entry.next;

				MOPMonitor monitor = (MOPMonitor) entry.value;
				
				if (entry.key.get() == null) {
					if (previous == null) {
						data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}

					if (monitor != null && !monitor.MOP_terminated)
						monitor.endObject(idnum);

					entry.next = null;
					this.deletedMappings++;
				} else {
					if (monitor != null && monitor.MOP_terminated){
						entry.value = null;
					}
					
					previous = entry;
				}
				entry = next;
			}
		}
	}

}
