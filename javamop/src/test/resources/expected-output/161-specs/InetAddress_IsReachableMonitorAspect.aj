package mop;
import java.net.*;
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

public aspect InetAddress_IsReachableMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public InetAddress_IsReachableMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock InetAddress_IsReachable_MOPLock = new ReentrantLock();
	static Condition InetAddress_IsReachable_MOPLock_cond = InetAddress_IsReachable_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut InetAddress_IsReachable_isreachable_4(int ttl, int timeout) : (call(* InetAddress+.isReachable(NetworkInterface, int, int)) && args(*, ttl, timeout)) && MOP_CommonPointCut();
	before (int ttl, int timeout) : InetAddress_IsReachable_isreachable_4(ttl, timeout) {
		InetAddress_IsReachableRuntimeMonitor.isreachableEvent(ttl, timeout);
	}

	pointcut InetAddress_IsReachable_isreachable_3(int timeout) : (call(* InetAddress+.isReachable(int)) && args(timeout)) && MOP_CommonPointCut();
	before (int timeout) : InetAddress_IsReachable_isreachable_3(timeout) {
		InetAddress_IsReachableRuntimeMonitor.isreachableEvent(timeout);
	}

}
