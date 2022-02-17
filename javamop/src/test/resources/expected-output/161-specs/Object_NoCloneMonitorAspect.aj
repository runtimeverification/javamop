package mop;
import java.io.*;
import java.lang.*;
import com.runtimeverification.rvmonitor.java.rt.RVMLogging;
import com.runtimeverification.rvmonitor.java.rt.RVMLogging.Level;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.*;

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

public aspect Object_NoCloneMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Object_NoCloneMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Object_NoClone_MOPLock = new ReentrantLock();
	static Condition Object_NoClone_MOPLock_cond = Object_NoClone_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Object_NoClone_clone(Object o) : (call(* Object.clone()) && target(o)) && MOP_CommonPointCut();
	before (Object o) : Object_NoClone_clone(o) {
		Object_NoCloneRuntimeMonitor.cloneEvent(o);
	}

}
