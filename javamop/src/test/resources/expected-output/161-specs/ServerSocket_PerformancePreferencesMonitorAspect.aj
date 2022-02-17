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

public aspect ServerSocket_PerformancePreferencesMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public ServerSocket_PerformancePreferencesMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock ServerSocket_PerformancePreferences_MOPLock = new ReentrantLock();
	static Condition ServerSocket_PerformancePreferences_MOPLock_cond = ServerSocket_PerformancePreferences_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut ServerSocket_PerformancePreferences_set(ServerSocket sock) : (call(* ServerSocket+.setPerformancePreferences(..)) && target(sock)) && MOP_CommonPointCut();
	before (ServerSocket sock) : ServerSocket_PerformancePreferences_set(sock) {
		ServerSocket_PerformancePreferencesRuntimeMonitor.setEvent(sock);
	}

	pointcut ServerSocket_PerformancePreferences_bind(ServerSocket sock) : (call(* ServerSocket+.bind(..)) && target(sock)) && MOP_CommonPointCut();
	before (ServerSocket sock) : ServerSocket_PerformancePreferences_bind(sock) {
		ServerSocket_PerformancePreferencesRuntimeMonitor.bindEvent(sock);
	}

	pointcut ServerSocket_PerformancePreferences_create_bound() : (call(ServerSocket.new(int, ..))) && MOP_CommonPointCut();
	after () returning (ServerSocket sock) : ServerSocket_PerformancePreferences_create_bound() {
		ServerSocket_PerformancePreferencesRuntimeMonitor.create_boundEvent(sock);
	}

	pointcut ServerSocket_PerformancePreferences_create_unbound() : (call(ServerSocket.new())) && MOP_CommonPointCut();
	after () returning (ServerSocket sock) : ServerSocket_PerformancePreferences_create_unbound() {
		ServerSocket_PerformancePreferencesRuntimeMonitor.create_unboundEvent(sock);
	}

}
