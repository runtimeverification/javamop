package com.runtimeverification.rvmonitor.java.rt.tablebase;

import com.runtimeverification.rvmonitor.java.rt.ref.CachedWeakReference;
import com.runtimeverification.rvmonitor.java.rt.tablebase.annotation.ThreadSafety;
import com.runtimeverification.rvmonitor.java.rt.tablebase.annotation.ThreadSafety.Safety;

/**
 * This class represents a bucket in WeakRefHashMap.
 * 
 * Previously, a bucket was implemented as a linked list. To improve
 * performance, I replaced it with a circular queue, which can be adjusted
 * at runtime.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
final class Bucket<TWeakRef extends CachedWeakReference, TValue extends IIndexingTreeValue> {
	private final int treeid;

	/*
	 * These two fields keep all the pairs of key and value. It is crucial
	 * to keep them synchronized; i.e., keys[i] and values[i] are related.
	 * It is more readable to make a pair structure, but, unlike C++ or C#, where
	 * a pair can be efficiently managed, Java does not provide any means.
	 * Rather than relying on JIT optimizations, I decided to keep two separate
	 * fields.
	 * Since it is impossible to create a generic array, the types are not strict;
	 * I already miss C++ a lot.
	 */
	private CachedWeakReference[] keys;
	private IIndexingTreeValue[] values;
	
	private int head;
	private int tail;

	private int count;
	
	// Some specifications are never or very rarely used. So,
	// allocating a big number would result in waste of memory.
	private static final int INITIAL_CAPACITY = 4;
	private static final int LOAD_THRESHOLD = 16;
	
	public boolean isSaturated() {
		return this.keys.length >= LOAD_THRESHOLD && this.isFull();
	}
	
	private boolean isFull() {
		return this.count == this.keys.length - 1;
	}
	
	public int getCapacity() {
		return this.keys == null ? 0 : this.keys.length;
	}
	
	public int size() {
		return this.count;
	}
	
	Bucket(int treeid, int capacity) {
		this.treeid = treeid;
		if (capacity > 0) {
			this.resize(capacity);
		}
	}
	
	@ThreadSafety(safety=Safety.UNSAFE)
	private void resize(int newsize) {
		CachedWeakReference[] newkeys = new CachedWeakReference[newsize];
		IIndexingTreeValue[] newvals = new IIndexingTreeValue[newsize];
		
		int oldsize = 0;

		if (this.keys != null) {
			oldsize = this.keys.length;
			System.arraycopy(this.keys, 0, newkeys, 0, oldsize);
			System.arraycopy(this.values, 0, newvals, 0, oldsize);
		}

		this.keys = newkeys;
		this.values = newvals;
		
		this.head = 0;
		this.tail = oldsize;
	}
	
	private void expand() {
		int newsize = this.keys.length * 2;
		this.resize(newsize);
	}
	
	private final boolean tryOverwrite(TWeakRef key, TValue value) {
		int lastindex = -1;
		for (int i = this.head; i != this.tail; i = (i + 1) & (this.keys.length - 1)) {
			CachedWeakReference ekey = this.keys[i];
			IIndexingTreeValue eval = this.values[i];
			if (this.terminateIfReclaimed(ekey, eval)) {
				this.keys[i] = null;
				this.values[i] = null;

				lastindex = i;
				this.count--;
			}
			else
				break;
		}
		
		if (lastindex == -1)
			return false;
		
		this.keys[lastindex] = key;
		this.values[lastindex] = value;
		this.count++;

		this.head = lastindex;
		return true;
	}
	
	@ThreadSafety(safety=Safety.UNSAFE)
	public final void add(TWeakRef key, TValue value) {
		if (this.keys == null)
			this.resize(INITIAL_CAPACITY);
		
		if (!this.tryOverwrite(key, value)) {
			this.keys[this.tail] = key;
			this.values[this.tail] = value;
			this.count++;
			
			this.tail = (this.tail + 1) & (this.keys.length - 1);

			if (this.head == this.tail)
				this.expand();
			
			if (this.isFull())
				this.cleanUpUnnecessaryMappings();
		}
	}

	@ThreadSafety(safety=Safety.UNSAFE)
	@SuppressWarnings("unchecked")
	public final boolean updateOrAdd(TupleTrait<TValue> trait, TWeakRef key, TValue value, boolean additive, int valueflag) {
		for (int i = this.head; i != this.tail; i = (i + 1) & (this.keys.length - 1)) {
			CachedWeakReference wref = this.keys[i];
			if (key == wref) {
				if (additive)
					trait.set((TValue)this.values[i], value, valueflag);
				else
					this.values[i] = value;
				return false;
			}
		}
		
		this.add(key, value);
		return true;
	}

	/**
	 * Finds the weak reference that corresponds to the given strong reference.
	 * @param key strong reference
	 * @return weak reference corresponds to the given strong reference
	 */
	@ThreadSafety(safety=Safety.UNSAFE)
	@SuppressWarnings("unchecked")
	public TWeakRef findWeakRef(Object key) {
		for (int i = this.head; i != this.tail; i = (i + 1) & (this.keys.length - 1)) {
			CachedWeakReference wref = this.keys[i];
			if (key == wref.get())
				return (TWeakRef)wref;
		}
		return null;
	}

	/**
	 * Finds the value associated with the given weak reference.
	 * @param key weak reference
	 * @return value associated with the weak reference
	 */
	@ThreadSafety(safety=Safety.UNSAFE)
	@SuppressWarnings("unchecked")
	public TValue findByWeakRef(TWeakRef key) {
		for (int i = this.head; i != this.tail; i = (i + 1) & (this.keys.length - 1)) {
			CachedWeakReference wref = this.keys[i];
			if (key == wref)
				return (TValue)this.values[i];
		}
		return null;
	}

	/**
	 * Finds the value associated with the given strong reference.
	 * @param key strong reference
	 * @return value associated with the strong reference
	 */
	@ThreadSafety(safety=Safety.UNSAFE)
	@SuppressWarnings("unchecked")
	public TValue findByStrongRef(Object key) {
		for (int i = this.head; i != this.tail; i = (i + 1) & (this.keys.length - 1)) {
			CachedWeakReference wref = this.keys[i];
			if (key == wref.get())
				return (TValue)this.values[i];
		}
		return null;
	}
	
	/**
	 * Removes reclaimed elements from this bucket.
	 * When removing an element, it also terminates the value.
	 * This should be synchronized by the caller.
	 * @param from from
	 * @param to to
	 * @param numalive number of alive monitors
	 * @return number of alive monitors
	 */
	private int checkAndTerminate(int from, int to, int numalive) {
		for (int i = from; i < to; ++i) {
			CachedWeakReference key = this.keys[i];
			IIndexingTreeValue val = this.values[i];
			
			boolean alive = false;
			if (key != null && key.get() != null) {
				this.keys[numalive] = key;
				this.values[numalive] = val;
				alive = true;
			}

			if (alive)
				++numalive;
			else {
				// Putting null here seems unnecessary.
				this.keys[i] = null;
				if (val != null) {
					if (this.treeid != -1)
						val.terminate(this.treeid);
					this.values[i] = null;
				}
			}
		}
		return numalive;
	}
	
	/**
	 * Zeroes elements in the given range.
	 * @param from from
	 * @param to to
	 */
	private void zeroElement(int from, int to) {
		for (int i = from; i < to; ++i) {
			this.keys[i] = null;
			this.values[i] = null;
		}
	}

	/**
	 * Removes all the reclaimed elements from this bucket, and
	 * shifts elements, so that all alive elements are located
	 * at the beginning of the list without any space. This method
	 * also terminates values when it removes elements.
	 * @return number of removed elements
	 */
	@ThreadSafety(safety=Safety.UNSAFE)
	public final int cleanUpUnnecessaryMappings() {
		int removed;
		int numalive = 0;
		
		int oldhead = this.head;
		int oldtail = this.tail;
		
		if (oldhead == oldtail) {
			removed = 0;
			numalive = 0;
		}
		else if (oldhead < oldtail) {
			numalive = this.checkAndTerminate(oldhead, oldtail, numalive);
			this.zeroElement(numalive, oldtail);
			removed = oldtail - oldhead - numalive;
		}
		else {
			numalive = this.checkAndTerminate(0, oldtail, numalive);
			numalive = this.checkAndTerminate(oldhead, this.keys.length, numalive);
			this.zeroElement(numalive, oldtail);
			this.zeroElement(Math.max(oldhead, numalive), this.keys.length);
			removed = oldtail + (this.keys.length - oldhead) - numalive;
		}

		this.head = 0;
		this.tail = numalive;
		this.count = numalive;
		
		return removed;
	}

	public final boolean terminateIfReclaimed(CachedWeakReference key, IIndexingTreeValue val) {
		if (key.get() == null) {
			if (this.treeid != -1) {
				if (val != null)
					val.terminate(this.treeid);
			}
			return true;
		}
		return false;
	}

	public final void terminateValues() {
		for (int i = this.head; i != this.tail; i = (i + 1) & (this.keys.length - 1)) {
			IIndexingTreeValue val = this.values[i];
			if (this.treeid != -1) {
				if (val != null)
					val.terminate(this.treeid);
			}
		}
	}
	
	public PairIterator iterator() {
		return new PairIterator();
	}
	
	/**
	 * Iterator<E> cannot be used because we do not have any notion of pair, because
	 * storing keys and values as pairs probably causes unnecessary overhead.
	 * Also, this class follows C# convention: MoveNext() and Current, because I
	 * believe this is more efficient and intuitive than Java's convention.
	 * Although this should be efficient, this is only for other classes. The enclosing
	 * class never relies on this iterator.
	 */
	public class PairIterator {
		private int i;
		
		public PairIterator() {
			this.i = -1;
		}
		
		public boolean moveNext() {
			if (this.i == -1)
				this.i = Bucket.this.head;
			else
				this.i = (this.i + 1) & (Bucket.this.keys.length - 1);
			return this.i != Bucket.this.tail;
		}
		
		@SuppressWarnings("unchecked")
		public TWeakRef getKey() {
			return (TWeakRef)Bucket.this.keys[this.i];
		}
		
		@SuppressWarnings("unchecked")
		public TValue getValue() {
			return (TValue)Bucket.this.values[this.i];
		}
	}
	
	@Override
	public String toString() {
		if (this.head == this.tail)
			return "<empty>";
		
		StringBuilder s = new StringBuilder();
		for (int i = this.head; i != this.tail; i = (i + 1) & (this.keys.length - 1)) {
			CachedWeakReference key = this.keys[i];
			IIndexingTreeValue val = this.values[i];
			if (key != null) {
				s.append('<');
				s.append(key);
				s.append(':');
				s.append(val);
				s.append("> ");
			}
		}
		return s.toString();
	}
	
	public void printStatistics() {
		StringBuilder s = new StringBuilder();
		s.append("\t");
		s.append("size: " + this.count);
		s.append(", capacity: " + (this.keys == null ? 0 : this.keys.length));
		System.out.println(s);
	}
}
