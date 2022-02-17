package com.runtimeverification.rvmonitor.java.rt;

public class RVMTimer implements RVMObject {
	long elapsedTime = 0;

	boolean running = false;
	long startTime = 0;
	long endTime = 0;

	public void start() {
		if (running)
			return;
		startTime = System.nanoTime();
		running = true;
	}

	public void end() {
		if (!running)
			return;
		endTime = System.nanoTime();
		running = false;
		elapsedTime += (endTime - startTime);
	}

	public long getElapsedMicroTime() {
		return elapsedTime / 1000;
	}

	public long getElapsedNanoTime() {
		return elapsedTime;
	}
}