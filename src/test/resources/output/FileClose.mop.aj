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

public aspect FileCloseMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public FileCloseMonitorAspect(){
		Runtime.getRuntime().addShutdownHook(new FileClose_DummyHookThread());
	}

	// Declarations for the Lock
	static ReentrantLock FileClose_MOPLock = new ReentrantLock();
	static Condition FileClose_MOPLock_cond = FileClose_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut FileClose_write(FileWriter f) : (call(* FileWriter+.write(..)) && target(f)) && MOP_CommonPointCut();
	before (FileWriter f) : FileClose_write(f) {
		FileCloseRuntimeMonitor.writeEvent(f);
	}

	pointcut FileClose_close(FileWriter f) : (call(* FileWriter+.close(..)) && target(f)) && MOP_CommonPointCut();
	after (FileWriter f) : FileClose_close(f) {
		FileCloseRuntimeMonitor.closeEvent(f);
	}

	class FileClose_DummyHookThread extends Thread {
		public void run(){
			FileCloseRuntimeMonitor.endProgEvent();
		}
	}
}
