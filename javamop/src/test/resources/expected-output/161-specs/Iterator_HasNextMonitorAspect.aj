package mop;
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

public aspect Iterator_HasNextMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Iterator_HasNextMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Iterator_HasNext_MOPLock = new ReentrantLock();
	static Condition Iterator_HasNext_MOPLock_cond = Iterator_HasNext_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Iterator_HasNext_next(Iterator i) : (call(* Iterator+.next()) && target(i)) && MOP_CommonPointCut();
	before (Iterator i) : Iterator_HasNext_next(i) {
		Iterator_HasNextRuntimeMonitor.nextEvent(i);
	}

	pointcut Iterator_HasNext_hasnexttrue(Iterator i) : (call(* Iterator+.hasNext()) && target(i)) && MOP_CommonPointCut();
	after (Iterator i) returning (boolean b) : Iterator_HasNext_hasnexttrue(i) {
		//Iterator_HasNext_hasnexttrue
		Iterator_HasNextRuntimeMonitor.hasnexttrueEvent(i, b);
		//Iterator_HasNext_hasnextfalse
		Iterator_HasNextRuntimeMonitor.hasnextfalseEvent(i, b);
	}

}
