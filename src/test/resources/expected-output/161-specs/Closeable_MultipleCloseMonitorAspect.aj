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

public aspect Closeable_MultipleCloseMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Closeable_MultipleCloseMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Closeable_MultipleClose_MOPLock = new ReentrantLock();
	static Condition Closeable_MultipleClose_MOPLock_cond = Closeable_MultipleClose_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Closeable_MultipleClose_close(Closeable c) : (call(* Closeable+.close(..)) && target(c)) && MOP_CommonPointCut();
	before (Closeable c) : Closeable_MultipleClose_close(c) {
		Closeable_MultipleCloseRuntimeMonitor.closeEvent(c);
	}

}
