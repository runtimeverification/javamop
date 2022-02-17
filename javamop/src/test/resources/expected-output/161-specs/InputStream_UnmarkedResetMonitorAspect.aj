package mop;
import java.io.*;
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

public aspect InputStream_UnmarkedResetMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public InputStream_UnmarkedResetMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock InputStream_UnmarkedReset_MOPLock = new ReentrantLock();
	static Condition InputStream_UnmarkedReset_MOPLock_cond = InputStream_UnmarkedReset_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut InputStream_UnmarkedReset_reset(InputStream i) : (call(* InputStream+.reset(..)) && target(i) && if(i instanceof BufferedInputStream || i instanceof DataInputStream || i instanceof LineNumberInputStream)) && MOP_CommonPointCut();
	before (InputStream i) : InputStream_UnmarkedReset_reset(i) {
		InputStream_UnmarkedResetRuntimeMonitor.resetEvent(i);
	}

	pointcut InputStream_UnmarkedReset_mark(InputStream i) : (call(* InputStream+.mark(..)) && target(i) && if(i instanceof BufferedInputStream || i instanceof DataInputStream || i instanceof LineNumberInputStream)) && MOP_CommonPointCut();
	before (InputStream i) : InputStream_UnmarkedReset_mark(i) {
		InputStream_UnmarkedResetRuntimeMonitor.markEvent(i);
	}

}
