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

public aspect Console_CloseReaderMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Console_CloseReaderMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Console_CloseReader_MOPLock = new ReentrantLock();
	static Condition Console_CloseReader_MOPLock_cond = Console_CloseReader_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Console_CloseReader_close(Reader r) : (call(* Reader+.close(..)) && target(r)) && MOP_CommonPointCut();
	before (Reader r) : Console_CloseReader_close(r) {
		Console_CloseReaderRuntimeMonitor.closeEvent(r);
	}

	pointcut Console_CloseReader_getreader() : (call(Reader+ Console+.reader())) && MOP_CommonPointCut();
	after () returning (Reader r) : Console_CloseReader_getreader() {
		Console_CloseReaderRuntimeMonitor.getreaderEvent(r);
	}

}
