package com.runtimeverification.rvmonitor.java.rt.map;

import com.runtimeverification.rvmonitor.java.rt.RVMMonitor;
import com.runtimeverification.rvmonitor.java.rt.RVMSet;
import com.runtimeverification.rvmonitor.java.rt.map.hashentry.RVMHashAllEntry;
import com.runtimeverification.rvmonitor.java.rt.ref.RVMWeakReference;

public class RVMMapOfAll extends RVMAbstractMap {
	protected RVMHashAllEntry[] data;
	protected RVMHashAllEntry[] newdata;

	protected Object lastSet = null;
	protected Object lastMap = null;

	public RVMMapOfAll(int idnum) {
		super();
		
		this.data = new RVMHashAllEntry[DEFAULT_CAPACITY];
		this.newdata = null;
		this.idnum = idnum;
	}

	@Override
	public Object getMap(RVMWeakReference key) {
		RVMHashAllEntry[] data = this.data;

		int hashCode = key.hash;
		int index = hashIndex(hashCode, data.length);
		RVMHashAllEntry entry = data[index];

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
	public Object getSet(RVMWeakReference key) {
		RVMHashAllEntry[] data = this.data;

		int hashCode = key.hash;
		int index = hashIndex(hashCode, data.length);
		RVMHashAllEntry entry = data[index];

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
	public Object getNode(RVMWeakReference key) {
		RVMHashAllEntry[] data = this.data;

		int hashCode = key.hash;
		int index = hashIndex(hashCode, data.length);
		RVMHashAllEntry entry = data[index];

		while (entry != null) {
			if (key == entry.key) {
				return entry.node;
			}
			entry = entry.next;
		}

		return null;
	}

	@Override
	public boolean putMap(RVMWeakReference keyref, Object map) {
		lastMap = map;

		if (multicore && data.length > DEFAULT_THREADED_CLEANUP_THREASHOLD) {
			RVMHashAllEntry[] data = this.data;

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

			RVMHashAllEntry entry = data[putIndex];

			while (entry != null) {
				if (keyref == entry.key) {

					entry.map = map;
					putIndex = -1;
					return true;
				}
				entry = entry.next;
			}

			RVMHashAllEntry newentry = new RVMHashAllEntry(data[putIndex], keyref);
			newentry.map = map;
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

			RVMHashAllEntry entry = data[index];

			while (entry != null) {
				if (keyref == entry.key) {

					entry.map = map;
					return true;
				}
				entry = entry.next;
			}

			RVMHashAllEntry newentry = new RVMHashAllEntry(data[index], keyref);
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
	public boolean putSet(RVMWeakReference keyref, Object set) {
		lastSet = set;

		if (multicore && data.length > DEFAULT_THREADED_CLEANUP_THREASHOLD) {
			RVMHashAllEntry[] data = this.data;

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

			RVMHashAllEntry entry = data[putIndex];

			while (entry != null) {
				if (keyref == entry.key) {

					entry.set = set;
					putIndex = -1;
					return true;
				}
				entry = entry.next;
			}

			RVMHashAllEntry newentry = new RVMHashAllEntry(data[putIndex], keyref);
			newentry.set = set;
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

			RVMHashAllEntry entry = data[index];

			while (entry != null) {
				if (keyref == entry.key) {

					entry.set = set;
					return true;
				}
				entry = entry.next;
			}

			RVMHashAllEntry newentry = new RVMHashAllEntry(data[index], keyref);
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
	public boolean putNode(RVMWeakReference keyref, Object node) {
		if (multicore && data.length > DEFAULT_THREADED_CLEANUP_THREASHOLD) {
			RVMHashAllEntry[] data = this.data;

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

			RVMHashAllEntry entry = data[putIndex];

			while (entry != null) {
				if (keyref == entry.key) {

					entry.node = node;
					putIndex = -1;
					return true;
				}
				entry = entry.next;
			}

			RVMHashAllEntry newentry = new RVMHashAllEntry(data[putIndex], keyref);
			newentry.node = node;
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

			RVMHashAllEntry entry = data[index];

			while (entry != null) {
				if (keyref == entry.key) {
					entry.node = node;
					return true;
				}
				entry = entry.next;
			}

			RVMHashAllEntry newentry = new RVMHashAllEntry(data[index], keyref);
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
			RVMHashAllEntry entry = data[i];
			data[i] = null;
			while (entry != null) {
				RVMHashAllEntry next = entry.next;

				RVMAbstractMap map = (RVMAbstractMap) entry.map;
				RVMSet set = (RVMSet) entry.set;
				RVMMonitor monitor = (RVMMonitor) entry.node;

				if (map != null)
					map.endObject(idnum);
				if (set != null)
					set.endObjectAndClean(idnum);
				if (monitor != null && !monitor.RVM_terminated)
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
			RVMHashAllEntry previous = null;
			RVMHashAllEntry entry = data[cleancursor];

			while (entry != null) {
				RVMHashAllEntry next = entry.next;

				RVMAbstractMap map = (RVMAbstractMap) entry.map;
				RVMSet set = (RVMSet) entry.set;
				RVMMonitor monitor = (RVMMonitor) entry.node;

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
					if (monitor != null && !monitor.RVM_terminated)
						monitor.endObject(idnum);

					entry.next = null;
					this.deletedMappings++;
				} else {
					if (map != null && map != lastMap && map.size() == 0)
						entry.map = null;
					if (set != null && set != lastSet && set.size == 0)
						entry.set = null;
					if (monitor != null && monitor.RVM_terminated)
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
			RVMHashAllEntry entry = data[i];
			RVMHashAllEntry previous = null;
			while (entry != null) {
				RVMHashAllEntry next = entry.next;

				RVMAbstractMap map = (RVMAbstractMap) entry.map;
				RVMSet set = (RVMSet) entry.set;
				RVMMonitor monitor = (RVMMonitor) entry.node;

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
					if (monitor != null && !monitor.RVM_terminated)
						monitor.endObject(idnum);

					entry.next = null;
					this.deletedMappings++;
				} else {
					if (map != null && map != lastMap && map.size() == 0)
						entry.map = null;
					if (set != null && set != lastSet && set.size() == 0)
						entry.set = null;
					if (monitor != null && monitor.RVM_terminated)
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

		RVMHashAllEntry oldEntries[] = data;
		RVMHashAllEntry newEntries[] = new RVMHashAllEntry[newCapacity];

		for (int i = oldCapacity - 1; i >= 0; i--) {
			RVMHashAllEntry entry = oldEntries[i];
			if (entry != null) {
				oldEntries[i] = null;
				do {
					RVMHashAllEntry next = entry.next;
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
