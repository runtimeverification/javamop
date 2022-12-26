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

public aspect EnumMap_NonNullMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public EnumMap_NonNullMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock EnumMap_NonNull_MOPLock = new ReentrantLock();
	static Condition EnumMap_NonNull_MOPLock_cond = EnumMap_NonNull_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut EnumMap_NonNull_insertnull_16(Map m) : (call(* EnumMap.putAll(Map)) && args(m)) && MOP_CommonPointCut();
	before (Map m) : EnumMap_NonNull_insertnull_16(m) {
		EnumMap_NonNullRuntimeMonitor.insertnullEvent(m);
	}

	pointcut EnumMap_NonNull_insertnull_15(Object e) : (call(* EnumMap.put(Object, Object)) && args(e, ..)) && MOP_CommonPointCut();
	before (Object e) : EnumMap_NonNull_insertnull_15(e) {
		EnumMap_NonNullRuntimeMonitor.insertnullEvent(e);
	}

}
