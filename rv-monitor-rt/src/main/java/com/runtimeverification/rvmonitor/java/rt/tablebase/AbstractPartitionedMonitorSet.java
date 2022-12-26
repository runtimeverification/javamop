package com.runtimeverification.rvmonitor.java.rt.tablebase;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import com.runtimeverification.rvmonitor.java.rt.observable.IObservable;
import com.runtimeverification.rvmonitor.java.rt.observable.ISetBehaviorObserver;
import com.runtimeverification.rvmonitor.java.rt.observable.ObserverSettings;
import com.runtimeverification.rvmonitor.java.rt.observable.SetBehaviorObserver;

/**
 * This class represents a set of monitors. By default, a set of monitors
 * was kept in an array, which is not proper when there are many monitors.
 *
 * This class considers that there can be huge number of monitors in a set
 * and, therefore, keeps a list of arrays. Each array is called a 'node' in
 * this class.
 * 
 * More importantly, this class considers that, when an event occurs,
 * only certain monitors are affected and all the other monitors do not make
 * any transition. Based on this assumption, this class partitions monitors
 * into several disjoint subsets, each of which is called a 'slot' in this
 * class. This class can be thought of as a map from a pair of the last event
 * and the state of a monitor to a set of monitors.
 * 
 * One may think of this class as a container for holding disjoint subsets,
 * but this is untrue. One monitor may belong to the special invalidated
 * partition and another partition. Also, we do not need the find operation, which
 * is common to general purpose containers.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 * @see AbstractMonitorSet
 * 
 * @param <TMonitor> type of the monitor
 */
public abstract class AbstractPartitionedMonitorSet<TMonitor extends IMonitor> implements IMonitorSet, IObservable<ISetBehaviorObserver<AbstractPartitionedMonitorSet<?>>> {
	private final int numEvents;
	private final Slot<TMonitor>[] slots;
	private final Set<TMonitor> invalidated;
	
	private ISetBehaviorObserver<AbstractPartitionedMonitorSet<?>> observer;
	
	/**
	 * This class and this constructor assumes that the monitor states and
	 * the event identifiers start from zero and are consecutive. At the
	 * time of writing this class, that assumption seems correct.
	 * @param numStates the number of states
	 * @param numEvents the number of events
	 */
	@SuppressWarnings("unchecked")
	protected AbstractPartitionedMonitorSet(int numStates, int numEvents) {
		if (numEvents == 0)
			throw new IllegalArgumentException();

		this.numEvents = numEvents;
		this.slots = new Slot[2 + numStates * numEvents];
		this.invalidated = new HashSet<TMonitor>();
		
		if (ObserverSettings.observeSetBehavior)
			this.observer = SetBehaviorObserver.nil();
	}
	
	public final Set<TMonitor> getInvalidatedMonitors() {
		return this.invalidated;
	}

	private static final int PREMATURE_SLOT_INDEX = 0;
	private static final int ORDINARY_SLOT_BEGINNING_INDEX = 1;

	public final Slot<TMonitor> getPrematureMonitors() {
		return this.slots[PREMATURE_SLOT_INDEX];
	}
	
	private final int getSlotIndex(int state, int lastevt) {
		if (lastevt == -1)
			return -1;
	
		// 0: premature
		// 1 ~ : others
		return state * this.numEvents + lastevt + ORDINARY_SLOT_BEGINNING_INDEX;
	}
	
	public final int getSlotIndex(TMonitor m) {
		return this.getSlotIndex(m.getState(), m.getLastEvent());
	}
	
