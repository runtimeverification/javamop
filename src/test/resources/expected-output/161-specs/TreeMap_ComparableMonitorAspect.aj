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

public aspect TreeMap_ComparableMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public TreeMap_ComparableMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock TreeMap_Comparable_MOPLock = new ReentrantLock();
	static Condition TreeMap_Comparable_MOPLock_cond = TreeMap_Comparable_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut TreeMap_Comparable_putall(Map src) : (call(* Map+.putAll(Map)) && args(src) && target(TreeMap)) && MOP_CommonPointCut();
	before (Map src) : TreeMap_Comparable_putall(src) {
		TreeMap_ComparableRuntimeMonitor.putallEvent(src);
	}

	pointcut TreeMap_Comparable_put(Object key) : (call(* Map+.put(Object, Object)) && args(key, ..) && target(TreeMap)) && MOP_CommonPointCut();
	before (Object key) : TreeMap_Comparable_put(key) {
		TreeMap_ComparableRuntimeMonitor.putEvent(key);
	}

	pointcut TreeMap_Comparable_create(Map src) : (call(TreeMap.new(Map)) && args(src)) && MOP_CommonPointCut();
	before (Map src) : TreeMap_Comparable_create(src) {
		TreeMap_ComparableRuntimeMonitor.createEvent(src);
	}

}
