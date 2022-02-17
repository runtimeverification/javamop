package com.runtimeverification.rvmonitor.java.rt.map;

import com.runtimeverification.rvmonitor.java.rt.RVMMonitor;
import com.runtimeverification.rvmonitor.java.rt.RVMSet;
import com.runtimeverification.rvmonitor.java.rt.map.hashentry.RVMHashDualEntry;
import com.runtimeverification.rvmonitor.java.rt.map.hashentry.RVMHashEntry;
import com.runtimeverification.rvmonitor.java.rt.map.hashentry.RVMHashRefEntry;

public class RVMMapCleaner extends Thread {
	int id;
	
	public RVMCleanable map = null;
	public RVMMapCleaner next = null;

	public RVMMapCleaner(int id) {
		this.id = id;
		this.setDaemon(true);
	}
	
	protected void cleanupMapOfMap(RVMMapOfMap map) {
		int numDeleted = 0;
		for (int i = map.data.length - 1; i >= 0; i--) {
			map.cleanIndex = i;
			while (map.putIndex == i) {
				map.cleanIndex = -1;
				while (map.putIndex == i) {
					Thread.yield();
				}
				map.cleanIndex = i;
			}

			RVMHashEntry previous = null;
			RVMHashEntry entry = map.data[i];
			while (entry != null) {
				RVMHashEntry next = entry.next;
				RVMAbstractMap value = (RVMAbstractMap) entry.value;
				if (entry.key.get() == null) {
					if (previous == null) {
						map.data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}

					value.isDeleted = true;
					value.endObject(map.idnum);
					
					entry.next = null;
					numDeleted++;
				} else if (value != map.lastValue1 && value.size() == 0) {
					if (previous == null) {
						map.data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}

					value.isDeleted = true;
					
					entry.next = null;
					numDeleted++;
				} else {
					previous = entry;
				}
				entry = next;
			}
		}
		
		map.cleanIndex = -1;
		map.deletedMappings += numDeleted;
	}
	
	protected void cleanupMapOfMapBasicRef(RVMBasicRefMapOfMap map) {
		int numDeleted = 0;
		for (int i = map.data.length - 1; i >= 0; i--) {
			map.cleanIndex = i;
			while (map.putIndex == i) {
				map.cleanIndex = -1;
				while (map.putIndex == i) {
					Thread.yield();
				}
				map.cleanIndex = i;
			}

			RVMHashEntry previous = null;
			RVMHashEntry entry = map.data[i];
			while (entry != null) {
				RVMHashEntry next = entry.next;
				RVMAbstractMap value = (RVMAbstractMap) entry.value;
				if (entry.key.get() == null) {
					if (previous == null) {
						map.data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}

					value.isDeleted = true;
					value.endObject(map.idnum);
					
					entry.next = null;
					numDeleted++;
				} else {
					if (value != null && value != map.lastValue1 && value.size() == 0) {
						entry.value = null;
					}

					previous = entry;
				}
				entry = next;
			}
		}
		
		map.cleanIndex = -1;
		map.deletedMappings += numDeleted;
	}
	
	protected void cleanupMapOfSet(RVMMapOfSetMon map){
		int numDeleted = 0;
		for (int i = map.data.length - 1; i >= 0; i--) {
			map.cleanIndex = i;
			while (map.putIndex == i) {
				map.cleanIndex = -1;
				while (map.putIndex == i) {
					Thread.yield();
				}
				map.cleanIndex = i;
			}

			RVMHashDualEntry previous = null;
			RVMHashDualEntry entry = map.data[i];
			while (entry != null) {
				RVMHashDualEntry next = entry.next;

				RVMSet set = (RVMSet) entry.value1;
				RVMMonitor monitor = (RVMMonitor) entry.value2;
				if (entry.key.get() == null) {
					if (previous == null) {
						map.data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}

					if(monitor != null && !monitor.RVM_terminated)
						monitor.endObject(map.idnum);
					if(set != null)
						set.endObjectAndClean(map.idnum);

					entry.next = null;
					numDeleted++;
				} else if ( (monitor == null || monitor.RVM_terminated) && (set == null || (set != map.lastValue1 && set.size() == 0))) {
					if (previous == null) {
						map.data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}

					entry.next = null;
					numDeleted++;
				} else {
					previous = entry;
				}
				entry = next;
			}
		}
		map.cleanIndex = -1;
		map.deletedMappings += numDeleted;
	}
	
