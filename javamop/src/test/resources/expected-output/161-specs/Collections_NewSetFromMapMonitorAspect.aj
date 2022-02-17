package mop;
import java.util.*;
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

public aspect Collections_NewSetFromMapMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Collections_NewSetFromMapMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Collections_NewSetFromMap_MOPLock = new ReentrantLock();
	static Condition Collections_NewSetFromMap_MOPLock_cond = Collections_NewSetFromMap_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Collections_NewSetFromMap_access(Map map) : (call(* Map+.*(..)) && target(map)) && MOP_CommonPointCut();
	before (Map map) : Collections_NewSetFromMap_access(map) {
		Collections_NewSetFromMapRuntimeMonitor.accessEvent(map);
	}

	pointcut Collections_NewSetFromMap_create(Map map) : (call(* Collections.newSetFromMap(Map)) && args(map)) && MOP_CommonPointCut();
	before (Map map) : Collections_NewSetFromMap_create(map) {
		//Collections_NewSetFromMap_bad_create
		Collections_NewSetFromMapRuntimeMonitor.bad_createEvent(map);
		//Collections_NewSetFromMap_create
		Collections_NewSetFromMapRuntimeMonitor.createEvent(map);
	}

}
