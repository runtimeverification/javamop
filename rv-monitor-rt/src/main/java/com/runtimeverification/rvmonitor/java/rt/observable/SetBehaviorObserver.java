package com.runtimeverification.rvmonitor.java.rt.observable;

import java.io.PrintWriter;

import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractPartitionedMonitorSet;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IMonitor;

public class SetBehaviorObserver {
	public static <TSender extends AbstractPartitionedMonitorSet<?>> ISetBehaviorObserver<TSender> nil() {
		return new SetBehaviorNullObserver<TSender>();
	}

	public static <TSender extends AbstractPartitionedMonitorSet<?>> ISetBehaviorObserver<TSender> dumper(PrintWriter writer) {
		return new SetBehaviorDumper<TSender>(writer);
	}
}

class SetBehaviorNullObserver<TSender extends AbstractPartitionedMonitorSet<?>> implements ISetBehaviorObserver<TSender> {
	@Override
	public void onSetMonitorAdded(TSender set, IMonitor monitor) {
	}

	@Override
	public void onSetMonitorInvalidated(TSender set, IMonitor monitor) {
	}

	@Override
	public void onSetArranged(TSender set, int numInvalidated, int numSearchedSlots, int numSearchedNodes) {
	}

	@Override
	public void onSetTransitioned(TSender set, int receiver, int donor) {
	}

	@Override
	public void onCompleted() {
	}
}

class SetBehaviorDumper<TSender extends AbstractPartitionedMonitorSet<?>> implements ISetBehaviorObserver<TSender> {
	private final Dumper dumper;
	
	public SetBehaviorDumper(PrintWriter writer) {
		this.dumper = new Dumper(writer);
	}

	@Override
	public synchronized void onSetMonitorAdded(TSender set, IMonitor monitor) {
		if (!ISetBehaviorObserver.observeAdded)
			return;
		
		this.dumper.printTitle("SetAdded");
		this.dumper.printSet(set);
		this.dumper.printSpace();
		this.dumper.printMonitor(monitor);
		this.dumper.endline();
	}

	@Override
	public synchronized void onSetMonitorInvalidated(TSender set, IMonitor monitor) {
		if (!ISetBehaviorObserver.observeInvalidated)
			return;

		this.dumper.printTitle("SetInvalidated");
		this.dumper.printSet(set);
		this.dumper.printSpace();
		this.dumper.printMonitor(monitor);
		this.dumper.endline();
	}

	@Override
	public synchronized void onSetArranged(TSender set, int numInvalidated, int numSearchedSlots, int numSearchedNodes) {
		if (!ISetBehaviorObserver.observeArranged)
			return;

		this.dumper.printTitle("SetArranged");
		this.dumper.printSet(set);
		this.dumper.printSpace();
		this.dumper.print("invalidated", numInvalidated);
		this.dumper.printSpace();
		this.dumper.print("slots", numSearchedSlots);
		this.dumper.printSpace();
		this.dumper.print("nodes", numSearchedNodes);
		this.dumper.endline();
	}

	@Override
	public synchronized void onSetTransitioned(TSender set, int receiver, int donor) {
		if (!ISetBehaviorObserver.observeTransitioned)
			return;

		this.dumper.printTitle("SetTransitioned");
		this.dumper.printSet(set);
		this.dumper.printSpace();
		this.dumper.print("reciver", receiver);
		this.dumper.printSpace();
		this.dumper.print("donor", donor);
		this.dumper.printSpace();
		this.dumper.endline();
	}

	@Override
	public void onCompleted() {
		this.dumper.close();
	}
}
