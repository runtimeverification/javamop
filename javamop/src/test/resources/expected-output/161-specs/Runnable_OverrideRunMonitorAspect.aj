package mop;
import java.io.*;
import java.lang.*;
import java.lang.reflect.*;
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

public aspect Runnable_OverrideRunMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Runnable_OverrideRunMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Runnable_OverrideRun_MOPLock = new ReentrantLock();
	static Condition Runnable_OverrideRun_MOPLock_cond = Runnable_OverrideRun_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Runnable_OverrideRun_staticinit() : (staticinitialization(Runnable+)) && MOP_CommonPointCut();
	after () : Runnable_OverrideRun_staticinit() {
		Runnable_OverrideRunRuntimeMonitor.staticinitEvent(thisJoinPoint.getStaticPart().getSignature());
	}

}
