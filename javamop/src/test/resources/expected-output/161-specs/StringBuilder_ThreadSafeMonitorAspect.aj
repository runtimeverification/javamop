package mop;
import java.io.*;
import java.lang.*;
import java.util.*;
import com.runtimeverification.rvmonitor.java.rt.RVMLogging;
import com.runtimeverification.rvmonitor.java.rt.RVMLogging.Level;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

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

public aspect StringBuilder_ThreadSafeMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public StringBuilder_ThreadSafeMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock StringBuilder_ThreadSafe_MOPLock = new ReentrantLock();
	static Condition StringBuilder_ThreadSafe_MOPLock_cond = StringBuilder_ThreadSafe_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut StringBuilder_ThreadSafe_safe_oper(StringBuilder b) : (call(* StringBuilder.*(..)) && target(b)) && MOP_CommonPointCut();
	before (StringBuilder b) : StringBuilder_ThreadSafe_safe_oper(b) {
		Thread t = Thread.currentThread();
		//StringBuilder_ThreadSafe_unsafe_oper
		StringBuilder_ThreadSafeRuntimeMonitor.unsafe_operEvent(b, t);
		//StringBuilder_ThreadSafe_safe_oper
		StringBuilder_ThreadSafeRuntimeMonitor.safe_operEvent(b, t);
	}

}