	/**
	 * Relocates invalidated monitors to their correct slots.
	 * This method assumes that an invalidated monitor always belongs to
	 * either the premature slot or one of ordinary slots.
	 * When I ran the implementation on 'avrora' from the DaCapo 9.12,
	 * it turned out that this method did not have to do anything for most cases. 
	 * Among 10,556,197 calls, only 7,724 calls resulted in any relocation, and
	 * most of them involved only one monitor. This statistics implies that
	 * it is very likely that there are only a few invalidated monitors.
	 */
	public final void arrange() {
		int numInvalidated = this.invalidated.size();
		@SuppressWarnings("unused")
		int numSearchedSlots = 0;
		@SuppressWarnings("unused")
		int numSearchedNodes = 0;
		
		if (invalidated.size() > 0) {
			for (SetIterator i = this.iterator(true); i.moveNext() && invalidated.size() > 0; ) {
				++numSearchedSlots;
				
				// Since we add new elements and recently added monitors are likely to be
				// modified, we iterate over the elements in the reverse order.
				Slot<TMonitor> slot = i.getSlot();
				try {
					slot.acquireLock();
					Iterator<Node<TMonitor>> j = slot.list.descendingIterator();
					while (j.hasNext() && invalidated.size() > 0) {
						Node<TMonitor> node = j.next();
						node.removeIfContained(invalidated, null, this, i.getIndex());
						
						++numSearchedNodes;
					}
				}
				finally {
					slot.releaseLock();
				}
			}

			this.invalidated.clear();
		}

		if (ObserverSettings.observeSetBehavior)
			this.observer.onSetArranged(this, numInvalidated, numSearchedSlots, numSearchedNodes);
	}
	
	public final void invalidate(TMonitor m) {
		this.invalidated.add(m);
		
		if (ObserverSettings.observeSetBehavior)
			this.observer.onSetMonitorInvalidated(this, m);
	}
	
	protected void addUnprotected(TMonitor m) {
		int index = this.getSlotIndex(m);
		if (index == -1)
			index = PREMATURE_SLOT_INDEX;
		this.addToSlot(m, index);
		
		if (ObserverSettings.observeSetBehavior)
			this.observer.onSetMonitorAdded(this, m);
	}
	
	void addToSlot(TMonitor m, int index) {
		if (this.slots[index] == null)
			this.slots[index] = new Slot<TMonitor>();
		
		Slot<TMonitor> slot = this.slots[index];
		try {
			slot.acquireLock();
			slot.add(m);
		}
		finally {
			slot.releaseLock();
		}
	}
	
	static class TransitionTable {
		private int[] table;
		private int tablesize;
		
		public int size() {
			return this.tablesize;
		}
		
		public void resize(int size) {
			if (this.table == null || this.table.length < size) {
				this.table = new int[size];
				this.tablesize = size;
				for (int i = 0; i < this.table.length; ++i)
					this.table[i] = -1;
			}
			else
				this.tablesize = size;
		}
		
		public void mark(int current, int next) {
			this.table[current] = next;

			if (current == next) {
				// This should not happen because the caller checks whether or
				// not transition is needed.
				throw new IllegalStateException();
			}
		}
		
		public int getAndClear(int current) {
			int ret = this.table[current];
			this.table[current] = -1;
			return ret;
		}
	}
	
	private static final ThreadLocal<TransitionTable> transitionTable = new ThreadLocal<TransitionTable>() {
		@Override
		protected TransitionTable initialValue() {
			return new TransitionTable();
		}
	};
	
	/**
	 * Creates a transition table. Previously, I had created an ephemeral
	 * integer array and returned it to the caller, but it turned out that
	 * imposes too much memory overhead. I believe Java fails to optimize
	 * it---such as allocating at the stack---and reclaim it in a timely
	 * manner. Not being able to rely on JVM's optimization, I decided to create
	 * the table at the TLS. Because of this, this method and the two other
	 * coexisting methods should be invoked by the same thread.
	 * @see markSlotTransition
	 * @see moveSlots
	 */
	void createSlotTransitionTable() {
		transitionTable.get().resize(this.slots.length);
	}

	/**
	 * Marks the next slot index at the transition table.
	 * As mentioned in createSlotTransitionTable(), this method
	 * should be invoked by the thread that has invoked
	 * createSlotTransitionTable().
	 * @see createSlotTransitionTable
	 * @see moveSlots
	 */
	void markSlotTransition(SetIterator it, TMonitor monitor) {
		int curslot = it.getIndex();
		int nextslot = this.getSlotIndex(monitor);
		transitionTable.get().mark(curslot, nextslot);
	}
	
