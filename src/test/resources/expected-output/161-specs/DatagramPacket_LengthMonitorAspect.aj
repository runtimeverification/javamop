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

public aspect DatagramPacket_LengthMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public DatagramPacket_LengthMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock DatagramPacket_Length_MOPLock = new ReentrantLock();
	static Condition DatagramPacket_Length_MOPLock_cond = DatagramPacket_Length_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut DatagramPacket_Length_construct_offlen(byte[] buffer, int offset, int length) : ((call(DatagramPacket.new(byte[], int, int)) || call(DatagramPacket.new(byte[], int, int, InetAddress, int)) || call(DatagramPacket.new(byte[], int, int, SocketAddress))) && args(buffer, offset, length, ..)) && MOP_CommonPointCut();
	before (byte[] buffer, int offset, int length) : DatagramPacket_Length_construct_offlen(buffer, offset, length) {
		DatagramPacket_LengthRuntimeMonitor.construct_offlenEvent(buffer, offset, length);
	}

	pointcut DatagramPacket_Length_construct_len(byte[] buffer, int length) : ((call(DatagramPacket.new(byte[], int)) || call(DatagramPacket.new(byte[], int, InetAddress, int)) || call(DatagramPacket.new(byte[], int, SocketAddress))) && args(buffer, length, ..)) && MOP_CommonPointCut();
	before (byte[] buffer, int length) : DatagramPacket_Length_construct_len(buffer, length) {
		DatagramPacket_LengthRuntimeMonitor.construct_lenEvent(buffer, length);
	}

}
