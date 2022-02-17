package com.runtimeverification.rvmonitor.java.rt.map;

//import java.lang.management.ManagementFactory;

import com.runtimeverification.rvmonitor.java.rt.RVMObject;

public class RVMMapManager extends Thread implements RVMObject {
	public static final int DEFAULT_MANAGEENT_PERIOD_NSEC = 400 * 1000;

	boolean started = false;

	int numCPU;
	boolean multicore = false;
	boolean loadSupported = false;

	int numCleaner = 0;
	protected RVMMapCleaner[] cleanerThreads;

	static public RVMCleanable treeQueueHead = null;
	static public RVMCleanable treeQueueTail = null;

	public RVMMapManager() {
		super();
		this.setDaemon(true);

		numCPU = Runtime.getRuntime().availableProcessors();
		loadSupported = false; //ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage() >= 0;
		multicore = RVMCleanable.multicore;
		
		int numCleaners = numCPU - 1;
		if(numCleaners < 1)
			numCleaners = 1;

		cleanerThreads = new RVMMapCleaner[numCleaners];
		
		treeQueueHead = new RVMMapOfMap(0);
		treeQueueTail = treeQueueHead;
		treeQueueHead.isDeleted = true;
	}

	public void run() {
		if (!multicore)
			return;

		started = true;

		int lastCleaner = 0;
		int currentCleaner = 0;
		
		cleanerThreads[0] = new RVMMapCleaner(0);
		numCleaner++;
		cleanerThreads[0].start();

		while (true) {
			RVMCleanable tempHead = treeQueueHead;

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
						if(numCleaner < numCPU - 1){
							cleanerThreads[numCleaner] = new RVMMapCleaner(numCleaner);
							cleanerThreads[numCleaner].start();
							lastCleaner = numCleaner;
							numCleaner++;
						} else {
							try {
								Thread.sleep(0, RVMMapManager.DEFAULT_MANAGEENT_PERIOD_NSEC);
							} catch (Exception e) {
								System.err.println("[RVMMapCleaner] Thread cannot sleep.");
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
						Thread.sleep(0, RVMMapManager.DEFAULT_MANAGEENT_PERIOD_NSEC);
					} catch (Exception e) {
						System.err.println("[RVMMapCleaner] Thread cannot sleep.");
					}
				}
			}

			if (!tempHead.repeat) {
				treeQueueHead = tempHead.nextInQueue;
				tempHead.nextInQueue = null;
			}
		}
	}
}
