package mop;
import java.io.*;
import com.runtimeverification.rvmonitor.java.rt.RVMLogging;
import com.runtimeverification.rvmonitor.java.rt.RVMLogging.Level;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.*;

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

public aspect BufferedInputStream_SynchronizedFillMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public BufferedInputStream_SynchronizedFillMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock BufferedInputStream_SynchronizedFill_MOPLock = new ReentrantLock();
	static Condition BufferedInputStream_SynchronizedFill_MOPLock_cond = BufferedInputStream_SynchronizedFill_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut BufferedInputStream_SynchronizedFill_fill(BufferedInputStream i) : (call(* BufferedInputStream.fill(..)) && target(i) && !cflow(call(synchronized * *.*(..)))) && MOP_CommonPointCut();
	before (BufferedInputStream i) : BufferedInputStream_SynchronizedFill_fill(i) {
		BufferedInputStream_SynchronizedFillRuntimeMonitor.fillEvent(i);
	}

}
