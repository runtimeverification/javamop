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

public aspect SafeFileMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public SafeFileMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock SafeFile_MOPLock = new ReentrantLock();
	static Condition SafeFile_MOPLock_cond = SafeFile_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut SafeFile_begin() : (execution(* *.*(..))) && MOP_CommonPointCut();
	before () : SafeFile_begin() {
		Thread t = Thread.currentThread();
		SafeFileRuntimeMonitor.beginEvent(t);
	}

	pointcut SafeFile_open() : (call(FileReader.new(..))) && MOP_CommonPointCut();
	after () returning (FileReader f) : SafeFile_open() {
		Thread t = Thread.currentThread();
		SafeFileRuntimeMonitor.openEvent(t, f);
	}

	pointcut SafeFile_close(FileReader f) : (call(* FileReader.close(..)) && target(f)) && MOP_CommonPointCut();
	after (FileReader f) : SafeFile_close(f) {
		Thread t = Thread.currentThread();
		SafeFileRuntimeMonitor.closeEvent(f, t);
	}

	after () : SafeFile_begin() {
		Thread t = Thread.currentThread();
		SafeFileRuntimeMonitor.endEvent(t);
	}

}
