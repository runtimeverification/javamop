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

public aspect NavigableMap_ModificationMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public NavigableMap_ModificationMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock NavigableMap_Modification_MOPLock = new ReentrantLock();
	static Condition NavigableMap_Modification_MOPLock_cond = NavigableMap_Modification_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut NavigableMap_Modification_useiter(Iterator i) : ((call(* Iterator.hasNext(..)) || call(* Iterator.next(..))) && target(i)) && MOP_CommonPointCut();
	before (Iterator i) : NavigableMap_Modification_useiter(i) {
		NavigableMap_ModificationRuntimeMonitor.useiterEvent(i);
	}

	pointcut NavigableMap_Modification_modify3(Collection c) : ((call(* Collection+.add(..)) || call(* Collection+.addAll(..))) && target(c)) && MOP_CommonPointCut();
	before (Collection c) : NavigableMap_Modification_modify3(c) {
		NavigableMap_ModificationRuntimeMonitor.modify3Event(c);
	}

	pointcut NavigableMap_Modification_modify2(NavigableMap m2) : ((call(* Map+.clear*(..)) || call(* Map+.put*(..)) || call(* Map+.remove(..))) && target(m2)) && MOP_CommonPointCut();
	before (NavigableMap m2) : NavigableMap_Modification_modify2(m2) {
		NavigableMap_ModificationRuntimeMonitor.modify2Event(m2);
	}

	pointcut NavigableMap_Modification_modify1(NavigableMap m1) : ((call(* Map+.clear*(..)) || call(* Map+.put*(..)) || call(* Map+.remove(..))) && target(m1)) && MOP_CommonPointCut();
	before (NavigableMap m1) : NavigableMap_Modification_modify1(m1) {
		NavigableMap_ModificationRuntimeMonitor.modify1Event(m1);
	}

	pointcut NavigableMap_Modification_create(NavigableMap m1) : (call(NavigableMap NavigableMap+.descendingMap()) && target(m1)) && MOP_CommonPointCut();
	after (NavigableMap m1) returning (NavigableMap m2) : NavigableMap_Modification_create(m1) {
		NavigableMap_ModificationRuntimeMonitor.createEvent(m1, m2);
	}

	pointcut NavigableMap_Modification_getset1(NavigableMap m1) : ((call(Set Map+.keySet()) || call(Set Map+.entrySet()) || call(Collection Map+.values())) && target(m1)) && MOP_CommonPointCut();
	after (NavigableMap m1) returning (Collection c) : NavigableMap_Modification_getset1(m1) {
		NavigableMap_ModificationRuntimeMonitor.getset1Event(m1, c);
	}

	pointcut NavigableMap_Modification_getset2(NavigableMap m2) : ((call(Set Map+.keySet()) || call(Set Map+.entrySet()) || call(Collection Map+.values())) && target(m2)) && MOP_CommonPointCut();
	after (NavigableMap m2) returning (Collection c) : NavigableMap_Modification_getset2(m2) {
		NavigableMap_ModificationRuntimeMonitor.getset2Event(m2, c);
	}

	pointcut NavigableMap_Modification_getiter(Collection c) : (call(Iterator Iterable+.iterator()) && target(c)) && MOP_CommonPointCut();
	after (Collection c) returning (Iterator i) : NavigableMap_Modification_getiter(c) {
		NavigableMap_ModificationRuntimeMonitor.getiterEvent(c, i);
	}

}
