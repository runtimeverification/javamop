package mop;
import java.io.*;
import java.lang.*;
import java.util.*;
import com.runtimeverification.rvmonitor.java.rt.RVMLogging;
import com.runtimeverification.rvmonitor.java.rt.RVMLogging.Level;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

import java.lang.ref.*;
import org.aspectj.lang.*;

aspect BaseAspect {
	pointcut notwithin() :
	!within(sun..*) &&
	!within(java..*) &&
	!within(javax..*) &&
	!within(com.sun..*) &&
	!within(org.dacapo.harness..*) &&
	!within(org.apache.commons..*) &&
	!within(org.apache.geronimo..*) &&
	!within(net.sf.cglib..*) &&
	!within(mop..*) &&
	!within(javamoprt..*) &&
	!within(rvmonitorrt..*) &&
	!within(com.runtimeverification..*);
}

public aspect Throwable_InitCauseOnceMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Throwable_InitCauseOnceMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Throwable_InitCauseOnce_MOPLock = new ReentrantLock();
	static Condition Throwable_InitCauseOnce_MOPLock_cond = Throwable_InitCauseOnce_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Throwable_InitCauseOnce_initCause(Throwable t) : (call(* Throwable+.initCause(..)) && target(t)) && MOP_CommonPointCut();
	before (Throwable t) : Throwable_InitCauseOnce_initCause(t) {
		Throwable_InitCauseOnceRuntimeMonitor.initCauseEvent(t);
	}

	pointcut Throwable_InitCauseOnce_createWithoutThrowable() : (call(Throwable+.new()) || call(Throwable+.new(String))) && MOP_CommonPointCut();
	after () returning (Throwable t) : Throwable_InitCauseOnce_createWithoutThrowable() {
		Throwable_InitCauseOnceRuntimeMonitor.createWithoutThrowableEvent(t);
	}

	pointcut Throwable_InitCauseOnce_createWithThrowable() : (call(Throwable+.new(String, Throwable)) || call(Throwable+.new(Throwable))) && MOP_CommonPointCut();
	after () returning (Throwable t) : Throwable_InitCauseOnce_createWithThrowable() {
		Throwable_InitCauseOnceRuntimeMonitor.createWithThrowableEvent(t);
	}

}
