package com.runtimeverification.rvmonitor.java.rt.observable;

import java.util.ArrayList;

public class ObserverSettings {
	public static final boolean observeSetBehavior = false;
	
	private static final ArrayList<IObserver> observers = new ArrayList<IObserver>();
	
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				ArrayList<IObserver> obs = observers;
				for (IObserver o : obs)
					o.onCompleted();
			}
		});
	}

	public static synchronized void register(IObserver observer) {
		observers.add(observer);
	}
}
