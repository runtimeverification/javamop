package mop;
import java.io.*;
import java.util.*;
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

public aspect SafeFileWriterMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public SafeFileWriterMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock SafeFileWriter_MOPLock = new ReentrantLock();
	static Condition SafeFileWriter_MOPLock_cond = SafeFileWriter_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut SafeFileWriter_write(FileWriter f) : (call(* write(..)) && target(f)) && MOP_CommonPointCut();
	before (FileWriter f) : SafeFileWriter_write(f) {
		SafeFileWriterRuntimeMonitor.writeEvent(f);
	}

	pointcut SafeFileWriter_open() : (call(FileWriter.new(..))) && MOP_CommonPointCut();
	after () returning (FileWriter f) : SafeFileWriter_open() {
		SafeFileWriterRuntimeMonitor.openEvent(f);
	}

	pointcut SafeFileWriter_close(FileWriter f) : (call(* close(..)) && target(f)) && MOP_CommonPointCut();
	after (FileWriter f) : SafeFileWriter_close(f) {
		SafeFileWriterRuntimeMonitor.closeEvent(f);
	}

}
