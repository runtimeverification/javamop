package mop;
import java.io.*;
import java.lang.*;
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

public aspect System_NullArrayCopyMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public System_NullArrayCopyMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock System_NullArrayCopy_MOPLock = new ReentrantLock();
	static Condition System_NullArrayCopy_MOPLock_cond = System_NullArrayCopy_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut System_NullArrayCopy_null_arraycopy(Object src, int srcPos, Object dest, int destPos, int length) : (call(* System.arraycopy(Object, int, Object, int, int)) && args(src, srcPos, dest, destPos, length)) && MOP_CommonPointCut();
	before (Object src, int srcPos, Object dest, int destPos, int length) : System_NullArrayCopy_null_arraycopy(src, srcPos, dest, destPos, length) {
		System_NullArrayCopyRuntimeMonitor.null_arraycopyEvent(src, srcPos, dest, destPos, length);
	}

}
