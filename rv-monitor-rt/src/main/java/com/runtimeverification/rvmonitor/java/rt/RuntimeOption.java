package com.runtimeverification.rvmonitor.java.rt;

/**
 * This "static" (C# term) class is to inform the runtime library of the
 * user-specified configuration used to generate the library code. This is
 * necessary because some runtime library (such as indexing trees) need to
 * behave differently depending on compile-time options.
 */
public class RuntimeOption {
	private RuntimeOption() {
	}
	
	private static boolean useFineGrainedLock;
	
	public static boolean isFineGrainedLockEnabled() {
		return useFineGrainedLock;
	}
	
	public static void enableFineGrainedLock(boolean on) {
		useFineGrainedLock = on;
	}
}
