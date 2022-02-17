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

public aspect Socket_TimeoutMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Socket_TimeoutMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Socket_Timeout_MOPLock = new ReentrantLock();
	static Condition Socket_Timeout_MOPLock_cond = Socket_Timeout_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Socket_Timeout_set(int timeout) : (call(* Socket+.setSoTimeout(int)) && args(timeout)) && MOP_CommonPointCut();
	before (int timeout) : Socket_Timeout_set(timeout) {
		Socket_TimeoutRuntimeMonitor.setEvent(timeout);
	}

}
