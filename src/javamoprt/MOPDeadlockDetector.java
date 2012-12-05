package javamoprt;

import java.lang.Thread.State;
import java.util.HashSet;
import java.util.concurrent.locks.*;

/**
 * 
 * Deadlock detection
 * 
 * */
public class MOPDeadlockDetector {
	
	private static MOPCallBack callback;

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
	public static void startDeadlockDetectionThread(HashSet<Thread> allThreads, Thread mainThread, ReentrantLock lock, MOPCallBack monitorCallback) {
		MOPDeadlockDetector.callback = monitorCallback;
		Thread deadlockDetectionThread = new Thread(new DeadlockDetector(allThreads, mainThread, lock));
		deadlockDetectionThread.start();
		
	}

	static class DeadlockDetector implements Runnable {

		private HashSet<Thread> allThreads = new HashSet<Thread>();
		private Thread mainThread;
		private ReentrantLock lock;

		public DeadlockDetector(HashSet<Thread> threads, Thread main,
				ReentrantLock lockObj) {
			this.lock = lockObj;
			while (!this.lock.tryLock()) {
				Thread.yield();
			}
			this.allThreads = threads;
			this.mainThread = main;
			this.lock.unlock();	
		}

		@Override
		public void run() {
			while (!this.allThreadsTerminated()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				boolean deadlock = true;
				while (!this.lock.tryLock()) {
					Thread.yield();
				}
				for (Thread t : this.allThreads) {
					if (t.getState() != State.WAITING
							&& t.getState() != State.BLOCKED) {
						deadlock = false;
						break;
					}
				}
				this.lock.unlock();
				if (deadlock && this.allThreads.size() != 0) {
					MOPDeadlockDetector.callback.apply();
					break;
				}
			}
		}
		
		private boolean allThreadsTerminated() {
			while (!this.lock.tryLock()) {
				Thread.yield();
			}
			for (Thread t : allThreads) {
				if (t.getState() != State.TERMINATED) {
					this.lock.unlock();
					return false;
				}
			}
			this.lock.unlock();
			return true;
		}

	}

}
