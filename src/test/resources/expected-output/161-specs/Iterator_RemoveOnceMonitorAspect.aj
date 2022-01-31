package mop;
import java.util.*;
import java.lang.*;
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

public aspect Iterator_RemoveOnceMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Iterator_RemoveOnceMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Iterator_RemoveOnce_MOPLock = new ReentrantLock();
	static Condition Iterator_RemoveOnce_MOPLock_cond = Iterator_RemoveOnce_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Iterator_RemoveOnce_next(Iterator i) : (call(* Iterator+.next()) && target(i)) && MOP_CommonPointCut();
	before (Iterator i) : Iterator_RemoveOnce_next(i) {
		Iterator_RemoveOnceRuntimeMonitor.nextEvent(i);
	}

	pointcut Iterator_RemoveOnce_remove(Iterator i) : (call(void Iterator+.remove()) && target(i)) && MOP_CommonPointCut();
	before (Iterator i) : Iterator_RemoveOnce_remove(i) {
		Iterator_RemoveOnceRuntimeMonitor.removeEvent(i);
	}

}
