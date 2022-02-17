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

public aspect PipedInputStream_UnconnectedReadMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public PipedInputStream_UnconnectedReadMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock PipedInputStream_UnconnectedRead_MOPLock = new ReentrantLock();
	static Condition PipedInputStream_UnconnectedRead_MOPLock_cond = PipedInputStream_UnconnectedRead_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut PipedInputStream_UnconnectedRead_read(PipedInputStream i) : ((call(* PipedInputStream+.read(..)) || call(* PipedInputStream+.receive(..)) || call(* PipedInputStream+.available(..))) && target(i)) && MOP_CommonPointCut();
	before (PipedInputStream i) : PipedInputStream_UnconnectedRead_read(i) {
		PipedInputStream_UnconnectedReadRuntimeMonitor.readEvent(i);
	}

	pointcut PipedInputStream_UnconnectedRead_connect2(PipedInputStream i) : (call(* PipedInputStream+.connect(PipedOutputStream+)) && target(i)) && MOP_CommonPointCut();
	before (PipedInputStream i) : PipedInputStream_UnconnectedRead_connect2(i) {
		PipedInputStream_UnconnectedReadRuntimeMonitor.connect2Event(i);
	}

	pointcut PipedInputStream_UnconnectedRead_connect1(PipedInputStream i) : (call(* PipedOutputStream+.connect(PipedInputStream+)) && args(i)) && MOP_CommonPointCut();
	before (PipedInputStream i) : PipedInputStream_UnconnectedRead_connect1(i) {
		PipedInputStream_UnconnectedReadRuntimeMonitor.connect1Event(i);
	}

	pointcut PipedInputStream_UnconnectedRead_create_io(PipedInputStream i) : (call(PipedOutputStream+.new(PipedInputStream+)) && args(i)) && MOP_CommonPointCut();
	before (PipedInputStream i) : PipedInputStream_UnconnectedRead_create_io(i) {
		PipedInputStream_UnconnectedReadRuntimeMonitor.create_ioEvent(i);
	}

	pointcut PipedInputStream_UnconnectedRead_create() : (call(PipedInputStream+.new())) && MOP_CommonPointCut();
	after () returning (PipedInputStream i) : PipedInputStream_UnconnectedRead_create() {
		PipedInputStream_UnconnectedReadRuntimeMonitor.createEvent(i);
	}

	pointcut PipedInputStream_UnconnectedRead_create_oi() : (call(PipedInputStream+.new(PipedOutputStream+))) && MOP_CommonPointCut();
	after () returning (PipedInputStream i) : PipedInputStream_UnconnectedRead_create_oi() {
		PipedInputStream_UnconnectedReadRuntimeMonitor.create_oiEvent(i);
	}

}
