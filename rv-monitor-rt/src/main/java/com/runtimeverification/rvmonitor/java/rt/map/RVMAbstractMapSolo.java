package com.runtimeverification.rvmonitor.java.rt.map;

import com.runtimeverification.rvmonitor.java.rt.map.hashentry.RVMHashEntry;
import com.runtimeverification.rvmonitor.java.rt.ref.RVMWeakReference;

public abstract class RVMAbstractMapSolo extends RVMAbstractMap {
	protected RVMHashEntry[] data;
	protected RVMHashEntry[] newdata;

	protected Object lastValue1 = null;

	public RVMAbstractMapSolo() {
		super();

		this.data = new RVMHashEntry[DEFAULT_CAPACITY];
		this.newdata = null;
	}

	public Object get_1(RVMWeakReference key) {
		RVMHashEntry[] data = this.data;

		int hashCode = key.hash;
		int index = hashIndex(hashCode, data.length);
		RVMHashEntry entry = data[index];

		while (entry != null) {
			if (key == entry.key) {
				lastValue1 = entry.value;

				return entry.value;
			}
			entry = entry.next;
		}

		return null;
	}

	public boolean put_1(RVMWeakReference keyref, Object value) {
		lastValue1 = value;

		if (multicore && data.length > DEFAULT_THREADED_CLEANUP_THREASHOLD) {
			RVMHashEntry[] data = this.data;

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

			RVMHashEntry newentry = new RVMHashEntry(data[putIndex], keyref, value);
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
			int hashCode = keyref.hash;
			int index = hashIndex(hashCode, data.length);

			RVMHashEntry newentry = new RVMHashEntry(data[index], keyref, value);
			data[index] = newentry;
			addedMappings++;

			if (multicore)
				checkCapacityNoOneIter();
			else
				checkCapacity();
		}

		return true;
	}

	/* ************************************************************************************ */

	@Override
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

	@Override
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

	@Override
	protected void adjustCapacity(int newCapacity) {
		int oldCapacity = data.length;

		RVMHashEntry oldEntries[] = data;
		RVMHashEntry newEntries[] = new RVMHashEntry[newCapacity];

		for (int i = oldCapacity - 1; i >= 0; i--) {
			RVMHashEntry entry = oldEntries[i];
			if (entry != null) {
				oldEntries[i] = null;
				do {
					RVMHashEntry next = entry.next;
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
