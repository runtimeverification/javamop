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

public aspect Collection_UnsynchronizedAddAllMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Collection_UnsynchronizedAddAllMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Collection_UnsynchronizedAddAll_MOPLock = new ReentrantLock();
	static Condition Collection_UnsynchronizedAddAll_MOPLock_cond = Collection_UnsynchronizedAddAll_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Collection_UnsynchronizedAddAll_modify(Collection s) : ((call(* Collection+.add*(..)) || call(* Collection+.remove*(..)) || call(* Collection+.clear(..)) || call(* Collection+.retain*(..))) && target(s)) && MOP_CommonPointCut();
	before (Collection s) : Collection_UnsynchronizedAddAll_modify(s) {
		Collection_UnsynchronizedAddAllRuntimeMonitor.modifyEvent(s);
	}

	pointcut Collection_UnsynchronizedAddAll_enter(Collection t, Collection s) : (call(boolean Collection+.addAll(..)) && target(t) && args(s)) && MOP_CommonPointCut();
	before (Collection t, Collection s) : Collection_UnsynchronizedAddAll_enter(t, s) {
		Collection_UnsynchronizedAddAllRuntimeMonitor.enterEvent(t, s);
	}

	after (Collection t, Collection s) : Collection_UnsynchronizedAddAll_enter(t, s) {
		Collection_UnsynchronizedAddAllRuntimeMonitor.leaveEvent(t, s);
	}

}
