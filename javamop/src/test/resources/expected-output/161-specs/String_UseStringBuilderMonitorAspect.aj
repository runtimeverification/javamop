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

public aspect String_UseStringBuilderMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public String_UseStringBuilderMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock String_UseStringBuilder_MOPLock = new ReentrantLock();
	static Condition String_UseStringBuilder_MOPLock_cond = String_UseStringBuilder_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut String_UseStringBuilder_constructor_create() : (call(String.new(StringBuilder))) && MOP_CommonPointCut();
	after () returning (String b) : String_UseStringBuilder_constructor_create() {
		String_UseStringBuilderRuntimeMonitor.constructor_createEvent(b);
	}

}
