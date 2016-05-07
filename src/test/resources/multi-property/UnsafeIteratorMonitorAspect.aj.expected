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

public aspect UnsafeIteratorMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public UnsafeIteratorMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock UnsafeIterator_MOPLock = new ReentrantLock();
	static Condition UnsafeIterator_MOPLock_cond = UnsafeIterator_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut UnsafeIterator_next(Iterator i) : (call(* Iterator.next()) && target(i)) && MOP_CommonPointCut();
	before (Iterator i) : UnsafeIterator_next(i) {
		UnsafeIteratorRuntimeMonitor.nextEvent(i);
	}

	pointcut UnsafeIterator_create(Collection c) : (call(Iterator Collection+.iterator()) && target(c)) && MOP_CommonPointCut();
	after (Collection c) returning (Iterator i) : UnsafeIterator_create(c) {
		UnsafeIteratorRuntimeMonitor.createEvent(c, i);
	}

	pointcut UnsafeIterator_updatesource(Collection c) : ((call(* Collection+.remove*(..)) || call(* Collection+.add*(..))) && target(c)) && MOP_CommonPointCut();
	after (Collection c) : UnsafeIterator_updatesource(c) {
		UnsafeIteratorRuntimeMonitor.updatesourceEvent(c);
	}

}
