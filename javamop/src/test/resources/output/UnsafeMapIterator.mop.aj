package mop;
import java.io.*;
import java.util.*;
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

public aspect UnsafeMapIteratorMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public UnsafeMapIteratorMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock UnsafeMapIterator_MOPLock = new ReentrantLock();
	static Condition UnsafeMapIterator_MOPLock_cond = UnsafeMapIterator_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut UnsafeMapIterator_useIter(Iterator i) : (call(* Iterator.next()) && target(i)) && MOP_CommonPointCut();
	before (Iterator i) : UnsafeMapIterator_useIter(i) {
		UnsafeMapIteratorRuntimeMonitor.useIterEvent(i);
	}

	pointcut UnsafeMapIterator_createColl(Map map) : ((call(* Map.values()) || call(* Map.keySet())) && target(map)) && MOP_CommonPointCut();
	after (Map map) returning (Collection c) : UnsafeMapIterator_createColl(map) {
		UnsafeMapIteratorRuntimeMonitor.createCollEvent(map, c);
	}

	pointcut UnsafeMapIterator_createIter(Collection c) : (call(* Collection.iterator()) && target(c)) && MOP_CommonPointCut();
	after (Collection c) returning (Iterator i) : UnsafeMapIterator_createIter(c) {
		UnsafeMapIteratorRuntimeMonitor.createIterEvent(c, i);
	}

	pointcut UnsafeMapIterator_updateMap(Map map) : ((call(* Map.put*(..)) || call(* Map.putAll*(..)) || call(* Map.clear()) || call(* Map.remove*(..))) && target(map)) && MOP_CommonPointCut();
	after (Map map) : UnsafeMapIterator_updateMap(map) {
		UnsafeMapIteratorRuntimeMonitor.updateMapEvent(map);
	}

}
