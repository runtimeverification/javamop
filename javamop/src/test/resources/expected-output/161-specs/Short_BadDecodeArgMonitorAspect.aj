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

public aspect Short_BadDecodeArgMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Short_BadDecodeArgMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Short_BadDecodeArg_MOPLock = new ReentrantLock();
	static Condition Short_BadDecodeArg_MOPLock_cond = Short_BadDecodeArg_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Short_BadDecodeArg_decode(Short s, String nm) : (call(* Short.decode(String)) && args(nm) && target(s)) && MOP_CommonPointCut();
	before (Short s, String nm) : Short_BadDecodeArg_decode(s, nm) {
		Short_BadDecodeArgRuntimeMonitor.decodeEvent(s, nm);
	}

}
