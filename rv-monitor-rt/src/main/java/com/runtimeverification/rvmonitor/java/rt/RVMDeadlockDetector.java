package com.runtimeverification.rvmonitor.java.rt;

import java.lang.Thread.State;
import java.util.HashSet;
import java.util.concurrent.locks.*;

/**
 * 
 * Deadlock detection
 * 
 * */
public class RVMDeadlockDetector {
	
	private static RVMCallBack callback;
	
	public static boolean startedDeadlockDetection = false;

	/**
	 * 
	 * Start a deadlock detection thread
	 * 
	 * @param allThreads
	 *            The set of all running threads
	 * @param lock
	 *            Lock object used when accessing the set of all running threads
	 * 
	 * */
	public static void startDeadlockDetectionThread(HashSet<Thread> allThreads, ReentrantLock lock, RVMCallBack monitorCallback) {
		RVMDeadlockDetector.callback = monitorCallback;
		Thread deadlockDetectionThread = new Thread(new DeadlockDetector(allThreads, lock));
		deadlockDetectionThread.start();
		
	}

	static class DeadlockDetector implements Runnable {

		private HashSet<Thread> allThreads = new HashSet<Thread>();
		private ReentrantLock lock;

		public DeadlockDetector(HashSet<Thread> threads, ReentrantLock lockObj) {
			this.lock = lockObj;
			while (!this.lock.tryLock()) {
				Thread.yield();
			}
			this.allThreads = threads;
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
				boolean needCleanup = false;
				for (Thread t : this.allThreads) {
					if (t.getState() == State.TERMINATED) {
						needCleanup = true;
						continue;
					}
					if (t.getState() != State.WAITING
							&& t.getState() != State.BLOCKED) {
						deadlock = false;
						break;
					}
				}
				if (needCleanup) {
					cleanup();
				}
				this.lock.unlock();
				if (deadlock && this.allThreads.size() != 0) {
					RVMDeadlockDetector.callback.apply();
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
		
		/**
		 * 
		 * 	Remove all terminated threads.
		 * 
		 * */
		private void cleanup() {
			HashSet<Thread> newThreads = new HashSet<Thread>();
			for (Thread t : this.allThreads) {
				if (t.getState() != State.TERMINATED) {
					newThreads.add(t);
				}
			}
			this.allThreads = newThreads;
		}

	}

}
