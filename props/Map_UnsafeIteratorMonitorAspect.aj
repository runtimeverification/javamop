package mop;
import java.util.*;
import java.lang.*;
import com.runtimeverification.rvmonitor.java.rt.RVMLogging;
import com.runtimeverification.rvmonitor.java.rt.RVMLogging.Level;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

import java.lang.ref.*;
import org.aspectj.lang.*;

public aspect Map_UnsafeIteratorMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Map_UnsafeIteratorMonitorAspect(){
	}

	// advices for Statistics
	after () : execution(* org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(..)) {
		System.err.println("==start Map_UnsafeIterator ==");
		System.err.println("#monitors: " + Map_UnsafeIteratorMonitor.getTotalMonitorCount());
		System.err.println("#collected monitors: " + Map_UnsafeIteratorMonitor.getCollectedMonitorCount());
		System.err.println("#terminated monitors: " + Map_UnsafeIteratorMonitor.getTerminatedMonitorCount());
		System.err.println("#event - getiter: " + Map_UnsafeIteratorMonitor.getEventCounters().get("getiter"));
		System.err.println("#event - modifyCol: " + Map_UnsafeIteratorMonitor.getEventCounters().get("modifyCol"));
		System.err.println("#event - getset: " + Map_UnsafeIteratorMonitor.getEventCounters().get("getset"));
		System.err.println("#event - modifyMap: " + Map_UnsafeIteratorMonitor.getEventCounters().get("modifyMap"));
		System.err.println("#event - useiter: " + Map_UnsafeIteratorMonitor.getEventCounters().get("useiter"));
		System.err.println("#category - prop 1 - match: " + Map_UnsafeIteratorMonitor.getCategoryCounters().get("match"));
		System.err.println("==end Map_UnsafeIterator ==");
	}
	// Declarations for the Lock
	static ReentrantLock Map_UnsafeIterator_MOPLock = new ReentrantLock();
	static Condition Map_UnsafeIterator_MOPLock_cond = Map_UnsafeIterator_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Map_UnsafeIterator_useiter(Iterator i) : ((call(* Iterator.hasNext(..)) || call(* Iterator.next(..))) && target(i)) && MOP_CommonPointCut();
	before (Iterator i) : Map_UnsafeIterator_useiter(i) {
		MultiSpec_1RuntimeMonitor.Map_UnsafeIterator_useiterEvent(i);
	}

	pointcut Map_UnsafeIterator_modifyCol(Collection c) : ((call(* Collection+.clear(..)) || call(* Collection+.offer*(..)) || call(* Collection+.pop(..)) || call(* Collection+.push(..)) || call(* Collection+.remove*(..)) || call(* Collection+.retain*(..))) && target(c)) && MOP_CommonPointCut();
	before (Collection c) : Map_UnsafeIterator_modifyCol(c) {
		MultiSpec_1RuntimeMonitor.Map_UnsafeIterator_modifyColEvent(c);
	}

	pointcut Map_UnsafeIterator_modifyMap(Map m) : ((call(* Map+.clear*(..)) || call(* Map+.put*(..)) || call(* Map+.remove(..))) && target(m)) && MOP_CommonPointCut();
	before (Map m) : Map_UnsafeIterator_modifyMap(m) {
		MultiSpec_1RuntimeMonitor.Map_UnsafeIterator_modifyMapEvent(m);
	}

	pointcut Map_UnsafeIterator_getset(Map m) : ((call(Set Map+.keySet()) || call(Set Map+.entrySet()) || call(Collection Map+.values())) && target(m)) && MOP_CommonPointCut();
	after (Map m) returning (Collection c) : Map_UnsafeIterator_getset(m) {
		MultiSpec_1RuntimeMonitor.Map_UnsafeIterator_getsetEvent(m, c);
	}

	pointcut Map_UnsafeIterator_getiter(Collection c) : (call(Iterator Iterable+.iterator()) && target(c)) && MOP_CommonPointCut();
	after (Collection c) returning (Iterator i) : Map_UnsafeIterator_getiter(c) {
		MultiSpec_1RuntimeMonitor.Map_UnsafeIterator_getiterEvent(c, i);
	}

}
