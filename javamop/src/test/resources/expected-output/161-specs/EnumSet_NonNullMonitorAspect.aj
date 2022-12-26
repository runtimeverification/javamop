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

public aspect EnumSet_NonNullMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public EnumSet_NonNullMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock EnumSet_NonNull_MOPLock = new ReentrantLock();
	static Condition EnumSet_NonNull_MOPLock_cond = EnumSet_NonNull_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut EnumSet_NonNull_insertnull_4(Collection c) : (call(* EnumSet+.addAll(Collection)) && args(c)) && MOP_CommonPointCut();
	before (Collection c) : EnumSet_NonNull_insertnull_4(c) {
		EnumSet_NonNullRuntimeMonitor.insertnullEvent(c);
	}

	pointcut EnumSet_NonNull_insertnull_3(Object e) : (call(* EnumSet+.add(Object)) && args(e)) && MOP_CommonPointCut();
	before (Object e) : EnumSet_NonNull_insertnull_3(e) {
		EnumSet_NonNullRuntimeMonitor.insertnullEvent(e);
	}

}
