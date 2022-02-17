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

public aspect SortedSet_ComparableMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public SortedSet_ComparableMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock SortedSet_Comparable_MOPLock = new ReentrantLock();
	static Condition SortedSet_Comparable_MOPLock_cond = SortedSet_Comparable_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut SortedSet_Comparable_addall(Collection c) : (call(* Collection+.addAll(Collection)) && target(SortedSet) && args(c)) && MOP_CommonPointCut();
	before (Collection c) : SortedSet_Comparable_addall(c) {
		SortedSet_ComparableRuntimeMonitor.addallEvent(c);
	}

	pointcut SortedSet_Comparable_add(Object e) : ((call(* Collection+.add*(..)) || call(* Queue+.offer*(..))) && target(SortedSet) && args(e)) && MOP_CommonPointCut();
	before (Object e) : SortedSet_Comparable_add(e) {
		SortedSet_ComparableRuntimeMonitor.addEvent(e);
	}

}
