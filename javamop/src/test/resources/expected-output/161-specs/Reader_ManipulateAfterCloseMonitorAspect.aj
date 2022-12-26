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

public aspect Reader_ManipulateAfterCloseMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Reader_ManipulateAfterCloseMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Reader_ManipulateAfterClose_MOPLock = new ReentrantLock();
	static Condition Reader_ManipulateAfterClose_MOPLock_cond = Reader_ManipulateAfterClose_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Reader_ManipulateAfterClose_close(Reader r) : (call(* Reader+.close(..)) && target(r)) && MOP_CommonPointCut();
	before (Reader r) : Reader_ManipulateAfterClose_close(r) {
		Reader_ManipulateAfterCloseRuntimeMonitor.closeEvent(r);
	}

	pointcut Reader_ManipulateAfterClose_manipulate(Reader r) : ((call(* Reader+.read(..)) || call(* Reader+.ready(..)) || call(* Reader+.mark(..)) || call(* Reader+.reset(..)) || call(* Reader+.skip(..))) && target(r)) && MOP_CommonPointCut();
	before (Reader r) : Reader_ManipulateAfterClose_manipulate(r) {
		Reader_ManipulateAfterCloseRuntimeMonitor.manipulateEvent(r);
	}

}
