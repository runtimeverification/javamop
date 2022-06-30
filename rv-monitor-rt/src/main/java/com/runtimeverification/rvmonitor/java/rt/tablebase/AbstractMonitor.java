package com.runtimeverification.rvmonitor.java.rt.tablebase;

import com.runtimeverification.rvmonitor.java.rt.RVMObject;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMonitor implements IMonitor, RVMObject {
	public int monitorid = -123;
	public List<String> trace = new ArrayList<>();
	/**
	 * Terminates this monitor instance. The actual code depends on the specification and,
	 * therefore, is to be implemented in the generated code.
	 * @param treeid
	 */
	protected abstract void terminateInternal(int treeid);

	@Override
	public String toString() {
		String r = this.getClass().getSimpleName();
		r += "#";
		r += String.format("%03x", this.hashCode() & 0xFFF);
		return r;
	}
}
