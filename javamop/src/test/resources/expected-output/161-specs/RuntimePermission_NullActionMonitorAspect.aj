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

public aspect RuntimePermission_NullActionMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public RuntimePermission_NullActionMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock RuntimePermission_NullAction_MOPLock = new ReentrantLock();
	static Condition RuntimePermission_NullAction_MOPLock_cond = RuntimePermission_NullAction_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut RuntimePermission_NullAction_constructor_runtimeperm(String name, String actions) : (call(RuntimePermission.new(String, String)) && args(name, actions)) && MOP_CommonPointCut();
	after (String name, String actions) returning (RuntimePermission r) : RuntimePermission_NullAction_constructor_runtimeperm(name, actions) {
		RuntimePermission_NullActionRuntimeMonitor.constructor_runtimepermEvent(name, actions, r);
	}

}
