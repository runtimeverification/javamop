package mop;
import java.io.*;
import java.lang.*;
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

public aspect Long_BadDecodeArgMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Long_BadDecodeArgMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Long_BadDecodeArg_MOPLock = new ReentrantLock();
	static Condition Long_BadDecodeArg_MOPLock_cond = Long_BadDecodeArg_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Long_BadDecodeArg_decode(Long l, String nm) : (call(* Long.decode(String)) && args(nm) && target(l)) && MOP_CommonPointCut();
	before (Long l, String nm) : Long_BadDecodeArg_decode(l, nm) {
		Long_BadDecodeArgRuntimeMonitor.decodeEvent(l, nm);
	}

}
