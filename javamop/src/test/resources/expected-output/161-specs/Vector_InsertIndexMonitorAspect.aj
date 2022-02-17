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

public aspect Vector_InsertIndexMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Vector_InsertIndexMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Vector_InsertIndex_MOPLock = new ReentrantLock();
	static Condition Vector_InsertIndex_MOPLock_cond = Vector_InsertIndex_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Vector_InsertIndex_insert(Vector v, int index) : (call(* Vector+.insertElementAt(Object, int)) && target(v) && args(.., index)) && MOP_CommonPointCut();
	before (Vector v, int index) : Vector_InsertIndex_insert(v, index) {
		Vector_InsertIndexRuntimeMonitor.insertEvent(v, index);
	}

}
