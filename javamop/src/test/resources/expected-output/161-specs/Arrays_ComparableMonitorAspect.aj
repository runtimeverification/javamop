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

public aspect Arrays_ComparableMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Arrays_ComparableMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Arrays_Comparable_MOPLock = new ReentrantLock();
	static Condition Arrays_Comparable_MOPLock_cond = Arrays_Comparable_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Arrays_Comparable_invalid_sort(Object[] arr) : (target(Arrays) && (call(void Arrays.sort(Object[])) || call(void Arrays.sort(Object[], ..))) && args(arr, ..)) && MOP_CommonPointCut();
	before (Object[] arr) : Arrays_Comparable_invalid_sort(arr) {
		Arrays_ComparableRuntimeMonitor.invalid_sortEvent(arr);
	}

}
