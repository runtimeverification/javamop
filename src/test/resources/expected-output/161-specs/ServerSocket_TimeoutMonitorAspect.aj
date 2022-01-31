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

public aspect ServerSocket_TimeoutMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public ServerSocket_TimeoutMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock ServerSocket_Timeout_MOPLock = new ReentrantLock();
	static Condition ServerSocket_Timeout_MOPLock_cond = ServerSocket_Timeout_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut ServerSocket_Timeout_set(int timeout) : (call(* ServerSocket+.setSoTimeout(int)) && args(timeout)) && MOP_CommonPointCut();
	before (int timeout) : ServerSocket_Timeout_set(timeout) {
		ServerSocket_TimeoutRuntimeMonitor.setEvent(timeout);
	}

}
