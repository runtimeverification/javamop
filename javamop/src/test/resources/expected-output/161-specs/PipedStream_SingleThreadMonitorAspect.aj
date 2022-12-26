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

public aspect PipedStream_SingleThreadMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public PipedStream_SingleThreadMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock PipedStream_SingleThread_MOPLock = new ReentrantLock();
	static Condition PipedStream_SingleThread_MOPLock_cond = PipedStream_SingleThread_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut PipedStream_SingleThread_read(PipedInputStream i) : (call(* InputStream+.read(..)) && target(i)) && MOP_CommonPointCut();
	before (PipedInputStream i) : PipedStream_SingleThread_read(i) {
		Thread t = Thread.currentThread();
		PipedStream_SingleThreadRuntimeMonitor.readEvent(i, t);
	}

	pointcut PipedStream_SingleThread_write(PipedOutputStream o) : (call(* OutputStream+.write(..)) && target(o)) && MOP_CommonPointCut();
	before (PipedOutputStream o) : PipedStream_SingleThread_write(o) {
		Thread t = Thread.currentThread();
		PipedStream_SingleThreadRuntimeMonitor.writeEvent(o, t);
	}

	pointcut PipedStream_SingleThread_create4(PipedOutputStream o, PipedInputStream i) : (call(* PipedOutputStream+.connect(PipedInputStream+)) && target(o) && args(i)) && MOP_CommonPointCut();
	before (PipedOutputStream o, PipedInputStream i) : PipedStream_SingleThread_create4(o, i) {
		PipedStream_SingleThreadRuntimeMonitor.create4Event(o, i);
	}

	pointcut PipedStream_SingleThread_create2(PipedInputStream i, PipedOutputStream o) : (call(* PipedInputStream+.connect(PipedOutputStream+)) && target(i) && args(o)) && MOP_CommonPointCut();
	before (PipedInputStream i, PipedOutputStream o) : PipedStream_SingleThread_create2(i, o) {
		PipedStream_SingleThreadRuntimeMonitor.create2Event(i, o);
	}

	pointcut PipedStream_SingleThread_create1(PipedOutputStream o) : (call(PipedInputStream+.new(PipedOutputStream+)) && args(o)) && MOP_CommonPointCut();
	after (PipedOutputStream o) returning (PipedInputStream i) : PipedStream_SingleThread_create1(o) {
		PipedStream_SingleThreadRuntimeMonitor.create1Event(o, i);
	}

	pointcut PipedStream_SingleThread_create3(PipedInputStream i) : (call(PipedOutputStream+.new(PipedInputStream+)) && args(i)) && MOP_CommonPointCut();
	after (PipedInputStream i) returning (PipedOutputStream o) : PipedStream_SingleThread_create3(i) {
		PipedStream_SingleThreadRuntimeMonitor.create3Event(i, o);
	}

}
