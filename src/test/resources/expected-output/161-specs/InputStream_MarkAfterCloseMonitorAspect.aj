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

public aspect InputStream_MarkAfterCloseMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public InputStream_MarkAfterCloseMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock InputStream_MarkAfterClose_MOPLock = new ReentrantLock();
	static Condition InputStream_MarkAfterClose_MOPLock_cond = InputStream_MarkAfterClose_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut InputStream_MarkAfterClose_close(InputStream i) : (call(* InputStream+.close(..)) && target(i)) && MOP_CommonPointCut();
	before (InputStream i) : InputStream_MarkAfterClose_close(i) {
		InputStream_MarkAfterCloseRuntimeMonitor.closeEvent(i);
	}

	pointcut InputStream_MarkAfterClose_mark(InputStream i) : (call(* InputStream+.mark(..)) && target(i)) && MOP_CommonPointCut();
	before (InputStream i) : InputStream_MarkAfterClose_mark(i) {
		InputStream_MarkAfterCloseRuntimeMonitor.markEvent(i);
	}

}
