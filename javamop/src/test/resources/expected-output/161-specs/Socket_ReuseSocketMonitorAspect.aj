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

public aspect Socket_ReuseSocketMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Socket_ReuseSocketMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Socket_ReuseSocket_MOPLock = new ReentrantLock();
	static Condition Socket_ReuseSocket_MOPLock_cond = Socket_ReuseSocket_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Socket_ReuseSocket_connect(Socket sock) : (call(* Socket+.connect(..)) && target(sock)) && MOP_CommonPointCut();
	before (Socket sock) : Socket_ReuseSocket_connect(sock) {
		Socket_ReuseSocketRuntimeMonitor.connectEvent(sock);
	}

	pointcut Socket_ReuseSocket_bind(Socket sock) : (call(* Socket+.bind(..)) && target(sock)) && MOP_CommonPointCut();
	before (Socket sock) : Socket_ReuseSocket_bind(sock) {
		Socket_ReuseSocketRuntimeMonitor.bindEvent(sock);
	}

	pointcut Socket_ReuseSocket_close(Socket sock) : (call(* Socket+.close(..)) && target(sock)) && MOP_CommonPointCut();
	before (Socket sock) : Socket_ReuseSocket_close(sock) {
		Socket_ReuseSocketRuntimeMonitor.closeEvent(sock);
	}

}
