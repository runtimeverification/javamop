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

public aspect ClassLoader_UnsafeClassDefinitionMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public ClassLoader_UnsafeClassDefinitionMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock ClassLoader_UnsafeClassDefinition_MOPLock = new ReentrantLock();
	static Condition ClassLoader_UnsafeClassDefinition_MOPLock_cond = ClassLoader_UnsafeClassDefinition_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut ClassLoader_UnsafeClassDefinition_defineClass(String name) : (call(* ClassLoader+.defineClass(String, ..)) && args(name, ..)) && MOP_CommonPointCut();
	before (String name) : ClassLoader_UnsafeClassDefinition_defineClass(name) {
		ClassLoader_UnsafeClassDefinitionRuntimeMonitor.defineClassEvent(name);
	}

}
