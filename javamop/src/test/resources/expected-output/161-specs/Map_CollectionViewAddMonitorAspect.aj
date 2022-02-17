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

public aspect Map_CollectionViewAddMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Map_CollectionViewAddMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Map_CollectionViewAdd_MOPLock = new ReentrantLock();
	static Condition Map_CollectionViewAdd_MOPLock_cond = Map_CollectionViewAdd_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Map_CollectionViewAdd_add(Collection c) : ((call(* Collection+.add(..)) || call(* Collection+.addAll(..))) && target(c)) && MOP_CommonPointCut();
	before (Collection c) : Map_CollectionViewAdd_add(c) {
		Map_CollectionViewAddRuntimeMonitor.addEvent(c);
	}

	pointcut Map_CollectionViewAdd_getset(Map m) : ((call(Set Map+.keySet()) || call(Set Map+.entrySet()) || call(Collection Map+.values())) && target(m)) && MOP_CommonPointCut();
	after (Map m) returning (Collection c) : Map_CollectionViewAdd_getset(m) {
		Map_CollectionViewAddRuntimeMonitor.getsetEvent(m, c);
	}

}
