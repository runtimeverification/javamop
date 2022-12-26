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

public aspect ArrayDeque_NonNullMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public ArrayDeque_NonNullMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock ArrayDeque_NonNull_MOPLock = new ReentrantLock();
	static Condition ArrayDeque_NonNull_MOPLock_cond = ArrayDeque_NonNull_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut ArrayDeque_NonNull_insertnull(Object e) : ((call(* ArrayDeque.add*(..)) || call(* ArrayDeque.offer*(..)) || call(* ArrayDeque.push(..))) && args(Object+) && args(e)) && MOP_CommonPointCut();
	before (Object e) : ArrayDeque_NonNull_insertnull(e) {
		ArrayDeque_NonNullRuntimeMonitor.insertnullEvent(e);
	}

}
