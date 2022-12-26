package com.runtimeverification.rvmonitor.java.rt.tablebase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class implements a thread that removes terminated monitors from
 * a partitioned set, an instance of AbstractPartitionedSet.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class TerminatedMonitorCleaner {
	private static List<AbstractPartitionedMonitorSet<?>> addedEntries = Collections.synchronizedList(new ArrayList<AbstractPartitionedMonitorSet<?>>());
	private static List<AbstractPartitionedMonitorSet<?>> removedEntries = Collections.synchronizedList(new ArrayList<AbstractPartitionedMonitorSet<?>>());
	private static final Runner runner;
	
	static {
		runner = new Runner();
	}
	
	public static void addSet(AbstractPartitionedMonitorSet<?> set, ReentrantLock lock) {
		addedEntries.add(set);
	}
	
	public static void removeSet(AbstractPartitionedMonitorSet<?> set) {
		removedEntries.add(set);
	}
	
	public static void start() {
        if (!runner.isAlive())
		    runner.start();
	}
	
	public static Thread getThread() {
		return runner;
	}
	
	static class Runner extends Thread {
		private final Set<AbstractPartitionedMonitorSet<?>> queue;
		/**
		 * Specifies how long this thread should sleep. This will be adjusted
		 * based on the number of removed monitors.
		 * @see adjustSleepTime()
		 */
		private long sleepTime = 1;
		
		Runner() {
			this.queue = new HashSet<AbstractPartitionedMonitorSet<?>>();
			this.setName("MonitorCleaner");
			this.setDaemon(true);
		}
		
		@Override
		public void run() {
			try {
				for ( ; ; ) {
					Thread.sleep(this.sleepTime);
					this.updateEntries();
					this.doOneIteration();
				}
			}
			catch (InterruptedException e) {
			}
		}
		
		private void updateEntries() {
			synchronized (removedEntries) {
				for (AbstractPartitionedMonitorSet<?> set : removedEntries)
					this.queue.remove(set);
				removedEntries.clear();
			}

			synchronized (addedEntries) {
				for (AbstractPartitionedMonitorSet<?> set : addedEntries)
					this.queue.add(set);
				addedEntries.clear();
			}
		}
	
		private final void doOneIteration() {
			long starts = System.nanoTime();
			int removed = 0;
			for (AbstractPartitionedMonitorSet<?> set : this.queue)
				removed += this.handleOneSet(set);
			long elapsednano = System.nanoTime() - starts;
			long elapsed = elapsednano / 1000000;

			this.adjustSleepTime(removed, elapsed);
		}
		
		private final void adjustSleepTime(int removed, long elapsed) {
			int max = 10;
			int min = 1;

			long oldvalue = this.sleepTime;
			long newvalue;
			if (removed == 0) {
				if (oldvalue == 0)
					newvalue = min;
				else
					newvalue = Math.min(elapsed * 2, max);
			}
			else {
				// The operation may need to be performed more frequently.
				newvalue = elapsed / removed;
			}
			
			this.sleepTime = newvalue;
		}
		
		private final int handleOneSet(AbstractPartitionedMonitorSet<?> set) {
			int removed = set.removeTerminatedMonitors();
			return removed;
		}
	}
}
