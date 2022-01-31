package mop;
import java.net.*;
import java.io.OutputStream;
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

public aspect Socket_SetTimeoutBeforeBlockingOutputMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Socket_SetTimeoutBeforeBlockingOutputMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Socket_SetTimeoutBeforeBlockingOutput_MOPLock = new ReentrantLock();
	static Condition Socket_SetTimeoutBeforeBlockingOutput_MOPLock_cond = Socket_SetTimeoutBeforeBlockingOutput_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Socket_SetTimeoutBeforeBlockingOutput_set(Socket sock, int timeout) : (call(* Socket+.setSoTimeout(int)) && target(sock) && args(timeout)) && MOP_CommonPointCut();
	before (Socket sock, int timeout) : Socket_SetTimeoutBeforeBlockingOutput_set(sock, timeout) {
		Socket_SetTimeoutBeforeBlockingOutputRuntimeMonitor.setEvent(sock, timeout);
	}

	pointcut Socket_SetTimeoutBeforeBlockingOutput_enter(OutputStream output) : (call(* OutputStream+.write(..)) && target(output)) && MOP_CommonPointCut();
	before (OutputStream output) : Socket_SetTimeoutBeforeBlockingOutput_enter(output) {
		Socket_SetTimeoutBeforeBlockingOutputRuntimeMonitor.enterEvent(output);
	}

	pointcut Socket_SetTimeoutBeforeBlockingOutput_getoutput(Socket sock) : (call(OutputStream Socket+.getOutputStream()) && target(sock)) && MOP_CommonPointCut();
	after (Socket sock) returning (OutputStream output) : Socket_SetTimeoutBeforeBlockingOutput_getoutput(sock) {
		Socket_SetTimeoutBeforeBlockingOutputRuntimeMonitor.getoutputEvent(sock, output);
	}

	after (OutputStream output) : Socket_SetTimeoutBeforeBlockingOutput_enter(output) {
		Socket_SetTimeoutBeforeBlockingOutputRuntimeMonitor.leaveEvent(output);
	}

}
