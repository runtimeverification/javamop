package mop;
import java.io.*;
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

public aspect InputStream_ReadAheadLimitMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public InputStream_ReadAheadLimitMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock InputStream_ReadAheadLimit_MOPLock = new ReentrantLock();
	static Condition InputStream_ReadAheadLimit_MOPLock_cond = InputStream_ReadAheadLimit_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut InputStream_ReadAheadLimit_badreset(InputStream i) : (call(* InputStream+.reset(..)) && target(i) && if(i instanceof BufferedInputStream || i instanceof DataInputStream || i instanceof LineNumberInputStream)) && MOP_CommonPointCut();
	before (InputStream i) : InputStream_ReadAheadLimit_badreset(i) {
		//InputStream_ReadAheadLimit_goodreset
		InputStream_ReadAheadLimitRuntimeMonitor.goodresetEvent(i);
		//InputStream_ReadAheadLimit_badreset
		InputStream_ReadAheadLimitRuntimeMonitor.badresetEvent(i);
	}

	pointcut InputStream_ReadAheadLimit_mark(InputStream i, int l) : (call(* InputStream+.mark(int)) && target(i) && args(l) && if(i instanceof BufferedInputStream || i instanceof DataInputStream || i instanceof LineNumberInputStream)) && MOP_CommonPointCut();
	before (InputStream i, int l) : InputStream_ReadAheadLimit_mark(i, l) {
		InputStream_ReadAheadLimitRuntimeMonitor.markEvent(i, l);
	}

	pointcut InputStream_ReadAheadLimit_read1(InputStream i) : (call(* InputStream+.read()) && target(i) && if(i instanceof BufferedInputStream || i instanceof DataInputStream || i instanceof LineNumberInputStream)) && MOP_CommonPointCut();
	after (InputStream i) returning (int n) : InputStream_ReadAheadLimit_read1(i) {
		InputStream_ReadAheadLimitRuntimeMonitor.read1Event(i, n);
	}

	pointcut InputStream_ReadAheadLimit_readn(InputStream i) : (call(* InputStream+.read(char[], ..)) && target(i) && if(i instanceof BufferedInputStream || i instanceof DataInputStream || i instanceof LineNumberInputStream)) && MOP_CommonPointCut();
	after (InputStream i) returning (int n) : InputStream_ReadAheadLimit_readn(i) {
		InputStream_ReadAheadLimitRuntimeMonitor.readnEvent(i, n);
	}

}
