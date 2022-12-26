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

public aspect Thread_StartOnceMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Thread_StartOnceMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Thread_StartOnce_MOPLock = new ReentrantLock();
	static Condition Thread_StartOnce_MOPLock_cond = Thread_StartOnce_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Thread_StartOnce_start(Thread t) : (call(* Thread+.start()) && target(t)) && MOP_CommonPointCut();
	before (Thread t) : Thread_StartOnce_start(t) {
		Thread_StartOnceRuntimeMonitor.startEvent(t);
	}

}
