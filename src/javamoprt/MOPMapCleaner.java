package javamoprt;

import javamoprt.MOPMultiMapNode.MOPHashEntry;

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

			MOPAbstractMap.MOPHashEntry previous = null;
			MOPAbstractMap.MOPHashEntry entry = map.data[i];
			while (entry != null) {
				MOPAbstractMap.MOPHashEntry next = entry.next;
				MOPAbstractMap value = (MOPAbstractMap) entry.getValue();
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
				} else if (value != map.lastValue && value.size() == 0) {
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

			MOPAbstractMap.MOPHashEntry previous = null;
			MOPAbstractMap.MOPHashEntry entry = map.data[i];
			while (entry != null) {
				MOPAbstractMap.MOPHashEntry next = entry.next;
				MOPSet value = (MOPSet) entry.getValue();
				if (entry.key.get() == null) {
					if (previous == null) {
						map.data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}
					value.endObjectAndClean(map.idnum);

					entry.next = null;
					numDeleted++;
				} else if (value != map.lastValue && value.size() == 0) {
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

			MOPAbstractMap.MOPHashEntry previous = null;
			MOPAbstractMap.MOPHashEntry entry = map.data[i];
			while (entry != null) {
				MOPAbstractMap.MOPHashEntry next = entry.next;
				MOPMonitor monitor = (MOPMonitor) entry.getValue();
				if (entry.key.get() == null) {
					if (previous == null) {
						map.data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}
					if (!monitor.MOP_terminated)
						monitor.endObject(map.idnum);

					entry.next = null;
					numDeleted++;
				} else if (monitor.MOP_terminated) {
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
	
	protected void cleanupMultiMapNode(MOPMultiMapNode map){
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

			MOPMultiMapNode.MOPHashEntry previous = null;
			MOPMultiMapNode.MOPHashEntry entry = map.data[i];
			while (entry != null) {
				MOPMultiMapNode.MOPHashEntry next = entry.next;
				Object[] values = entry.getValue();
				if (entry.key.get() == null) {
					if (previous == null) {
						map.data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}
					for(int j = 0; j < map.valueSize; j++){
						if(values[j] != null){
							if(map.valuePattern[j].type == MOPMultiMapSignature.MAP_OF_MONITOR){
								MOPMonitor monitor = (MOPMonitor) values[j];
								if (!monitor.MOP_terminated)
									monitor.endObject(map.valuePattern[j].idnum);
							} else if(map.valuePattern[j].type == MOPMultiMapSignature.MAP_OF_SET){
								MOPSet set = (MOPSet) values[j];
								set.endObjectAndClean(map.valuePattern[j].idnum);
							}
							values[j] = null;
						}
					}

					entry.next = null;
					numDeleted++;
				} else {
					boolean exist = false;

					for(int j = 0; j < map.valueSize; j++){
						if(values[j] != null){
							if(map.valuePattern[j].type == MOPMultiMapSignature.MAP_OF_MONITOR){
								MOPMonitor monitor = (MOPMonitor) values[j];
								if (!monitor.MOP_terminated) {
									exist = true;
								} else {
									values[j] = null;
								}
							} else if(map.valuePattern[j].type == MOPMultiMapSignature.MAP_OF_SET){
								MOPSet set = (MOPSet) values[j];
								if (!(set != map.lastValue && set.size() == 0)) {
									exist = true;
								} else {
									values[j] = null;
								}
							}
						}
					}
					
					if(!exist){
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
				}
				entry = next;
			}
		}

		map.cleanIndex = -1;
		map.deletedMappings += numDeleted;
	}

	protected void cleanupMultiMapOfMap(MOPMultiMapOfMap map){
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

			MOPMultiMapOfMap.MOPHashEntry previous = null;
			MOPMultiMapOfMap.MOPHashEntry entry = map.data[i];
			while (entry != null) {
				MOPMultiMapOfMap.MOPHashEntry next = entry.next;
				MOPMap value = (MOPMap)entry.getValue();
				if (entry.key.get() == null) {
					if (previous == null) {
						map.data[i] = entry.next;
					} else {
						previous.next = entry.next;
					}

					value.endObject(map.valuePattern);

					entry.next = null;
					numDeleted++;
				} else if (value != map.lastValue && value.size() == 0) {
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

	protected void cleanup(MOPMap map) {
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
		} else if (map instanceof MOPMultiMapNode) {
			MOPMultiMapNode multiMapNode = (MOPMultiMapNode) map;
			cleanupMultiMapNode(multiMapNode);
		} else if (map instanceof MOPMultiMapOfMap) {
			MOPMultiMapOfMap multiMapOfMap = (MOPMultiMapOfMap) map;
			cleanupMultiMapOfMap(multiMapOfMap);
		}
	}

	protected void maintainMap(MOPAbstractMap map) {
		cleanup(map);
		if (map.addedMappings - map.deletedMappings >= map.datathreshold) {
			int oldCapacity = map.data.length;
			int newCapacity = oldCapacity * 2;
			if (newCapacity <= map.MAXIMUM_CAPACITY) {
				map.newdata = new MOPAbstractMap.MOPHashEntry[newCapacity];

				while (map.putIndex != -1) {
					Thread.yield();
				}

				MOPAbstractMap.MOPHashEntry oldEntries[] = map.data;
				MOPAbstractMap.MOPHashEntry newEntries[] = map.newdata;

				for (int i = oldCapacity - 1; i >= 0; i--) {
					MOPAbstractMap.MOPHashEntry entry = oldEntries[i];
					if (entry != null) {
						oldEntries[i] = null;
						do {
							MOPAbstractMap.MOPHashEntry next = entry.next;
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
	
	protected void maintainMultiMapNode(MOPMultiMapNode map) {
		cleanup(map);
		if (map.addedMappings - map.deletedMappings >= map.datathreshold) {
			int oldCapacity = map.data.length;
			int newCapacity = oldCapacity * 2;
			if (newCapacity <= map.MAXIMUM_CAPACITY) {
				map.newdata = new MOPMultiMapNode.MOPHashEntry[newCapacity];

				while (map.putIndex != -1) {
					Thread.yield();
				}

				MOPMultiMapNode.MOPHashEntry oldEntries[] = map.data;
				MOPMultiMapNode.MOPHashEntry newEntries[] = map.newdata;

				for (int i = oldCapacity - 1; i >= 0; i--) {
					MOPMultiMapNode.MOPHashEntry entry = oldEntries[i];
					if (entry != null) {
						oldEntries[i] = null;
						do {
							MOPMultiMapNode.MOPHashEntry next = entry.next;
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

	protected void maintainMultiMapOfMap(MOPMultiMapOfMap map) {
		cleanup(map);
		if (map.addedMappings - map.deletedMappings >= map.datathreshold) {
			int oldCapacity = map.data.length;
			int newCapacity = oldCapacity * 2;
			if (newCapacity <= map.MAXIMUM_CAPACITY) {
				map.newdata = new MOPMultiMapOfMap.MOPHashEntry[newCapacity];

				while (map.putIndex != -1) {
					Thread.yield();
				}

				MOPMultiMapOfMap.MOPHashEntry oldEntries[] = map.data;
				MOPMultiMapOfMap.MOPHashEntry newEntries[] = map.newdata;

				for (int i = oldCapacity - 1; i >= 0; i--) {
					MOPMultiMapOfMap.MOPHashEntry entry = oldEntries[i];
					if (entry != null) {
						oldEntries[i] = null;
						do {
							MOPMultiMapOfMap.MOPHashEntry next = entry.next;
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
						maintainMultiMapNode(mopMultiMap);
						mopMultiMap.lastsize = (int) (mopMultiMap.addedMappings - mopMultiMap.deletedMappings);
						map.isCleaning = false;
					} else if(map instanceof MOPMultiMapOfMap){
						MOPMultiMapOfMap mopMultiMap = (MOPMultiMapOfMap) map;
						maintainMultiMapOfMap(mopMultiMap);
						mopMultiMap.lastsize = (int) (mopMultiMap.addedMappings - mopMultiMap.deletedMappings);
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
