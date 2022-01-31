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

public aspect Map_UnsynchronizedAddAllMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Map_UnsynchronizedAddAllMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Map_UnsynchronizedAddAll_MOPLock = new ReentrantLock();
	static Condition Map_UnsynchronizedAddAll_MOPLock_cond = Map_UnsynchronizedAddAll_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Map_UnsynchronizedAddAll_modify(Map s) : ((call(* Map+.clear(..)) || call(* Map+.put*(..)) || call(* Map+.remove*(..))) && target(s)) && MOP_CommonPointCut();
	before (Map s) : Map_UnsynchronizedAddAll_modify(s) {
		Map_UnsynchronizedAddAllRuntimeMonitor.modifyEvent(s);
	}

	pointcut Map_UnsynchronizedAddAll_enter(Map t, Map s) : (call(boolean Map+.putAll(..)) && target(t) && args(s)) && MOP_CommonPointCut();
	before (Map t, Map s) : Map_UnsynchronizedAddAll_enter(t, s) {
		Map_UnsynchronizedAddAllRuntimeMonitor.enterEvent(t, s);
	}

	pointcut Map_UnsynchronizedAddAll_leave(Map t, Map s) : (call(void Map+.putAll(..)) && target(t) && args(s)) && MOP_CommonPointCut();
	after (Map t, Map s) : Map_UnsynchronizedAddAll_leave(t, s) {
		Map_UnsynchronizedAddAllRuntimeMonitor.leaveEvent(t, s);
	}

}
