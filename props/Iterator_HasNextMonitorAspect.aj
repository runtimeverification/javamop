package mop;
import java.util.*;
import com.runtimeverification.rvmonitor.java.rt.RVMLogging;
import com.runtimeverification.rvmonitor.java.rt.RVMLogging.Level;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

import java.lang.ref.*;
import org.aspectj.lang.*;

public aspect Iterator_HasNextMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Iterator_HasNextMonitorAspect(){
	}

	// advices for Statistics
	after () : execution(* org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(..)) {
		System.err.println("==start Iterator_HasNext ==");
		System.err.println("#monitors: " + Iterator_HasNextMonitor.getTotalMonitorCount());
		System.err.println("#collected monitors: " + Iterator_HasNextMonitor.getCollectedMonitorCount());
		System.err.println("#terminated monitors: " + Iterator_HasNextMonitor.getTerminatedMonitorCount());
		System.err.println("#event - next: " + Iterator_HasNextMonitor.getEventCounters().get("next"));
		System.err.println("#event - hasnexttrue: " + Iterator_HasNextMonitor.getEventCounters().get("hasnexttrue"));
		System.err.println("#event - hasnextfalse: " + Iterator_HasNextMonitor.getEventCounters().get("hasnextfalse"));
		System.err.println("#category - prop 1 - violation: " + Iterator_HasNextMonitor.getCategoryCounters().get("violation"));
		System.err.println("==end Iterator_HasNext ==");
	}
	// Declarations for the Lock
	static ReentrantLock Iterator_HasNext_MOPLock = new ReentrantLock();
	static Condition Iterator_HasNext_MOPLock_cond = Iterator_HasNext_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Iterator_HasNext_next(Iterator i) : (call(* Iterator+.next()) && target(i)) && MOP_CommonPointCut();
	before (Iterator i) : Iterator_HasNext_next(i) {
		MultiSpec_1RuntimeMonitor.Iterator_HasNext_nextEvent(i);
	}

	pointcut Iterator_HasNext_hasnexttrue(Iterator i) : (call(* Iterator+.hasNext()) && target(i)) && MOP_CommonPointCut();
	after (Iterator i) returning (boolean b) : Iterator_HasNext_hasnexttrue(i) {
		//Iterator_HasNext_hasnexttrue
		MultiSpec_1RuntimeMonitor.Iterator_HasNext_hasnexttrueEvent(i, b);
		//Iterator_HasNext_hasnextfalse
		MultiSpec_1RuntimeMonitor.Iterator_HasNext_hasnextfalseEvent(i, b);
	}

}
