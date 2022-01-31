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

public aspect Reader_MarkResetMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Reader_MarkResetMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Reader_MarkReset_MOPLock = new ReentrantLock();
	static Condition Reader_MarkReset_MOPLock_cond = Reader_MarkReset_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Reader_MarkReset_reset(Reader r) : (call(* Reader+.reset(..)) && target(r) && (target(PushbackReader) || target(InputStreamReader) || target(FileReader) || target(PipedReader))) && MOP_CommonPointCut();
	before (Reader r) : Reader_MarkReset_reset(r) {
		Reader_MarkResetRuntimeMonitor.resetEvent(r);
	}

	pointcut Reader_MarkReset_mark(Reader r) : (call(* Reader+.mark(..)) && target(r) && (target(PushbackReader) || target(InputStreamReader) || target(FileReader) || target(PipedReader))) && MOP_CommonPointCut();
	before (Reader r) : Reader_MarkReset_mark(r) {
		Reader_MarkResetRuntimeMonitor.markEvent(r);
	}

}
