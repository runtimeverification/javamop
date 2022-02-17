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

public aspect ServerSocket_SetTimeoutBeforeBlockingMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public ServerSocket_SetTimeoutBeforeBlockingMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock ServerSocket_SetTimeoutBeforeBlocking_MOPLock = new ReentrantLock();
	static Condition ServerSocket_SetTimeoutBeforeBlocking_MOPLock_cond = ServerSocket_SetTimeoutBeforeBlocking_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut ServerSocket_SetTimeoutBeforeBlocking_set(ServerSocket sock, int timeout) : (call(* ServerSocket+.setSoTimeout(int)) && target(sock) && args(timeout)) && MOP_CommonPointCut();
	before (ServerSocket sock, int timeout) : ServerSocket_SetTimeoutBeforeBlocking_set(sock, timeout) {
		ServerSocket_SetTimeoutBeforeBlockingRuntimeMonitor.setEvent(sock, timeout);
	}

	pointcut ServerSocket_SetTimeoutBeforeBlocking_enter(ServerSocket sock) : (call(* ServerSocket+.accept(..)) && target(sock)) && MOP_CommonPointCut();
	before (ServerSocket sock) : ServerSocket_SetTimeoutBeforeBlocking_enter(sock) {
		ServerSocket_SetTimeoutBeforeBlockingRuntimeMonitor.enterEvent(sock);
	}

	after (ServerSocket sock) : ServerSocket_SetTimeoutBeforeBlocking_enter(sock) {
		ServerSocket_SetTimeoutBeforeBlockingRuntimeMonitor.leaveEvent(sock);
	}

}
