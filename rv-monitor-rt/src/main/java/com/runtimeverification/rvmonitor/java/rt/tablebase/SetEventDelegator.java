package com.runtimeverification.rvmonitor.java.rt.tablebase;

/**
 * This abstract class is used to dispatch an event to affected monitors
 * in the partitioned set. It is used solely within a partitioned set, an
 * instance of AbstractPartitionedMonitorSet.
 * 
 * A partitioned set requires somewhat complicated operations when an event
 * occurs. For this reason, instead of putting this delicate code in each
 * generated monitor set code, I have decided to put in a common place, so
 * that the generated code has only set-specific routine in commit():
 * invoking an event handling routine defined in the monitor class. This
 * must be and is implemented in the generated monitor set class. This
 * class guarantees that commit() is invoked for each affected monitor.
 * 
 * The abstract method commit() should do the followings:
 * 1. invokes the event handling routine, defined in the monitor class
 * 2. if the pattern matches, invokes the handler
 * 3. invalidates the pair map
 * 
 * When an event occurs, a partitioned set should be first arranged; i.e.,
 * all monitors in the invalidated set should be placed at the proper slots.
 * Then, for each slot, it checks if the first monitor is affected by the
 * event. If it is affected, then all the other monitors would be affected.
 * Thus, it iterates over all the monitors and, for each monitor, invokes
 * the commit() method, which is implemented by the subclass. If the first
 * monitor is unaffected, this method skips that slot.
 * 
 * @author Choonghwan Lee <clee83@illinois.edu>
 * @see AbstractPartitionedMonitorSet
 *
 * @param <TMonitor> type of a monitor
 * @param <TPairMap> type of the pair map
 */
public abstract class SetEventDelegator<TMonitor extends IMonitor, TPairMap extends AbstractIndexingTree<?, ?>> {
	public void fireEvent(AbstractPartitionedMonitorSet<TMonitor> set, TPairMap pairmap) {
		set.createSlotTransitionTable();

		set.arrange();
		AbstractPartitionedMonitorSet<TMonitor>.SetIterator i = set.iterator(true);
		while (i.moveNext()) {
			AbstractPartitionedMonitorSet.Slot<TMonitor> slot = i.getSlot();
			
			// Slot-wise lock is employed to synchronize with TerminatedMonitorCleaner.
			try {
				slot.acquireLock();

				AbstractPartitionedMonitorSet.Slot<TMonitor>.MonitorIterator j = slot.monitorIterator();
				if (!j.moveNext())
					continue;
	
				{
					TMonitor monitor = j.getMonitor();
					int lastevt = monitor.getLastEvent();
					int laststate = monitor.getState();
					this.commit(monitor, pairmap);
					if (lastevt == monitor.getLastEvent() && laststate == monitor.getState())
						continue;
	
					set.markSlotTransition(i, monitor);
				}
				
				while (j.moveNext()) {
					TMonitor monitor = j.getMonitor();
					this.commit(monitor, pairmap);
				}
			}
			finally {
				slot.releaseLock();
			}
		}
		
		set.moveSlots();
	}

	/**
	 * Handles the event. Currently, this method is implemented in the
	 * generated code.
	 * @param monitor monitor
	 * @param pairmap pair map
	 */
	protected abstract void commit(TMonitor monitor, TPairMap pairmap);
}