	/**
	 * Moves slots according to the transition table.
	 * As mentioned in createSlotTransitionTable(), this method
	 * should be invoked by the thread that has invoked
	 * createSlotTransitionTable().
	 * @see createSlotTransitionTable
	 * @see markSlotTransition
	 */
	void moveSlots() {
		TransitionTable tbl = transitionTable.get();
		for (int idonor = 0; idonor < tbl.size(); ++idonor) {
			int ireceiver = tbl.getAndClear(idonor);
			if (ireceiver == -1)
				continue;
			Slot<TMonitor> donor = this.slots[idonor];
			Slot<TMonitor> receiver = this.slots[ireceiver];
	
			if (receiver == null)
				receiver = this.slots[ireceiver] = new Slot<TMonitor>();
			
			try {
				donor.acquireLock();
				receiver.acquireLock();
				receiver.receive(donor);
			}
			finally {
				donor.releaseLock();
				receiver.releaseLock();
			}
			
			if (ObserverSettings.observeSetBehavior)
				this.observer.onSetTransitioned(this, ireceiver, idonor);
		}
	}
	
	/**
	 * Terminates all the monitors in this set. This method is to be invoked
	 * by any subclass's terminate(), which is declared in the IIndexingTreeValue
	 * interface.
	 * @param treeid tree id
	 */
	protected final void terminateInternal(int treeid) {
		for (SetIterator i = this.iterator(true); i.moveNext(); ) {
			Slot<TMonitor> slot = i.getSlot();
			try {
				slot.acquireLock();
				slot.terminate(treeid);
			}
			finally {
				slot.releaseLock();
			}
		}
	}
	
	/**
	 * Removes terminated monitors from slots. This method is to be invoked
	 * by a monitor cleaning thread, such as a TerminatedMonitorCleaner instance.
	 * To prevent the cleaning thread from blocking the worker thread, this method
	 * should not dominate the critical section. Currently, this requirement is
	 * implemented by allowing a worker thread to interrupt the cleaning thread.
	 * As a result, this method periodically should check whether or not this thread
	 * has been interrupted.
	 */
	public final int removeTerminatedMonitors() {
		int removed = 0;
		
		for (SetIterator i = this.iterator(true); i.moveNext(); ) {
			Slot<TMonitor> slot = i.getSlot();
			boolean acquired =  false;
			try {
				acquired = slot.tryAcquireLock();
				if (acquired)
					removed += slot.removeTerminatedMonitors();
			}
			finally {
				if (acquired)
					slot.releaseLock();
			}
		}
		
		return removed;
	}
	
	public SetIterator iterator(boolean includeSpecialSlots) {
		return new SetIterator(includeSpecialSlots);
	}
	
	public class SetIterator {
		private final boolean includeSpecialSlots;
		private int slotindex;

		SetIterator(boolean includeSpecialSlots) {
			this.includeSpecialSlots = includeSpecialSlots;
			this.slotindex = -1;
		}
		
		public boolean moveNext() {
			if (this.slotindex == -1)
				this.slotindex = this.includeSpecialSlots ? 0 : ORDINARY_SLOT_BEGINNING_INDEX;
			else
				this.slotindex++;
			
			for ( ; this.slotindex < AbstractPartitionedMonitorSet.this.slots.length; ++this.slotindex) {
				Slot<TMonitor> slot = AbstractPartitionedMonitorSet.this.slots[this.slotindex];
				if (slot != null)
					return true;
			}

			return false;
		}
		
		public Slot<TMonitor> getSlot() {
			return AbstractPartitionedMonitorSet.this.slots[this.slotindex];
		}
		
		public int getIndex() {
			return this.slotindex;
		}
	}
	
	public MonitorIterator monitorIterator(boolean includeSpecialSlots) {
		return new MonitorIterator(includeSpecialSlots);
	}
	
	public class MonitorIterator {
		private SetIterator iterset;
		private Slot<TMonitor>.MonitorIterator iterslot;
	
		MonitorIterator(boolean includeSpecialSlots) {
			this.iterset = new SetIterator(includeSpecialSlots);
		}
		
