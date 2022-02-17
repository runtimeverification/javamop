package mop;
import java.io.*;
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

public aspect Serializable_NoArgConstructorMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Serializable_NoArgConstructorMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Serializable_NoArgConstructor_MOPLock = new ReentrantLock();
	static Condition Serializable_NoArgConstructor_MOPLock_cond = Serializable_NoArgConstructor_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Serializable_NoArgConstructor_staticinit() : (staticinitialization(Serializable+)) && MOP_CommonPointCut();
	after () : Serializable_NoArgConstructor_staticinit() {
		Serializable_NoArgConstructorRuntimeMonitor.staticinitEvent(thisJoinPoint.getStaticPart().getSignature());
	}

}
