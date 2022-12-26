package mop;
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

public aspect Collections_CopySizeMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Collections_CopySizeMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Collections_CopySize_MOPLock = new ReentrantLock();
	static Condition Collections_CopySize_MOPLock_cond = Collections_CopySize_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Collections_CopySize_bad_copy(List dest, List src) : (call(void Collections.copy(List, List)) && args(dest, src)) && MOP_CommonPointCut();
	before (List dest, List src) : Collections_CopySize_bad_copy(dest, src) {
		Collections_CopySizeRuntimeMonitor.bad_copyEvent(dest, src);
	}

}
