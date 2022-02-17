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

public aspect StringTokenizer_HasMoreElementsMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public StringTokenizer_HasMoreElementsMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock StringTokenizer_HasMoreElements_MOPLock = new ReentrantLock();
	static Condition StringTokenizer_HasMoreElements_MOPLock_cond = StringTokenizer_HasMoreElements_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut StringTokenizer_HasMoreElements_next(StringTokenizer i) : ((call(* StringTokenizer.nextToken()) || call(* StringTokenizer.nextElement())) && target(i)) && MOP_CommonPointCut();
	before (StringTokenizer i) : StringTokenizer_HasMoreElements_next(i) {
		StringTokenizer_HasMoreElementsRuntimeMonitor.nextEvent(i);
	}

	pointcut StringTokenizer_HasMoreElements_hasnexttrue(StringTokenizer i) : ((call(boolean StringTokenizer.hasMoreTokens()) || call(boolean StringTokenizer.hasMoreElements())) && target(i)) && MOP_CommonPointCut();
	after (StringTokenizer i) returning (boolean b) : StringTokenizer_HasMoreElements_hasnexttrue(i) {
		//StringTokenizer_HasMoreElements_hasnexttrue
		StringTokenizer_HasMoreElementsRuntimeMonitor.hasnexttrueEvent(i, b);
		//StringTokenizer_HasMoreElements_hasnextfalse
		StringTokenizer_HasMoreElementsRuntimeMonitor.hasnextfalseEvent(i, b);
	}

}
