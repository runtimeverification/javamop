package javamoprt.map;

import javamoprt.map.hashentry.MOPHashDualEntry;
import javamoprt.ref.MOPWeakReference;

public abstract class MOPAbstractMapDuo extends MOPAbstractMap {
	protected MOPHashDualEntry[] data;
	protected MOPHashDualEntry[] newdata;

	protected Object lastValue1 = null;
	protected Object lastValue2 = null;

	public MOPAbstractMapDuo() {
		super();

		this.data = new MOPHashDualEntry[DEFAULT_CAPACITY];
		this.newdata = null;
	}

	public Object get_1(MOPWeakReference key) {
		MOPHashDualEntry[] data = this.data;

		int hashCode = key.hash;
		int index = hashIndex(hashCode, data.length);
		MOPHashDualEntry entry = data[index];

		while (entry != null) {
			if (key == entry.key) {
				lastValue1 = entry.value1;
				if (lastValue1 != null) {
					return entry.value1;
				}
			}
			entry = entry.next;
		}

		return null;
	}

	public Object get_2(MOPWeakReference key) {
		MOPHashDualEntry[] data = this.data;

		int hashCode = key.hash;
		int index = hashIndex(hashCode, data.length);
		MOPHashDualEntry entry = data[index];

		while (entry != null) {
			if (key == entry.key) {
				lastValue2 = entry.value2;

				return entry.value2;
			}
			entry = entry.next;
		}

		return null;
	}

	public boolean put_1(MOPWeakReference keyref, Object value) {
		lastValue1 = value;

		if (multicore && data.length > DEFAULT_THREADED_CLEANUP_THREASHOLD) {
			MOPHashDualEntry[] data = this.data;

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

			MOPHashDualEntry entry = data[putIndex];

			while (entry != null) {
				if (keyref == entry.key) {

					entry.value1 = value;
					putIndex = -1;
					return true;
				}
				entry = entry.next;
			}

			MOPHashDualEntry newentry = new MOPHashDualEntry(data[putIndex], keyref);
			newentry.value1 = value;
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
			int hashCode = keyref.hash;
			int index = hashIndex(hashCode, data.length);

			MOPHashDualEntry entry = data[index];

			while (entry != null) {
				if (keyref == entry.key) {

					entry.value1 = value;
					return true;
				}
				entry = entry.next;
			}

			MOPHashDualEntry newentry = new MOPHashDualEntry(data[index], keyref);
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

	public boolean put_2(MOPWeakReference keyref, Object value) {
		lastValue2 = value;

		if (multicore && data.length > DEFAULT_THREADED_CLEANUP_THREASHOLD) {
			MOPHashDualEntry[] data = this.data;

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

			MOPHashDualEntry entry = data[putIndex];

			while (entry != null) {
				if (keyref == entry.key) {

					entry.value2 = value;
					putIndex = -1;
					return true;
				}
				entry = entry.next;
			}

			MOPHashDualEntry newentry = new MOPHashDualEntry(data[putIndex], keyref);
			newentry.value2 = value;
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
			int hashCode = keyref.hash;
			int index = hashIndex(hashCode, data.length);

			MOPHashDualEntry entry = data[index];

			while (entry != null) {
				if (keyref == entry.key) {

					entry.value2 = value;
					return true;
				}
				entry = entry.next;
			}

			MOPHashDualEntry newentry = new MOPHashDualEntry(data[index], keyref);
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

		MOPHashDualEntry oldEntries[] = data;
		MOPHashDualEntry newEntries[] = new MOPHashDualEntry[newCapacity];

		for (int i = oldCapacity - 1; i >= 0; i--) {
			MOPHashDualEntry entry = oldEntries[i];
			if (entry != null) {
				oldEntries[i] = null;
				do {
					MOPHashDualEntry next = entry.next;
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