	protected void cleanupMapOfSetBasicRef(RVMBasicRefMapOfSetMon map){
		int numDeleted = 0;
		for (int i = map.data.length - 1; i >= 0; i--) {
			map.cleanIndex = i;
			while (map.putIndex == i) {
				map.cleanIndex = -1;
				while (map.putIndex == i) {
					Thread.yield();
				}
				map.cleanIndex = i;
			}

			RVMHashDualEntry previous = null;
			RVMHashDualEntry entry = map.data[i];
			while (entry != null) {
				RVMHashDualEntry next = entry.next;

				RVMSet set = (RVMSet) entry.value1;
				RVMMonitor monitor = (RVMMonitor) entry.value2;
				
				if (entry.key.get() == null) {
					if (previous == null) {
						map.data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}

					if(monitor != null && !monitor.RVM_terminated)
						monitor.endObject(map.idnum);
					if(set != null)
						set.endObjectAndClean(map.idnum);

					entry.next = null;
					numDeleted++;
				} else {
					if (set != null && (set != map.lastValue1 && set.size() == 0)) {
						entry.value1 = null;
					}
					if (monitor != null && monitor.RVM_terminated){
						entry.value2 = null;
					}
					
					previous = entry;
				}
				entry = next;
			}
		}
		map.cleanIndex = -1;
		map.deletedMappings += numDeleted;
	}
	
	protected void cleanupMapOfMonitor(RVMMapOfMonitor map){
		int numDeleted = 0;
		for (int i = map.data.length - 1; i >= 0; i--) {
			map.cleanIndex = i;
			while (map.putIndex == i) {
				map.cleanIndex = -1;
				while (map.putIndex == i) {
					Thread.yield();
				}
				map.cleanIndex = i;
			}

			RVMHashEntry previous = null;
			RVMHashEntry entry = map.data[i];
			while (entry != null) {
				RVMHashEntry next = entry.next;
				RVMMonitor monitor = (RVMMonitor) entry.value;
				if (entry.key.get() == null) {
					if (previous == null) {
						map.data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}
					if (!monitor.RVM_terminated)
						monitor.endObject(map.idnum);

					entry.next = null;
					numDeleted++;
				} else if (monitor.RVM_terminated) {
					if (previous == null) {
						map.data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}

					entry.next = null;
					numDeleted++;
				} else {
					previous = entry;
				}
				entry = next;
			}
		}

		map.cleanIndex = -1;
		map.deletedMappings += numDeleted;
	}
	
	protected void cleanupMapOfMonitorBasicRef(RVMBasicRefMapOfMonitor map){
		int numDeleted = 0;
		for (int i = map.data.length - 1; i >= 0; i--) {
			map.cleanIndex = i;
			while (map.putIndex == i) {
				map.cleanIndex = -1;
				while (map.putIndex == i) {
					Thread.yield();
				}
				map.cleanIndex = i;
			}

			RVMHashEntry previous = null;
			RVMHashEntry entry = map.data[i];
			while (entry != null) {
				RVMHashEntry next = entry.next;
				RVMMonitor monitor = (RVMMonitor) entry.value;
				if (entry.key.get() == null) {
					if (previous == null) {
						map.data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}
					if (!monitor.RVM_terminated)
						monitor.endObject(map.idnum);

					entry.next = null;
					numDeleted++;
				} else {
					if (monitor != null && monitor.RVM_terminated) {
						entry.value = null;
					}
					previous = entry;
				}
				entry = next;
			}
		}

		map.cleanIndex = -1;
		map.deletedMappings += numDeleted;
	}
	
