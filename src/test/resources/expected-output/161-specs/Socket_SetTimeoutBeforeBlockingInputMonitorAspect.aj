package mop;
import java.net.*;
import java.io.InputStream;
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

public aspect Socket_SetTimeoutBeforeBlockingInputMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Socket_SetTimeoutBeforeBlockingInputMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Socket_SetTimeoutBeforeBlockingInput_MOPLock = new ReentrantLock();
	static Condition Socket_SetTimeoutBeforeBlockingInput_MOPLock_cond = Socket_SetTimeoutBeforeBlockingInput_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Socket_SetTimeoutBeforeBlockingInput_set(Socket sock, int timeout) : (call(* Socket+.setSoTimeout(int)) && target(sock) && args(timeout)) && MOP_CommonPointCut();
	before (Socket sock, int timeout) : Socket_SetTimeoutBeforeBlockingInput_set(sock, timeout) {
		Socket_SetTimeoutBeforeBlockingInputRuntimeMonitor.setEvent(sock, timeout);
	}

	pointcut Socket_SetTimeoutBeforeBlockingInput_enter(InputStream input) : (call(* InputStream+.read(..)) && target(input)) && MOP_CommonPointCut();
	before (InputStream input) : Socket_SetTimeoutBeforeBlockingInput_enter(input) {
		Socket_SetTimeoutBeforeBlockingInputRuntimeMonitor.enterEvent(input);
	}

	pointcut Socket_SetTimeoutBeforeBlockingInput_getinput(Socket sock) : (call(InputStream Socket+.getInputStream()) && target(sock)) && MOP_CommonPointCut();
	after (Socket sock) returning (InputStream input) : Socket_SetTimeoutBeforeBlockingInput_getinput(sock) {
		Socket_SetTimeoutBeforeBlockingInputRuntimeMonitor.getinputEvent(sock, input);
	}

	after (InputStream input) : Socket_SetTimeoutBeforeBlockingInput_enter(input) {
		Socket_SetTimeoutBeforeBlockingInputRuntimeMonitor.leaveEvent(input);
	}

}
