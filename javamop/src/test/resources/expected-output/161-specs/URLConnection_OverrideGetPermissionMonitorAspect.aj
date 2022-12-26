package mop;
import java.net.*;
import java.lang.reflect.*;
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

public aspect URLConnection_OverrideGetPermissionMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public URLConnection_OverrideGetPermissionMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock URLConnection_OverrideGetPermission_MOPLock = new ReentrantLock();
	static Condition URLConnection_OverrideGetPermission_MOPLock_cond = URLConnection_OverrideGetPermission_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut URLConnection_OverrideGetPermission_staticinit() : (staticinitialization(URLConnection+)) && MOP_CommonPointCut();
	after () : URLConnection_OverrideGetPermission_staticinit() {
		URLConnection_OverrideGetPermissionRuntimeMonitor.staticinitEvent(thisJoinPoint.getStaticPart().getSignature());
	}

}
