package javamoprt;

import java.lang.management.ManagementFactory;

import javamoprt.MOPMap.MOPHashEntry;

public class MOPMapManager extends Thread implements MOPObject {
	protected static final int DEFAULT_MANAGEENT_PERIOD_MSEC = 1;
	protected static final int DEFAULT_MANAGEENT_PERIOD_NSEC = 400000;

	boolean started = false;

	int numCPU;
	boolean multicore = false;
	boolean loadSupported = false;

	int numCleaner = 0;
	protected MOPMapCleaner[] cleanerThreads;

	static protected MOPMap treeQueueHead = null;
	static protected MOPMap treeQueueTail = null;

	public MOPMapManager() {
		super();
		this.setDaemon(true);

		numCPU = Runtime.getRuntime().availableProcessors();
		loadSupported = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage() >= 0;
		multicore = numCPU > 1;
		//multicore = false;

		treeQueueHead = new MOPMap();
		treeQueueTail = treeQueueHead;
		treeQueueHead.isDeleted = true;

		cleanerThreads = new MOPMapCleaner[numCPU];
	}

	public void run() {
		if (!multicore)
			return;

		started = true;

		int lastCleaner = 0;
		int currentCleaner = 0;
		
		cleanerThreads[0] = new MOPMapCleaner(0);
		numCleaner++;
		cleanerThreads[0].start();

		while (true) {
			MOPMap tempHead = treeQueueHead;

			if (tempHead.repeat) {
				tempHead.repeat = false;
				if (!tempHead.isDeleted) {
					if (cleanerThreads[lastCleaner].map == null) {
						cleanerThreads[lastCleaner].map = tempHead;
					} else {
						System.err.println("weird case");
					}
				}
			} else if (!tempHead.isDeleted) {
				lastCleaner = -1;
				while (lastCleaner == -1) {
					for (int i = 0; i < numCleaner; i++) {
						if (cleanerThreads[currentCleaner].map == null) {
							lastCleaner = currentCleaner;
							currentCleaner++;
							if(currentCleaner >= numCleaner)
								currentCleaner = 0;
							break;
						}
						currentCleaner++;
						if(currentCleaner >= numCleaner)
							currentCleaner = 0;
					}

					if (lastCleaner == -1) {
						if(numCleaner < numCPU){
							cleanerThreads[numCleaner] = new MOPMapCleaner(numCleaner);
							cleanerThreads[numCleaner].start();
							lastCleaner = numCleaner;
							numCleaner++;
						} else {
							try {
								//Thread.sleep(this.DEFAULT_MANAGEENT_PERIOD_MSEC);
								Thread.sleep(0, MOPMapManager.DEFAULT_MANAGEENT_PERIOD_NSEC);
							} catch (Exception e) {
								System.err.println("[MOPMapCleaner] Thread cannot sleep.");
							}
						}
					}
				}

				cleanerThreads[lastCleaner].map = tempHead;
			}

			if (tempHead.nextInQueue == null && !tempHead.repeat) {
				Thread.yield();

				while (tempHead.nextInQueue == null && !tempHead.repeat) {
					try {
						//Thread.sleep(this.DEFAULT_MANAGEENT_PERIOD_MSEC);
						Thread.sleep(0, MOPMapManager.DEFAULT_MANAGEENT_PERIOD_NSEC);
					} catch (Exception e) {
						System.err.println("[MOPMapCleaner] Thread cannot sleep.");
					}
				}
			}

			if (!tempHead.repeat) {
				treeQueueHead = tempHead.nextInQueue;
				tempHead.nextInQueue = null;
			}
		}
	}

	static protected class MOPMapCleaner extends Thread {
		int id;
		
		protected MOPMap map = null;
		protected MOPMapCleaner next = null;

		protected MOPMapCleaner(int id) {
			this.id = id;
			this.setDaemon(true);
		}

		protected void cleanup(MOPMap map) {
			int numDeleted = 0;
			if (map instanceof MOPMapOfMap) {
				MOPMapOfMap mapOfMap = (MOPMapOfMap) map;

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
						MOPMap value = (MOPMap) entry.getValue();
						if (entry.key.get() == null) {
							if (previous == null) {
								map.data[i] = entry.next;
							} else {
								previous.next = entry.next;
							}

							value.isDeleted = true;
							value.endObject(map.idnum);
							numDeleted++;
						} else if (value != mapOfMap.lastValue && value.size() == 0) {
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
			} else if (map instanceof MOPMapOfSet) {
				MOPMapOfSet mapOfSet = (MOPMapOfSet) map;

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
						} else if (value != mapOfSet.lastValue && value.size() == 0) {
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
			} else if (map instanceof MOPMapOfMonitor) {
				MOPMapOfMonitor mapOfMonitor = (MOPMapOfMonitor) map;

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
		}

		protected void maintainMap(MOPMap map) {
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
//						System.err.println("Cleaner " + id + " cleaning " + map);
						maintainMap(map);
						map.lastsize = (int) (map.addedMappings - map.deletedMappings);
						map.isCleaning = false;
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
}
