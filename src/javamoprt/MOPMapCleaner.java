package javamoprt;

import javamoprt.MOPAbstractMap.MOPHashEntry;

public class MOPMapCleaner extends Thread {
	int id;
	
	protected MOPCleanable map = null;
	protected MOPMapCleaner next = null;

	protected MOPMapCleaner(int id) {
		this.id = id;
		this.setDaemon(true);
	}
	
	protected void cleanupMapOfMap(MOPMapOfMap map) {
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

			MOPHashEntry previous = null;
			MOPHashEntry entry = map.data[i];
			while (entry != null) {
				MOPHashEntry next = entry.next;
				MOPAbstractMap value = (MOPAbstractMap) entry.getValue();
				if (entry.key.get() == null) {
					if (previous == null) {
						map.data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}

					value.isDeleted = true;
					value.endObject(map.idnum);
					numDeleted++;
				} else if (value != map.lastValue && value.size() == 0) {
					if (previous == null) {
						map.data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}

					value.isDeleted = true;
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
	
	protected void cleanupMapOfSet(MOPMapOfSet map){
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

			MOPHashEntry previous = null;
			MOPHashEntry entry = map.data[i];
			while (entry != null) {
				MOPHashEntry next = entry.next;
				MOPSet value = (MOPSet) entry.getValue();
				if (entry.key.get() == null) {
					if (previous == null) {
						map.data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}
					value.endObjectAndClean(map.idnum);

					numDeleted++;
				} else if (value != map.lastValue && value.size() == 0) {
					if (previous == null) {
						map.data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}

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
	
	
	protected void cleanupMapOfMonitor(MOPMapOfMonitor map){
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

			MOPHashEntry previous = null;
			MOPHashEntry entry = map.data[i];
			while (entry != null) {
				MOPHashEntry next = entry.next;
				MOPMonitor monitor = (MOPMonitor) entry.getValue();
				if (entry.key.get() == null) {
					if (previous == null) {
						map.data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}
					if (!monitor.MOP_terminated)
						monitor.endObject(map.idnum);

					numDeleted++;
				} else if (monitor.MOP_terminated) {
					if (previous == null) {
						map.data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}

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
	
	protected void cleanup(MOPAbstractMap map) {
		int numDeleted = 0;
		if (map instanceof MOPMapOfMap) {
			MOPMapOfMap mapOfMap = (MOPMapOfMap) map;
			cleanupMapOfMap(mapOfMap);
		} else if (map instanceof MOPMapOfSet) {
			MOPMapOfSet mapOfSet = (MOPMapOfSet) map;
			cleanupMapOfSet(mapOfSet);
		} else if (map instanceof MOPMapOfMonitor) {
			MOPMapOfMonitor mapOfMonitor = (MOPMapOfMonitor) map;
			cleanupMapOfMonitor(mapOfMonitor);
		}
	}

	protected void maintainMap(MOPAbstractMap map) {
		cleanup(map);
		if (map.addedMappings - map.deletedMappings >= map.datathreshold) {
			int oldCapacity = map.data.length;
			int newCapacity = oldCapacity * 2;
			if (newCapacity <= map.MAXIMUM_CAPACITY) {
				map.newdata = new MOPHashEntry[newCapacity];

				while (map.putIndex != -1) {
					Thread.yield();
				}

				MOPHashEntry oldEntries[] = map.data;
				MOPHashEntry newEntries[] = map.newdata;

				for (int i = oldCapacity - 1; i >= 0; i--) {
					MOPHashEntry entry = oldEntries[i];
					if (entry != null) {
						oldEntries[i] = null;
						do {
							MOPHashEntry next = entry.next;
							int index = map.hashIndex(entry.hashCode, newCapacity);
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
					if(map instanceof MOPAbstractMap){
						MOPAbstractMap mopMap = (MOPAbstractMap) map;
						maintainMap(mopMap);
						mopMap.lastsize = (int) (mopMap.addedMappings - mopMap.deletedMappings);
						map.isCleaning = false;
					} else if(map instanceof MOPMultiMapNode){
						MOPMultiMapNode mopMultiMap = (MOPMultiMapNode) map;
						
						// blah
						
						map.isCleaning = false;
					}
				}

				map = null;
				Thread.yield();
			} else {
				Thread.yield();
				if (map == null){
					try {
						//Thread.sleep(MOPMapManager.DEFAULT_MANAGEENT_PERIOD_MSEC);
						Thread.sleep(0, MOPMapManager.DEFAULT_MANAGEENT_PERIOD_NSEC);
					} catch (Exception e) {
						System.err.println("[MOPMapCleaner] Thread cannot sleep.");
					}
				}
			}

		}
	}
}
