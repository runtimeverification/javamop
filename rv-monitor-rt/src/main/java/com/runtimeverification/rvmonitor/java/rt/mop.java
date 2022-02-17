package com.runtimeverification.rvmonitor.java.rt;

import java.io.PrintStream;

public class mop {
	static public PrintStream nullStream = new PrintStream(new NullOutputStream());
	
	static public PrintStream out = nullStream;
	static public PrintStream err = nullStream;

	public mop() {
		this.out = nullStream;
		this.err = nullStream;
	}

	public mop(boolean verbose) {
		if (verbose) {
			this.out = System.out;
			this.err = System.err;
		} else {
			this.out = nullStream;
			this.err = nullStream;
		}
	}

	public void setVerbose(boolean verbose) {
		if (verbose) {
			this.out = System.out;
			this.err = System.err;
		} else {
			this.out = nullStream;
			this.err = nullStream;
		}
	}

	static class NullOutputStream extends java.io.OutputStream {
		public void write(int b) {
		}
	}
}
