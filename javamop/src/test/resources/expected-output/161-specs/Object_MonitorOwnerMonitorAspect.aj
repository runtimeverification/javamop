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

public aspect Object_MonitorOwnerMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Object_MonitorOwnerMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Object_MonitorOwner_MOPLock = new ReentrantLock();
	static Condition Object_MonitorOwner_MOPLock_cond = Object_MonitorOwner_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Object_MonitorOwner_bad_wait(Object o) : (call(* Object+.wait(..)) && target(o) && if(!Thread.holdsLock(o))) && MOP_CommonPointCut();
	before (Object o) : Object_MonitorOwner_bad_wait(o) {
		Object_MonitorOwnerRuntimeMonitor.bad_waitEvent(o);
	}

	pointcut Object_MonitorOwner_bad_notify(Object o) : ((call(* Object+.notify(..)) || call(* Object+.notifyAll(..))) && target(o) && if(!Thread.holdsLock(o))) && MOP_CommonPointCut();
	before (Object o) : Object_MonitorOwner_bad_notify(o) {
		Object_MonitorOwnerRuntimeMonitor.bad_notifyEvent(o);
	}

}
