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

public aspect Arrays_MutuallyComparableMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Arrays_MutuallyComparableMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Arrays_MutuallyComparable_MOPLock = new ReentrantLock();
	static Condition Arrays_MutuallyComparable_MOPLock_cond = Arrays_MutuallyComparable_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Arrays_MutuallyComparable_invalid_sort(Object[] arr, Comparator comp) : ((call(void Arrays.sort(Object[], Comparator)) || call(void Arrays.sort(Object[], int, int, Comparator))) && args(arr, .., comp)) && MOP_CommonPointCut();
	before (Object[] arr, Comparator comp) : Arrays_MutuallyComparable_invalid_sort(arr, comp) {
		Arrays_MutuallyComparableRuntimeMonitor.invalid_sortEvent(arr, comp);
	}

}
