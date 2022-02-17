package com.runtimeverification.rvmonitor.java.rt.map;

import com.runtimeverification.rvmonitor.java.rt.map.hashentry.RVMHashDualEntry;
import com.runtimeverification.rvmonitor.java.rt.ref.RVMWeakReference;

public abstract class RVMAbstractMapDuo extends RVMAbstractMap {
	protected RVMHashDualEntry[] data;
	protected RVMHashDualEntry[] newdata;

	protected Object lastValue1 = null;
	protected Object lastValue2 = null;

	public RVMAbstractMapDuo() {
		super();

		this.data = new RVMHashDualEntry[DEFAULT_CAPACITY];
		this.newdata = null;
	}

	public Object get_1(RVMWeakReference key) {
		RVMHashDualEntry[] data = this.data;

		int hashCode = key.hash;
		int index = hashIndex(hashCode, data.length);
		RVMHashDualEntry entry = data[index];

		while (entry != null) {
			if (key == entry.key) {
				lastValue1 = entry.value1;

				return entry.value1;
			}
			entry = entry.next;
		}

		return null;
	}

	public Object get_2(RVMWeakReference key) {
		RVMHashDualEntry[] data = this.data;

		int hashCode = key.hash;
		int index = hashIndex(hashCode, data.length);
		RVMHashDualEntry entry = data[index];

		while (entry != null) {
			if (key == entry.key) {
				lastValue2 = entry.value2;

				return entry.value2;
			}
			entry = entry.next;
		}

		return null;
	}

	public boolean put_1(RVMWeakReference keyref, Object value) {
		lastValue1 = value;

		if (multicore && data.length > DEFAULT_THREADED_CLEANUP_THREASHOLD) {
			RVMHashDualEntry[] data = this.data;

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

			RVMHashDualEntry entry = data[putIndex];

			while (entry != null) {
				if (keyref == entry.key) {

					entry.value1 = value;
					putIndex = -1;
					return true;
				}
				entry = entry.next;
			}

			RVMHashDualEntry newentry = new RVMHashDualEntry(data[putIndex], keyref);
			newentry.value1 = value;
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

			RVMHashDualEntry entry = data[index];

			while (entry != null) {
				if (keyref == entry.key) {

					entry.value1 = value;
					return true;
				}
				entry = entry.next;
			}

			RVMHashDualEntry newentry = new RVMHashDualEntry(data[index], keyref);
			newentry.value1 = value;
			data[index] = newentry;
			addedMappings++;

			if (multicore)
				checkCapacityNoOneIter();
			else
				checkCapacity();
		}

		return true;
	}

	public boolean put_2(RVMWeakReference keyref, Object value) {
		lastValue2 = value;

		if (multicore && data.length > DEFAULT_THREADED_CLEANUP_THREASHOLD) {
			RVMHashDualEntry[] data = this.data;

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

			RVMHashDualEntry entry = data[putIndex];

			while (entry != null) {
				if (keyref == entry.key) {

					entry.value2 = value;
					putIndex = -1;
					return true;
				}
				entry = entry.next;
			}

			RVMHashDualEntry newentry = new RVMHashDualEntry(data[putIndex], keyref);
			newentry.value2 = value;
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

			RVMHashDualEntry entry = data[index];

			while (entry != null) {
				if (keyref == entry.key) {

					entry.value2 = value;
					return true;
				}
				entry = entry.next;
			}

			RVMHashDualEntry newentry = new RVMHashDualEntry(data[index], keyref);
			newentry.value2 = value;
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

		RVMHashDualEntry oldEntries[] = data;
		RVMHashDualEntry newEntries[] = new RVMHashDualEntry[newCapacity];

		for (int i = oldCapacity - 1; i >= 0; i--) {
			RVMHashDualEntry entry = oldEntries[i];
			if (entry != null) {
				oldEntries[i] = null;
				do {
					RVMHashDualEntry next = entry.next;
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
