package com.runtimeverification.rvmonitor.java.rt.tablebase;

public abstract class AbstractAtomicMonitor extends AbstractMonitor {
	protected volatile boolean RVM_terminated;
	
	@Override
	public final void terminate(int treeid) {
		if (!this.RVM_terminated) {
			this.terminateInternal(treeid);
			this.RVM_terminated = true;
		}
	}
	
	@Override
	public final boolean isTerminated() {
		return this.RVM_terminated;
	}
}
