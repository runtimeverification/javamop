package com.runtimeverification.rvmonitor.java.rt.tablebase;

import java.util.Arrays;

import com.runtimeverification.rvmonitor.java.rt.RVMObject;

/**
 * This class represents a set of monitors. This is the default implementation
 * of a set. If certain conditions are met, the code generator replaces this by
 * AbstractPartitionedMonitorSet, the presumably more efficient implementation.
 * 
 * This implementation is almost identical to MonitorSet, which has been there
 * since JavaMOP 3.0. The only major difference between this and MonitorSet is
 * that this class implements all the common features, whereas the old
 * implementation implements less and assumes the generated monitor set, which
 * is MonitorSet's subclass, implements the rest.
 * 
 * @author Choonghwan Lee <clee83@illinois.edu>
 * @see AbstractPartitionedMonitorSet
 * 
 * @param <TMonitor>
 *            type of the monitor
 */
public abstract class AbstractMonitorSet<TMonitor extends IMonitor> implements
		IMonitorSet, RVMObject {
	protected int size = 0;
	protected TMonitor[] elements;

	@Override
	public synchronized final void terminate(int treeid) {
		for (int i = this.size - 1; i >= 0; --i) {
			TMonitor monitor = this.elements[i];
			if (monitor != null && !monitor.isTerminated())
				monitor.terminate(treeid);
			this.elements[i] = null;
		}
	
		this.elements = null;
		this.size = 0;
	}
	
	public synchronized final void add(TMonitor e) {
		this.ensureCapacity();
		this.elements[this.size++] = e;
	}
	
	public synchronized final TMonitor get(int index) {
		return this.elements[index];
	}
	
	public synchronized final void set(int index, TMonitor monitor) {
		this.elements[index] = monitor;
	}
	
	public synchronized final int getSize() {
		return this.size;
	}
	
	public synchronized final void eraseRange(int from) {
		for (int i = from; i < this.size; ++i)
			this.elements[i] = null;
		this.size = from;
	}
	
	private final void ensureCapacity() {
		int oldCapacity = this.elements.length;
		
		if (this.size + 1 > oldCapacity)
			this.removeTerminated();

		if (this.size + 1 > oldCapacity) {
			TMonitor[] oldData = this.elements;
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if (newCapacity < this.size + 1)
				newCapacity = this.size + 1;
			this.elements = Arrays.copyOf(oldData, newCapacity);
		}
	}
	
	private final void removeTerminated() {
		int numAlive = 0;
		for (int i = 0; i < this.size; ++i) {
			TMonitor monitor = this.elements[i];
			if (!monitor.isTerminated()) {
				this.elements[numAlive] = monitor;
				numAlive++;
			}
		}
		
		this.eraseRange(numAlive);
	}

	@Override
	public String toString() {
		String r = this.getClass().getSimpleName();
		r += "#";
		r += String.format("%03x", this.hashCode() & 0xFFF);
		return r;
	}
}
