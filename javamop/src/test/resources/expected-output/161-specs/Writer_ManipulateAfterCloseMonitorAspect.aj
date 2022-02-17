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

public aspect Writer_ManipulateAfterCloseMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Writer_ManipulateAfterCloseMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Writer_ManipulateAfterClose_MOPLock = new ReentrantLock();
	static Condition Writer_ManipulateAfterClose_MOPLock_cond = Writer_ManipulateAfterClose_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Writer_ManipulateAfterClose_close(Writer w) : (call(* Writer+.close(..)) && target(w) && !target(CharArrayWriter) && !target(StringWriter)) && MOP_CommonPointCut();
	before (Writer w) : Writer_ManipulateAfterClose_close(w) {
		Writer_ManipulateAfterCloseRuntimeMonitor.closeEvent(w);
	}

	pointcut Writer_ManipulateAfterClose_manipulate(Writer w) : ((call(* Writer+.write*(..)) || call(* Writer+.flush(..))) && target(w) && !target(CharArrayWriter) && !target(StringWriter)) && MOP_CommonPointCut();
	before (Writer w) : Writer_ManipulateAfterClose_manipulate(w) {
		Writer_ManipulateAfterCloseRuntimeMonitor.manipulateEvent(w);
	}

}
