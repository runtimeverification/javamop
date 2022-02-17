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

public aspect CreationMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public CreationMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Creation_MOPLock = new ReentrantLock();
	static Condition Creation_MOPLock_cond = Creation_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Creation_fun1(Object o) : (call(* *.fun1()) && target(o)) && MOP_CommonPointCut();
	after (Object o) : Creation_fun1(o) {
		CreationRuntimeMonitor.fun1Event(o);
	}

	pointcut Creation_fun2(Object o) : (call(* *.fun2()) && target(o)) && MOP_CommonPointCut();
	after (Object o) : Creation_fun2(o) {
		CreationRuntimeMonitor.fun2Event(o);
	}

	pointcut Creation_mainend() : (execution(* *.main(..))) && MOP_CommonPointCut();
	after () : Creation_mainend() {
		CreationRuntimeMonitor.mainendEvent();
	}

}
