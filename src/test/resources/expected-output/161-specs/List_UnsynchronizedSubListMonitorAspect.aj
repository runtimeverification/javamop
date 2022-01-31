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

public aspect List_UnsynchronizedSubListMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public List_UnsynchronizedSubListMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock List_UnsynchronizedSubList_MOPLock = new ReentrantLock();
	static Condition List_UnsynchronizedSubList_MOPLock_cond = List_UnsynchronizedSubList_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut List_UnsynchronizedSubList_usesublist(List s) : (call(* List.*(..)) && target(s)) && MOP_CommonPointCut();
	before (List s) : List_UnsynchronizedSubList_usesublist(s) {
		List_UnsynchronizedSubListRuntimeMonitor.usesublistEvent(s);
	}

	pointcut List_UnsynchronizedSubList_modifybackinglist(List b) : ((call(* Collection+.add*(..)) || call(* Collection+.remove*(..)) || call(* Collection+.clear(..)) || call(* Collection+.retain*(..))) && target(b)) && MOP_CommonPointCut();
	before (List b) : List_UnsynchronizedSubList_modifybackinglist(b) {
		List_UnsynchronizedSubListRuntimeMonitor.modifybackinglistEvent(b);
	}

	pointcut List_UnsynchronizedSubList_createsublist(List b) : (call(* List.subList(..)) && target(b)) && MOP_CommonPointCut();
	after (List b) returning (List s) : List_UnsynchronizedSubList_createsublist(b) {
		List_UnsynchronizedSubListRuntimeMonitor.createsublistEvent(b, s);
	}

}
