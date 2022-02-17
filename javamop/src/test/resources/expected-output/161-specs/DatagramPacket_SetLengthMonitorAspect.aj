package mop;
import java.net.*;
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

public aspect DatagramPacket_SetLengthMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public DatagramPacket_SetLengthMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock DatagramPacket_SetLength_MOPLock = new ReentrantLock();
	static Condition DatagramPacket_SetLength_MOPLock_cond = DatagramPacket_SetLength_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut DatagramPacket_SetLength_setlength(DatagramPacket packet, int length) : (call(void DatagramPacket.setLength(int)) && target(packet) && args(length)) && MOP_CommonPointCut();
	before (DatagramPacket packet, int length) : DatagramPacket_SetLength_setlength(packet, length) {
		DatagramPacket_SetLengthRuntimeMonitor.setlengthEvent(packet, length);
	}

}
