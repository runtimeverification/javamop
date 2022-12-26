package com.runtimeverification.rvmonitor.java.rt.tablebase;

public abstract class AbstractSynchronizedMonitor extends AbstractMonitor {
	protected boolean RVM_terminated;
	protected int RVM_lastevent = -1;
	
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
	
	@Override
	public final int getLastEvent() {
		return this.RVM_lastevent;
	}
}
