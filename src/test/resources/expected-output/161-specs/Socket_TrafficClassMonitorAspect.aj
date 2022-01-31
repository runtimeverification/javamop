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

public aspect Socket_TrafficClassMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Socket_TrafficClassMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Socket_TrafficClass_MOPLock = new ReentrantLock();
	static Condition Socket_TrafficClass_MOPLock_cond = Socket_TrafficClass_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Socket_TrafficClass_settc(Socket socket, int tc) : (call(void Socket.setTrafficClass(int)) && target(socket) && args(tc)) && MOP_CommonPointCut();
	before (Socket socket, int tc) : Socket_TrafficClass_settc(socket, tc) {
		Socket_TrafficClassRuntimeMonitor.settcEvent(socket, tc);
	}

}
