package javamoprt;

import java.lang.Thread.State;
import java.util.HashSet;

/**
 * 
 * Deadlock detection
 * 
 * */
public class MOPDeadlockDetector {

	/**
	 * 
	 * Start a deadlock detection thread
	 * 
	 * @param allThreads
	 *            The set of all running threads
	 * @param mainThread
	 *            Main thread of the program
	 * @param lock
	 *            Lock object used when accessing the set of all running threads
	 * 
	 * */
	public static void startDeadlockDetectionThread(HashSet<Thread> allThreads, Thread mainThread, Object lock) {
		Thread deadlockDetectionThread = new Thread(new DeadlockDetector(allThreads, mainThread, lock));
		deadlockDetectionThread.start();
	}

	static class DeadlockDetector implements Runnable {

		private static final String DEADLOCK_DETECTED_MSG = "Deadlock detected!";
		private HashSet<Thread> allThreads = new HashSet<Thread>();
		private Thread mainThread;
		private Object lock;

		public DeadlockDetector(HashSet<Thread> threads, Thread main,
				Object lockObj) {
			this.allThreads = threads;
			this.mainThread = main;
			this.lock = lockObj;
		}

		@Override
		public void run() {
			while (mainThread.getState() != State.TERMINATED) {
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				boolean deadlock = true;
				synchronized (lock) {
					for (Thread t : this.allThreads) {
						System.out.println(t.getName() + "  " + t.getState());
						if (t.getState() != State.WAITING && t.getState() != State.BLOCKED) {
							deadlock = false;
							break;
						}
					}
				}
				if (deadlock && this.allThreads.size() != 0) {
					System.err.println(DEADLOCK_DETECTED_MSG);
					break;
				}

			}
		}

	}

}
