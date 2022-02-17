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

public aspect Long_BadParsingArgsMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Long_BadParsingArgsMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Long_BadParsingArgs_MOPLock = new ReentrantLock();
	static Condition Long_BadParsingArgs_MOPLock_cond = Long_BadParsingArgs_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Long_BadParsingArgs_bad_arg2(String s) : (call(* Long.parseLong(String)) && args(s)) && MOP_CommonPointCut();
	before (String s) : Long_BadParsingArgs_bad_arg2(s) {
		Long_BadParsingArgsRuntimeMonitor.bad_arg2Event(s);
	}

	pointcut Long_BadParsingArgs_bad_arg(String s, int radix) : (call(* Long.parseLong(String, int)) && args(s, radix)) && MOP_CommonPointCut();
	before (String s, int radix) : Long_BadParsingArgs_bad_arg(s, radix) {
		Long_BadParsingArgsRuntimeMonitor.bad_argEvent(s, radix);
	}

}