		public boolean moveNext() {
			if (this.iterslot != null && this.iterslot.moveNext())
				return true;

			while (this.iterset.moveNext()) {
				Slot<TMonitor> slot = this.iterset.getSlot();
				this.iterslot = slot.monitorIterator();
				if (this.iterslot.moveNext())
					return true;
			}
			return false;
		}
		
		public TMonitor getMonitor() {
			return this.iterslot.getMonitor();
		}
		
		public int getSlotIndex() {
			return this.iterset.getIndex();
		}
	}

	@Override
	public void subscribe(ISetBehaviorObserver<AbstractPartitionedMonitorSet<?>> observer) {
		this.observer = observer;
	}

	@Override
	public void unsubscribe(ISetBehaviorObserver<AbstractPartitionedMonitorSet<?>> observer) {
		if (this.observer == null)
			throw new IllegalArgumentException();
		this.observer = null;
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (SetIterator i = this.iterator(true); i.moveNext(); ) {
			s.append('[');
			{
				int index = i.getIndex();
				switch (index) {
				case PREMATURE_SLOT_INDEX:
					s.append("premature");
					break;
				default:
					s.append(i.getIndex());
					break;
				}
			}
			s.append("]: ");

			Slot<TMonitor> slot = i.getSlot();
			s.append(slot.toString());
			s.append("\n");
		}
		return s.toString();
	}
	
	public static class Slot<TMonitor extends IMonitor> implements Iterable<TMonitor> {
		private static final int DEFAULT_NODE_CAPACITY = 32;
		
		private final LinkedList<Node<TMonitor>> list;
		private final ReentrantLock lock;
		
		Slot() {
			this.list = new LinkedList<Node<TMonitor>>();
			this.lock = new ReentrantLock();
		}

		final void acquireLock() {
			if (!this.lock.tryLock()) {
				Thread cleaner = TerminatedMonitorCleaner.getThread();
				cleaner.interrupt();
				this.lock.lock();
			}
		}
		
		final boolean tryAcquireLock() {
			return this.lock.tryLock();
		}
	
		final void releaseLock() {
			this.lock.unlock();
		}

		void add(TMonitor m) {
			Node<TMonitor> lastnode = this.list.peekLast();
			if (lastnode == null || lastnode.isFull()) {
				lastnode = new Node<TMonitor>(DEFAULT_NODE_CAPACITY);
				this.list.addLast(lastnode);
			}
			
			lastnode.add(m);
		}
		
		void clear() {
			this.list.clear();
		}

		void receive(Slot<TMonitor> donor) {
			// The last nodes may not be full. In fact, it's very likely that
			// the last node contains only a few elements. Instead of appending
			// almost empty nodes to this list, we merge the last nodes of this
			// list and the donor list.
			Node<TMonitor> rcvlast = this.list.peekLast();
			Node<TMonitor> dnrlast = donor.list.peekLast();
			
			if (rcvlast == null || dnrlast == null)
				this.list.addAll(donor.list);
			else {
				this.list.removeLast();
				donor.list.removeLast();
				this.list.addAll(donor.list);

				for (Node<TMonitor>.NodeUnabridgedIterator i = dnrlast.unabridgedIterator(); i.moveNext(); ) {
					if (rcvlast.isFull()) {
						this.list.add(rcvlast);
						rcvlast = new Node<TMonitor>(DEFAULT_NODE_CAPACITY);
					}
					rcvlast.add(i.getMonitor());
				}
				this.list.add(rcvlast);
			}
			donor.clear();
		}
		
		public void terminate(int treeid) {
			for (Iterator<Node<TMonitor>> i = this.list.iterator(); i.hasNext(); ) {
				Node<TMonitor> node = i.next();
				node.terminateValues(treeid);
			}
		}
		
		/**
		 * Removes terminated monitors from every node in this slot. This method
		 * should be invoked by a cleaner thread, such as TerminatedMonitorCleaner.
		 * Not to interfere with worker threads, this method checks whether or not
		 * this thread has been interrupted and, if interrupted, returns immediately.
		 */
		final int removeTerminatedMonitors() {
			int removed = 0;

			for (Iterator<Node<TMonitor>> i = this.list.iterator(); i.hasNext(); ) {
				Node<TMonitor> node = i.next();
				removed += node.removeTerminatedMonitors();
				if (node.size() == 0)
					i.remove();
				
				if (Thread.interrupted())
					break;
			}
			
			return removed;
		}
		
