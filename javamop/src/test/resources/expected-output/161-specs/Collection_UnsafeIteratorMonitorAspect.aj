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

public aspect Collection_UnsafeIteratorMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Collection_UnsafeIteratorMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Collection_UnsafeIterator_MOPLock = new ReentrantLock();
	static Condition Collection_UnsafeIterator_MOPLock_cond = Collection_UnsafeIterator_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Collection_UnsafeIterator_useiter(Iterator i) : ((call(* Iterator.hasNext(..)) || call(* Iterator.next(..))) && target(i)) && MOP_CommonPointCut();
	before (Iterator i) : Collection_UnsafeIterator_useiter(i) {
		Collection_UnsafeIteratorRuntimeMonitor.useiterEvent(i);
	}

	pointcut Collection_UnsafeIterator_modify(Collection c) : ((call(* Collection+.add*(..)) || call(* Collection+.clear(..)) || call(* Collection+.offer*(..)) || call(* Collection+.pop(..)) || call(* Collection+.push(..)) || call(* Collection+.remove*(..)) || call(* Collection+.retain*(..))) && target(c)) && MOP_CommonPointCut();
	before (Collection c) : Collection_UnsafeIterator_modify(c) {
		Collection_UnsafeIteratorRuntimeMonitor.modifyEvent(c);
	}

	pointcut Collection_UnsafeIterator_create(Collection c) : (call(Iterator Iterable+.iterator()) && target(c)) && MOP_CommonPointCut();
	after (Collection c) returning (Iterator i) : Collection_UnsafeIterator_create(c) {
		Collection_UnsafeIteratorRuntimeMonitor.createEvent(c, i);
	}

}