	protected void cleanupMapBasicRefMap(RVMBasicRefMap map){
		int numDeleted = 0;
		for (int i = map.data.length - 1; i >= 0; i--) {
			map.cleanIndex = i;
			while (map.putIndex == i) {
				map.cleanIndex = -1;
				while (map.putIndex == i) {
					Thread.yield();
				}
				map.cleanIndex = i;
			}

			RVMHashRefEntry previous = null;
			RVMHashRefEntry entry = map.data[i];
			while (entry != null) {
				RVMHashRefEntry next = entry.next;
				if (entry.key.get() == null) {
					if (previous == null) {
						map.data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}

					entry.next = null;
					numDeleted++;
				} else {
					previous = entry;
				}
				entry = next;
			}
		}

		map.cleanIndex = -1;
		map.deletedMappings += numDeleted;
	}

	protected void cleanup(RVMMap map) {
		int numDeleted = 0;
		if (map instanceof RVMMapOfMap) {
			if(map instanceof RVMBasicRefMapOfMap){
				RVMBasicRefMapOfMap mapOfMapBasicRef = (RVMBasicRefMapOfMap) map;
				cleanupMapOfMapBasicRef(mapOfMapBasicRef);
			} else {
				RVMMapOfMap mapOfMap = (RVMMapOfMap) map;
				cleanupMapOfMap(mapOfMap);
			}
		} else if (map instanceof RVMMapOfSetMon) {
			if(map instanceof RVMBasicRefMapOfSetMon){
				RVMBasicRefMapOfSetMon mapOfSetBasicRef = (RVMBasicRefMapOfSetMon) map;
				cleanupMapOfSetBasicRef(mapOfSetBasicRef);
			} else {
				RVMMapOfSetMon mapOfSet = (RVMMapOfSetMon) map;
				cleanupMapOfSet(mapOfSet);
			}
		} else if (map instanceof RVMMapOfMonitor) {
			if (map instanceof RVMBasicRefMapOfMonitor) {
				RVMBasicRefMapOfMonitor mapOfMonitorBasicRef = (RVMBasicRefMapOfMonitor) map;
				cleanupMapOfMonitorBasicRef(mapOfMonitorBasicRef);
			} else {
				RVMMapOfMonitor mapOfMonitor = (RVMMapOfMonitor) map;
				cleanupMapOfMonitor(mapOfMonitor);
			}
		}
	}
	
	protected void cleanup(RVMBasicRefMap map) {
		int numDeleted = 0;
		if (map instanceof RVMBasicRefMap){
			RVMBasicRefMap mapRefMap = map;
			cleanupMapBasicRefMap(mapRefMap);
		}
	}


	protected void maintainMap(RVMAbstractMapSolo map) {
		cleanup(map);
		if (map.addedMappings - map.deletedMappings >= map.datathreshold) {
			int oldCapacity = map.data.length;
			int newCapacity = oldCapacity * 2;
			if (newCapacity <= map.MAXIMUM_CAPACITY) {
				map.newdata = new RVMHashEntry[newCapacity];

				while (map.putIndex != -1) {
					Thread.yield();
				}

				RVMHashEntry oldEntries[] = map.data;
				RVMHashEntry newEntries[] = map.newdata;

				for (int i = oldCapacity - 1; i >= 0; i--) {
					RVMHashEntry entry = oldEntries[i];
					if (entry != null) {
						oldEntries[i] = null;
						do {
							RVMHashEntry next = entry.next;
							int index = map.hashIndex(entry.key.hash, newCapacity);
							entry.next = newEntries[index];
							newEntries[index] = entry;
							entry = next;
						} while (entry != null);
					}
				}
				map.datathreshold = (int) (newCapacity * map.DEFAULT_LOAD_FACTOR);
				// map.datalowthreshold = (int) (newCapacity *
				// map.DEFAULT_REDUCE_FACTOR);
				// map.cleanupThreshold = map.data.length / 5;

				map.data = map.newdata;
				map.newdata = null;
			}
		}
	}

