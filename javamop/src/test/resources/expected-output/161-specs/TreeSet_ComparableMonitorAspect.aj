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

public aspect TreeSet_ComparableMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public TreeSet_ComparableMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock TreeSet_Comparable_MOPLock = new ReentrantLock();
	static Condition TreeSet_Comparable_MOPLock_cond = TreeSet_Comparable_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut TreeSet_Comparable_addall(Collection c) : (call(* Collection+.addAll(Collection)) && target(TreeSet) && args(c)) && MOP_CommonPointCut();
	before (Collection c) : TreeSet_Comparable_addall(c) {
		TreeSet_ComparableRuntimeMonitor.addallEvent(c);
	}

	pointcut TreeSet_Comparable_add(Object e) : (call(* Collection+.add*(..)) && target(TreeSet) && args(e)) && MOP_CommonPointCut();
	before (Object e) : TreeSet_Comparable_add(e) {
		TreeSet_ComparableRuntimeMonitor.addEvent(e);
	}

}
