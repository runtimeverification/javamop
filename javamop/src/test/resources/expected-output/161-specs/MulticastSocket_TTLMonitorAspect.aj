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

public aspect MulticastSocket_TTLMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public MulticastSocket_TTLMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock MulticastSocket_TTL_MOPLock = new ReentrantLock();
	static Condition MulticastSocket_TTL_MOPLock_cond = MulticastSocket_TTL_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut MulticastSocket_TTL_set2(int ttl) : (call(* MulticastSocket+.setTimeToLive(int)) && args(ttl)) && MOP_CommonPointCut();
	before (int ttl) : MulticastSocket_TTL_set2(ttl) {
		MulticastSocket_TTLRuntimeMonitor.set2Event(ttl);
	}

	pointcut MulticastSocket_TTL_set1(byte ttl) : (call(* MulticastSocket+.setTTL(byte)) && args(ttl)) && MOP_CommonPointCut();
	before (byte ttl) : MulticastSocket_TTL_set1(ttl) {
		MulticastSocket_TTLRuntimeMonitor.set1Event(ttl);
	}

}
