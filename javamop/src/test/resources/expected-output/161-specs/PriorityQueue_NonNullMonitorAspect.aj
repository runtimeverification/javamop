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

public aspect PriorityQueue_NonNullMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public PriorityQueue_NonNullMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock PriorityQueue_NonNull_MOPLock = new ReentrantLock();
	static Condition PriorityQueue_NonNull_MOPLock_cond = PriorityQueue_NonNull_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut PriorityQueue_NonNull_insertnull_12(Collection c) : (call(* Collection+.addAll(Collection)) && target(PriorityQueue) && args(c)) && MOP_CommonPointCut();
	before (Collection c) : PriorityQueue_NonNull_insertnull_12(c) {
		PriorityQueue_NonNullRuntimeMonitor.insertnullEvent(c);
	}

	pointcut PriorityQueue_NonNull_insertnull_11(Object e) : ((call(* Collection+.add*(..)) || call(* Queue+.offer*(..))) && target(PriorityQueue) && args(e)) && MOP_CommonPointCut();
	before (Object e) : PriorityQueue_NonNull_insertnull_11(e) {
		PriorityQueue_NonNullRuntimeMonitor.insertnullEvent(e);
	}

}
