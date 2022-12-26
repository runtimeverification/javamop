package com.runtimeverification.rvmonitor.java.rt.observable;

import java.io.PrintWriter;

import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractMonitor;
import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractPartitionedMonitorSet;
import com.runtimeverification.rvmonitor.java.rt.tablebase.DisableHolder;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IMonitor;

class Dumper {
	private final PrintWriter writer;
	private final boolean flushOnNewLine;
	
	public Dumper(PrintWriter writer) {
		this.writer = writer;
		this.flushOnNewLine = true;
	}
	
	public void close() {
		this.writer.close();
	}

	public void endline() {
		this.writer.println();
		if (this.flushOnNewLine)
			this.writer.flush();
	}
	
	public void printSpace() {
		this.writer.print(' ');
	}
	
	public void printIndent(int num) {
		for (int i = 0; i < num; ++i)
			this.writer.print("   ");
	}
	
	public void printTitle(String title) {
		this.printIndent(1);
		this.writer.print('[');
		this.writer.print(title);
		this.writer.print("] ");
	}
	
	public void printMonitor(IMonitor monitor) {
		StringBuilder s = new StringBuilder();
		s.append(monitor.getClass().getSimpleName());

		if (monitor instanceof IObservableObject) {
			IObservableObject obs = (IObservableObject)monitor;
			s.append(obs.getObservableObjectDescription());
		}
		this.writer.print(s);
	}
	
	public void printPotentialMonitor(Object o) {
		StringBuilder s = new StringBuilder();
		if (o == null)
			s.append('_');
		else {
			if (o instanceof DisableHolder || o instanceof AbstractMonitor)
				s.append(o.getClass().getSimpleName());
			if (o instanceof IObservableObject) {
				IObservableObject obs = (IObservableObject)o;
				s.append(obs.getObservableObjectDescription());
			}
		}
		this.writer.print(s);
	}
	
	public void printSet(AbstractPartitionedMonitorSet<?> set) {
		this.writer.print(set.getClass().getSimpleName());
	}

	public void print(String string, int num) {
		this.writer.print(string);
		this.writer.print(':');
		this.writer.print(num);
	}
}
