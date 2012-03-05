package javamoprt;

import java.lang.management.ManagementFactory;

public class MOPMapManager extends Thread implements MOPObject {
	protected static final int DEFAULT_MANAGEENT_PERIOD_NSEC = 400 * 1000;

	boolean started = false;

	int numCPU;
	boolean multicore = false;
	boolean loadSupported = false;

	int numCleaner = 0;
	protected MOPMapCleaner[] cleanerThreads;

	static protected MOPCleanable treeQueueHead = null;
	static protected MOPCleanable treeQueueTail = null;

	public MOPMapManager() {
		super();
		this.setDaemon(true);

		numCPU = Runtime.getRuntime().availableProcessors();
		loadSupported = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage() >= 0;
		multicore = numCPU > 1;
		//multicore = false;
		
		int numCleaners = numCPU - 1;
		if(numCleaners < 1)
			numCleaners = 1;

		cleanerThreads = new MOPMapCleaner[numCleaners];
		
		treeQueueHead = new MOPMapOfMap(0);
		treeQueueTail = treeQueueHead;
		treeQueueHead.isDeleted = true;
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
			MOPCleanable tempHead = treeQueueHead;

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
							cleanerThreads[numCleaner] = new MOPMapCleaner(numCleaner);
							cleanerThreads[numCleaner].start();
							lastCleaner = numCleaner;
							numCleaner++;
						} else {
							try {
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
}
