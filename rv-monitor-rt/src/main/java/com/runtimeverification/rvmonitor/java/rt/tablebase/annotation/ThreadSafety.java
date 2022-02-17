package com.runtimeverification.rvmonitor.java.rt.tablebase.annotation;

public @interface ThreadSafety {
	public enum Safety { REENTRANT, SAFE, UNSAFE }
	Safety safety() default Safety.REENTRANT;
}
