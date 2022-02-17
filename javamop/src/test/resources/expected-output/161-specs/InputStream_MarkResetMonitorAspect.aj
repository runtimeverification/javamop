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

public aspect InputStream_MarkResetMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public InputStream_MarkResetMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock InputStream_MarkReset_MOPLock = new ReentrantLock();
	static Condition InputStream_MarkReset_MOPLock_cond = InputStream_MarkReset_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut InputStream_MarkReset_mark_or_reset() : ((call(* InputStream+.mark(..)) || call(* InputStream+.reset(..))) && (target(FileInputStream) || target(PushbackInputStream) || target(ObjectInputStream) || target(PipedInputStream) || target(SequenceInputStream))) && MOP_CommonPointCut();
	before () : InputStream_MarkReset_mark_or_reset() {
		InputStream_MarkResetRuntimeMonitor.mark_or_resetEvent();
	}

}
