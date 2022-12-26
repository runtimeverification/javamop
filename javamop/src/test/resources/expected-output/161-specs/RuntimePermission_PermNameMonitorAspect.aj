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

public aspect RuntimePermission_PermNameMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public RuntimePermission_PermNameMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock RuntimePermission_PermName_MOPLock = new ReentrantLock();
	static Condition RuntimePermission_PermName_MOPLock_cond = RuntimePermission_PermName_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut RuntimePermission_PermName_constructor_runtimeperm(String name) : (call(RuntimePermission.new(String)) && args(name)) && MOP_CommonPointCut();
	after (String name) returning (RuntimePermission r) : RuntimePermission_PermName_constructor_runtimeperm(name) {
		RuntimePermission_PermNameRuntimeMonitor.constructor_runtimepermEvent(name, r);
	}

}
