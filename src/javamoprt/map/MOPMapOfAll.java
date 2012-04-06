package javamoprt.map;

import javamoprt.MOPMonitor;
import javamoprt.MOPSet;
import javamoprt.map.hashentry.MOPHashAllEntry;
import javamoprt.ref.MOPWeakReference;

public class MOPMapOfAll extends MOPAbstractMap {
	protected MOPHashAllEntry[] data;
	protected MOPHashAllEntry[] newdata;

	protected Object lastSet = null;
	protected Object lastMap = null;

	public MOPMapOfAll(int idnum) {
		super();
		
		this.data = new MOPHashAllEntry[DEFAULT_CAPACITY];
		this.newdata = null;
		this.idnum = idnum;
	}

	@Override
	public Object getMap(MOPWeakReference key) {
		MOPHashAllEntry[] data = this.data;

		int hashCode = key.hash;
		int index = hashIndex(hashCode, data.length);
		MOPHashAllEntry entry = data[index];

		while (entry != null) {
			if (key == entry.key) {
				lastMap = entry.map;

				return entry.map;
			}
			entry = entry.next;
		}

		return null;
	}

	@Override
	public Object getSet(MOPWeakReference key) {
		MOPHashAllEntry[] data = this.data;

		int hashCode = key.hash;
		int index = hashIndex(hashCode, data.length);
		MOPHashAllEntry entry = data[index];

		while (entry != null) {
			if (key == entry.key) {
				lastSet = entry.set;

				return entry.set;
			}
			entry = entry.next;
		}

		return null;
	}

	@Override
	public Object getNode(MOPWeakReference key) {
		MOPHashAllEntry[] data = this.data;

		int hashCode = key.hash;
		int index = hashIndex(hashCode, data.length);
		MOPHashAllEntry entry = data[index];

		while (entry != null) {
			if (key == entry.key) {
				return entry.node;
			}
			entry = entry.next;
		}

		return null;
	}

	@Override
	public boolean putMap(MOPWeakReference keyref, Object map) {
		lastMap = map;

		if (multicore && data.length > DEFAULT_THREADED_CLEANUP_THREASHOLD) {
			MOPHashAllEntry[] data = this.data;

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

			MOPHashAllEntry entry = data[putIndex];

			while (entry != null) {
				if (keyref == entry.key) {

					entry.map = map;
					putIndex = -1;
					return true;
				}
				entry = entry.next;
			}

			MOPHashAllEntry newentry = new MOPHashAllEntry(data[putIndex], hashCode, keyref);
			newentry.map = map;
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

			MOPHashAllEntry entry = data[index];

			while (entry != null) {
				if (keyref == entry.key) {

					entry.map = map;
					return true;
				}
				entry = entry.next;
			}

			MOPHashAllEntry newentry = new MOPHashAllEntry(data[index], hashCode, keyref);
			newentry.map = map;
			data[index] = newentry;
			addedMappings++;

			if (multicore)
				checkCapacityNoOneIter();
			else
				checkCapacity();
		}

		return true;
	}

	@Override
	public boolean putSet(MOPWeakReference keyref, Object set) {
		lastSet = set;

		if (multicore && data.length > DEFAULT_THREADED_CLEANUP_THREASHOLD) {
			MOPHashAllEntry[] data = this.data;

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

			MOPHashAllEntry entry = data[putIndex];

			while (entry != null) {
				if (keyref == entry.key) {

					entry.set = set;
					putIndex = -1;
					return true;
				}
				entry = entry.next;
			}

			MOPHashAllEntry newentry = new MOPHashAllEntry(data[putIndex], hashCode, keyref);
			newentry.set = set;
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

			MOPHashAllEntry entry = data[index];

			while (entry != null) {
				if (keyref == entry.key) {

					entry.set = set;
					return true;
				}
				entry = entry.next;
			}

			MOPHashAllEntry newentry = new MOPHashAllEntry(data[index], hashCode, keyref);
			newentry.set = set;
			data[index] = newentry;
			addedMappings++;

			if (multicore)
				checkCapacityNoOneIter();
			else
				checkCapacity();
		}

		return true;
	}