		public MonitorIterator monitorIterator() {
			return new MonitorIterator();
		}

		public class MonitorIterator {
			private final Iterator<Node<TMonitor>> iterslot;
			private Node<TMonitor>.NodeIterator iternode;
			
			MonitorIterator() {
				this.iterslot = Slot.this.list.iterator();
			}
	
			public boolean moveNext() {
				if (this.iternode != null && this.iternode.moveNext())
					return true;

				while (this.iterslot.hasNext()) {
					Node<TMonitor> node = this.iterslot.next();
					this.iternode = node.iterator();
					if (this.iternode.moveNext())
						return true;
				}
				return false;
			}
			
			public TMonitor getMonitor() {
				return this.iternode.getMonitor();
			}
		}

		@Override
		public String toString() {
			StringBuilder s = new StringBuilder();
			for (Node<TMonitor> node : this.list) {
				if (s.length() > 0)
					s.append(" -> ");
				s.append('[');
				s.append(node);
				s.append(']');
			}
			return s.toString();
		}

		@Override
		public Iterator<TMonitor> iterator() {
			return new JavaStyleIterator();
		}
		
		/**
		 * This class implements Java-style iterator. The sole purpose of
		 * this class is to support for-each loops in test cases. It is not
		 * recommended to use this class for other purposes.
		 */
		class JavaStyleIterator implements Iterator<TMonitor> {
			private final MonitorIterator it;
			private boolean hasnext;
			
			public JavaStyleIterator() {
				this.it = new MonitorIterator();
				this.hasnext = this.it.moveNext();
			}
			
			@Override
			public boolean hasNext() {
				return this.hasnext;
			}

			@Override
			public TMonitor next() {
				if (!this.hasnext)
					throw new UnsupportedOperationException();
				TMonitor ret = this.it.getMonitor();
				this.hasnext = this.it.moveNext();
				return ret;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		}
	}

	static class Node<TMonitor extends IMonitor> {
		private final IMonitor[] elements;
		private int head;
		private int tail;
		private int count;
	
		public boolean isFull() {
			return this.count == this.elements.length - 1;
		}
		
		public int size() {
			return this.count;
		}
		
		Node(int capacity) {
			this.elements = new IMonitor[capacity];
		}
		
		public void add(TMonitor m) {
			assert !this.isFull();

			this.elements[this.tail] = m;
			this.count++;
			
			this.tail = (this.tail + 1) & (this.elements.length - 1);
		}
		
		public int removeIfContained(Set<TMonitor> remove, Set<TMonitor> donotcopy, AbstractPartitionedMonitorSet<TMonitor> enclosing, int curslotindex) {
			int removed;
			int numalive = 0;
			
			int oldhead = this.head;
			int oldtail = this.tail;
			
			if (oldhead == oldtail) {
				removed = 0;
				numalive = 0;
			}
			else if (oldhead < oldtail) {
				numalive = this.checkAndRemove(oldhead, oldtail, numalive, remove, donotcopy, enclosing, curslotindex);
				this.zeroElement(numalive, oldtail);
				removed = oldtail - oldhead - numalive;
			}
			else {
				numalive = this.checkAndRemove(0, oldtail, numalive, remove, donotcopy, enclosing, curslotindex);
				numalive = this.checkAndRemove(oldhead, this.elements.length, numalive, remove, donotcopy, enclosing, curslotindex);
				this.zeroElement(numalive, oldtail);
				this.zeroElement(Math.max(oldhead, numalive), this.elements.length);
				removed = oldtail + (this.elements.length - oldhead) - numalive;
			}

			this.head = 0;
			this.tail = numalive;
			this.count = numalive;
			
			return removed;
		}

