package mop;
import java.io.*;
import java.util.*;
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

public aspect HasNextMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public HasNextMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock HasNext_MOPLock = new ReentrantLock();
	static Condition HasNext_MOPLock_cond = HasNext_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut HasNext_next(Iterator i) : (call(* Iterator.next()) && target(i)) && MOP_CommonPointCut();
	before (Iterator i) : HasNext_next(i) {
		HasNextRuntimeMonitor.nextEvent(i);
	}

	pointcut HasNext_hasnext(Iterator i) : (call(* Iterator.hasNext()) && target(i)) && MOP_CommonPointCut();
	after (Iterator i) : HasNext_hasnext(i) {
		HasNextRuntimeMonitor.hasnextEvent(i);
	}

}
