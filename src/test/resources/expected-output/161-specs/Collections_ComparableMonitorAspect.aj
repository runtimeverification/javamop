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

public aspect Collections_ComparableMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Collections_ComparableMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Collections_Comparable_MOPLock = new ReentrantLock();
	static Condition Collections_Comparable_MOPLock_cond = Collections_Comparable_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Collections_Comparable_invalid_minmax(Collection col, Comparator comp) : ((call(* Collections.min(Collection, Comparator)) || call(* Collections.max(Collection, Comparator))) && args(col, comp)) && MOP_CommonPointCut();
	before (Collection col, Comparator comp) : Collections_Comparable_invalid_minmax(col, comp) {
		Collections_ComparableRuntimeMonitor.invalid_minmaxEvent(col, comp);
	}

	pointcut Collections_Comparable_invalid_sort(List list, Comparator comp) : (call(void Collections.sort(List, Comparator)) && args(list, comp)) && MOP_CommonPointCut();
	before (List list, Comparator comp) : Collections_Comparable_invalid_sort(list, comp) {
		Collections_ComparableRuntimeMonitor.invalid_sortEvent(list, comp);
	}

}