		private int checkAndRemove(int from, int to, int numalive, Set<TMonitor> remove, Set<TMonitor> donotcopy, AbstractPartitionedMonitorSet<TMonitor> enclosing, int curslotindex) {
			for (int i = from; i < to; ++i) {
				@SuppressWarnings("unchecked")
				TMonitor monitor = (TMonitor)this.elements[i];
				
				boolean alive = !remove.contains(monitor);
				if (!alive) {
					remove.remove(monitor);
					int slotindex = enclosing.getSlotIndex(monitor);
					if (curslotindex == slotindex)
						alive = true;
					else if (slotindex == -1)
						alive = true;
					else if (donotcopy == null || !donotcopy.contains(monitor))
						enclosing.addToSlot(monitor, slotindex);
				}

				if (alive)
					this.elements[numalive++] = monitor;
				else
					this.elements[i] = null;
			}
			return numalive;
		}
		
		public final int removeTerminatedMonitors() {
			int removed;
			int numalive = 0;
			
			int oldhead = this.head;
			int oldtail = this.tail;
			
			if (oldhead == oldtail) {
				removed = 0;
				numalive = 0;
			}
			else if (oldhead < oldtail) {
				numalive = this.checkAndShift(oldhead, oldtail, numalive);
				this.zeroElement(numalive, oldtail);
				removed = oldtail - oldhead - numalive;
			}
			else {
				numalive = this.checkAndShift(0, oldtail, numalive);
				numalive = this.checkAndShift(oldhead, this.elements.length, numalive);
				this.zeroElement(numalive, oldtail);
				this.zeroElement(Math.max(oldhead, numalive), this.elements.length);
				removed = oldtail + (this.elements.length - oldhead) - numalive;
			}

			this.head = 0;
			this.tail = numalive;
			this.count = numalive;

			return removed;
		}
		
		private int checkAndShift(int from, int to, int numalive) {
			for (int i = from; i < to; ++i) {
				IMonitor monitor = this.elements[i];
				
				boolean alive = false;
				if (!monitor.isTerminated()) {
					this.elements[numalive] = monitor;
					alive = true;
				}

				if (alive)
					++numalive;
			}
			return numalive;
		}
		
		private void zeroElement(int from, int to) {
			for (int i = from; i < to; ++i)
				this.elements[i] = null;
		}
		
		public final void terminateValues(int treeid) {
			for (NodeIterator i = this.iterator(); i.moveNext(); ) {
				TMonitor monitor = i.getMonitor();
				monitor.terminate(treeid);
			}
		}
		
		public NodeIterator iterator() {
			return new NodeIterator();
		}
		
		class NodeIterator {
			private int i;
			
			public NodeIterator() {
				this.i = -1;
			}

			public boolean moveNext() {
				for ( ; ; ) {
					if (this.i == -1)
						this.i = Node.this.head;
					else
						this.i = (this.i + 1) & (Node.this.elements.length - 1);
					
					if (this.i == Node.this.tail)
						return false;
					
					IMonitor monitor = Node.this.elements[this.i];
					if (!monitor.isTerminated())
						return true;
				}
			}
			
			@SuppressWarnings("unchecked")
			public TMonitor getMonitor() {
				return (TMonitor)Node.this.elements[this.i];
			}
		}
		
		private NodeUnabridgedIterator unabridgedIterator() {
			return new NodeUnabridgedIterator();
		}

		class NodeUnabridgedIterator {
			private int i;
			
			public NodeUnabridgedIterator() {
				this.i = -1;
			}

			public boolean moveNext() {
				if (this.i == -1)
					this.i = Node.this.head;
				else
					this.i = (this.i + 1) & (Node.this.elements.length - 1);
				return this.i != Node.this.tail;
			}
			
			@SuppressWarnings("unchecked")
			public TMonitor getMonitor() {
				return (TMonitor)Node.this.elements[this.i];
			}
		}
		
		@Override
		public String toString() {
			StringBuilder s = new StringBuilder();
			for (NodeUnabridgedIterator i = this.unabridgedIterator(); i.moveNext(); ) {
				if (s.length() > 0)
					s.append(", ");
				TMonitor m = i.getMonitor();
				s.append(m);
			}
			return s.toString();
		}
	}
}
