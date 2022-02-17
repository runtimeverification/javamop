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

public aspect Collections_ImplementComparableMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Collections_ImplementComparableMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Collections_ImplementComparable_MOPLock = new ReentrantLock();
	static Condition Collections_ImplementComparable_MOPLock_cond = Collections_ImplementComparable_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Collections_ImplementComparable_invalid_minmax(Collection col) : ((call(* Collections.min(Collection)) || call(* Collections.max(Collection))) && args(col)) && MOP_CommonPointCut();
	before (Collection col) : Collections_ImplementComparable_invalid_minmax(col) {
		Collections_ImplementComparableRuntimeMonitor.invalid_minmaxEvent(col);
	}

	pointcut Collections_ImplementComparable_invalid_sort(List list) : (call(void Collections.sort(List)) && args(list)) && MOP_CommonPointCut();
	before (List list) : Collections_ImplementComparable_invalid_sort(list) {
		Collections_ImplementComparableRuntimeMonitor.invalid_sortEvent(list);
	}

}