	@Override
	public boolean putNode(MOPWeakReference keyref, Object node) {
		if (multicore && data.length > DEFAULT_THREADED_CLEANUP_THREASHOLD) {
			MOPHashAllEntry[] data = this.data;

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

			MOPHashAllEntry entry = data[putIndex];

			while (entry != null) {
				if (keyref == entry.key) {

					entry.node = node;
					putIndex = -1;
					return true;
				}
				entry = entry.next;
			}

			MOPHashAllEntry newentry = new MOPHashAllEntry(data[putIndex], hashCode, keyref);
			newentry.node = node;
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

			MOPHashAllEntry entry = data[index];

			while (entry != null) {
				if (keyref == entry.key) {
					entry.node = node;
					return true;
				}
				entry = entry.next;
			}

			MOPHashAllEntry newentry = new MOPHashAllEntry(data[index], hashCode, keyref);
			newentry.node = node;
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
	protected void endObject(int idnum) {
		this.isDeleted = true;
		for (int i = data.length - 1; i >= 0; i--) {
			MOPHashAllEntry entry = data[i];
			data[i] = null;
			while (entry != null) {
				MOPHashAllEntry next = entry.next;

				MOPAbstractMap map = (MOPAbstractMap) entry.map;
				MOPSet set = (MOPSet) entry.set;
				MOPMonitor monitor = (MOPMonitor) entry.node;

				if (map != null)
					map.endObject(idnum);
				if (set != null)
					set.endObjectAndClean(idnum);
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

		for (int i = 0; i < cleanup_piece && cleancursor >= 0; i++) {
			MOPHashAllEntry previous = null;
			MOPHashAllEntry entry = data[cleancursor];

			while (entry != null) {
				MOPHashAllEntry next = entry.next;

				MOPAbstractMap map = (MOPAbstractMap) entry.map;
				MOPSet set = (MOPSet) entry.set;
				MOPMonitor monitor = (MOPMonitor) entry.node;

				if (entry.key.get() == null) {
					if (previous == null) {
						data[cleancursor] = entry.next;
					} else {
						previous.next = entry.next;
					}

					if (map != null)
						map.endObject(idnum);
					if (set != null)
						set.endObjectAndClean(idnum);
					if (monitor != null && !monitor.MOP_terminated)
						monitor.endObject(idnum);

					entry.next = null;
					this.deletedMappings++;
				} else {
					if (map != null && map != lastMap && map.size() == 0)
						entry.map = null;
					if (set != null && set != lastSet && set.size == 0)
						entry.set = null;
					if (monitor != null && monitor.MOP_terminated)
						entry.node = null;

					if (entry.map == null && entry.set == null && entry.node == null) {
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
				}
				entry = next;
			}
			cleancursor--;
		}
	}

	@Override
	protected void cleanupiter() {
		for (int i = data.length - 1; i >= 0; i--) {
			MOPHashAllEntry entry = data[i];
			MOPHashAllEntry previous = null;
			while (entry != null) {
				MOPHashAllEntry next = entry.next;

				MOPAbstractMap map = (MOPAbstractMap) entry.map;
				MOPSet set = (MOPSet) entry.set;
				MOPMonitor monitor = (MOPMonitor) entry.node;

				if (entry.key.get() == null) {
					if (previous == null) {
						data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}

					if (map != null)
						map.endObject(idnum);
					if (set != null)
						set.endObjectAndClean(idnum);
					if (monitor != null && !monitor.MOP_terminated)
						monitor.endObject(idnum);

					entry.next = null;
					this.deletedMappings++;
				} else {
					if (map != null && map != lastMap && map.size() == 0)
						entry.map = null;
					if (set != null && set != lastSet && set.size() == 0)
						entry.set = null;
					if (monitor != null && monitor.MOP_terminated)
						entry.node = null;

					if (entry.map == null && entry.set == null && entry.node == null) {
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
				}
				entry = next;
			}
		}
	}
	
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

		MOPHashAllEntry oldEntries[] = data;
		MOPHashAllEntry newEntries[] = new MOPHashAllEntry[newCapacity];

		for (int i = oldCapacity - 1; i >= 0; i--) {
			MOPHashAllEntry entry = oldEntries[i];
			if (entry != null) {
				oldEntries[i] = null;
				do {
					MOPHashAllEntry next = entry.next;
					int index = hashIndex(entry.hashCode, newCapacity);
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
