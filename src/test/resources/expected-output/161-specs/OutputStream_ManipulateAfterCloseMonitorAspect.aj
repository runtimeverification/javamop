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

public aspect OutputStream_ManipulateAfterCloseMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public OutputStream_ManipulateAfterCloseMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock OutputStream_ManipulateAfterClose_MOPLock = new ReentrantLock();
	static Condition OutputStream_ManipulateAfterClose_MOPLock_cond = OutputStream_ManipulateAfterClose_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut OutputStream_ManipulateAfterClose_close(OutputStream o) : (call(* OutputStream+.close(..)) && target(o) && !target(ByteArrayOutputStream)) && MOP_CommonPointCut();
	before (OutputStream o) : OutputStream_ManipulateAfterClose_close(o) {
		OutputStream_ManipulateAfterCloseRuntimeMonitor.closeEvent(o);
	}

	pointcut OutputStream_ManipulateAfterClose_manipulate(OutputStream o) : ((call(* OutputStream+.write*(..)) || call(* OutputStream+.flush(..))) && target(o) && !target(ByteArrayOutputStream)) && MOP_CommonPointCut();
	before (OutputStream o) : OutputStream_ManipulateAfterClose_manipulate(o) {
		OutputStream_ManipulateAfterCloseRuntimeMonitor.manipulateEvent(o);
	}

}
