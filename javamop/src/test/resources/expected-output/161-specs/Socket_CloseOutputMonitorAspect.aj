package mop;
import java.net.*;
import com.runtimeverification.rvmonitor.java.rt.RVMLogging;
import com.runtimeverification.rvmonitor.java.rt.RVMLogging.Level;
import java.io.OutputStream;
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

public aspect Socket_CloseOutputMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Socket_CloseOutputMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Socket_CloseOutput_MOPLock = new ReentrantLock();
	static Condition Socket_CloseOutput_MOPLock_cond = Socket_CloseOutput_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Socket_CloseOutput_use(OutputStream output) : (call(* OutputStream+.*(..)) && target(output)) && MOP_CommonPointCut();
	before (OutputStream output) : Socket_CloseOutput_use(output) {
		Socket_CloseOutputRuntimeMonitor.useEvent(output);
	}

	pointcut Socket_CloseOutput_close(Socket sock) : ((call(* Socket+.close(..)) || call(* Socket+.shutdownOutput(..))) && target(sock)) && MOP_CommonPointCut();
	before (Socket sock) : Socket_CloseOutput_close(sock) {
		Socket_CloseOutputRuntimeMonitor.closeEvent(sock);
	}

	pointcut Socket_CloseOutput_getoutput(Socket sock) : (call(OutputStream Socket+.getOutputStream()) && target(sock)) && MOP_CommonPointCut();
	after (Socket sock) returning (OutputStream output) : Socket_CloseOutput_getoutput(sock) {
		Socket_CloseOutputRuntimeMonitor.getoutputEvent(sock, output);
	}

}
