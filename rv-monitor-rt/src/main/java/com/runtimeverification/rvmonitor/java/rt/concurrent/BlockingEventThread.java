package com.runtimeverification.rvmonitor.java.rt.concurrent;
/**
 * 
 * Thread used to detect and trigger blocking event
 * 
 * @author Qingzhou
 *
 */
public class BlockingEventThread extends Thread {
	
	Thread monitoredThread = null;
	
	String eventName;
	
	public BlockingEventThread(String name) {
		this.monitoredThread = Thread.currentThread();
		this.eventName = name;
	}
	
	@Override
	public void run() {
		while(monitoredThread.getState() != Thread.State.BLOCKED && monitoredThread.getState() != Thread.State.WAITING) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (monitoredThread.getState() == Thread.State.TERMINATED) {
				// Report a warning
				System.err.println("Blocking event: " + this.eventName + " doesn't happen!");
				return;
			}
		}
		execEvent();
	}
	

	/**
	 * 
	 * Method to be executed to send the blocking event to the monitor.
	 * 
	 * To be overridden by thread declaration in blocking events.
	 * 
	 * */
	public void execEvent() {
		
	}
}