	protected void maintainMap(RVMMapOfSetMon map) {
		cleanup(map);
		if (map.addedMappings - map.deletedMappings >= map.datathreshold) {
			int oldCapacity = map.data.length;
			int newCapacity = oldCapacity * 2;
			if (newCapacity <= map.MAXIMUM_CAPACITY) {
				map.newdata = new RVMHashDualEntry[newCapacity];

				while (map.putIndex != -1) {
					Thread.yield();
				}

				RVMHashDualEntry oldEntries[] = map.data;
				RVMHashDualEntry newEntries[] = map.newdata;

				for (int i = oldCapacity - 1; i >= 0; i--) {
					RVMHashDualEntry entry = oldEntries[i];
					if (entry != null) {
						oldEntries[i] = null;
						do {
							RVMHashDualEntry next = entry.next;
							int index = map.hashIndex(entry.key.hash, newCapacity);
							entry.next = newEntries[index];
							newEntries[index] = entry;
							entry = next;
						} while (entry != null);
					}
				}
				map.datathreshold = (int) (newCapacity * map.DEFAULT_LOAD_FACTOR);
				// map.datalowthreshold = (int) (newCapacity *
				// map.DEFAULT_REDUCE_FACTOR);
				// map.cleanupThreshold = map.data.length / 5;

				map.data = map.newdata;
				map.newdata = null;
			}
		}
	}

	protected void maintainMap(RVMBasicRefMap map) {
		cleanup(map);
		if (map.addedMappings - map.deletedMappings >= map.datathreshold) {
			int oldCapacity = map.data.length;
			int newCapacity = oldCapacity * 2;
			if (newCapacity <= map.MAXIMUM_CAPACITY) {
				map.newdata = new RVMHashRefEntry[newCapacity];

				while (map.putIndex != -1) {
					Thread.yield();
				}

				RVMHashRefEntry oldEntries[] = map.data;
				RVMHashRefEntry newEntries[] = map.newdata;

				for (int i = oldCapacity - 1; i >= 0; i--) {
					RVMHashRefEntry entry = oldEntries[i];
					if (entry != null) {
						oldEntries[i] = null;
						do {
							RVMHashRefEntry next = entry.next;
							int index = map.hashIndex(entry.key.hash, newCapacity);
							entry.next = newEntries[index];
							newEntries[index] = entry;
							entry = next;
						} while (entry != null);
					}
				}
				map.datathreshold = (int) (newCapacity * map.DEFAULT_LOAD_FACTOR);
				// map.datalowthreshold = (int) (newCapacity *
				// map.DEFAULT_REDUCE_FACTOR);
				// map.cleanupThreshold = map.data.length / 5;

				map.data = map.newdata;
				map.newdata = null;
			}
		}
	}	
	
	public void run() {
		while (true) {
			if (map != null) {
				if (!map.isDeleted) {
//					System.err.println("Cleaner " + id + " cleaning " + map);
					if(map instanceof RVMMapOfSetMon){
						RVMMapOfSetMon mopMap = (RVMMapOfSetMon) map;
						maintainMap(mopMap);
						mopMap.lastsize = (int) (mopMap.addedMappings - mopMap.deletedMappings);
						map.isCleaning = false;
					} else if(map instanceof RVMAbstractMapSolo){
						RVMAbstractMapSolo mopMap = (RVMAbstractMapSolo) map;
						maintainMap(mopMap);
						mopMap.lastsize = (int) (mopMap.addedMappings - mopMap.deletedMappings);
						map.isCleaning = false;
					} else if(map instanceof RVMBasicRefMap){
						RVMBasicRefMap mopMap = (RVMBasicRefMap) map;
						maintainMap(mopMap);
						mopMap.lastsize = (int) (mopMap.addedMappings - mopMap.deletedMappings);
						map.isCleaning = false;
					}					
			}

				map = null;
				Thread.yield();
			} else {
				Thread.yield();
				if (map == null){
					try {
						//Thread.sleep(RVMMapManager.DEFAULT_MANAGEENT_PERIOD_MSEC);
						Thread.sleep(0, RVMMapManager.DEFAULT_MANAGEENT_PERIOD_NSEC);
					} catch (Exception e) {
						System.err.println("[RVMMapCleaner] Thread cannot sleep.");
					}
				}
			}

		}
	}
}
