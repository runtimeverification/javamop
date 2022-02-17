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

public aspect Socket_LargeReceiveBufferMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Socket_LargeReceiveBufferMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Socket_LargeReceiveBuffer_MOPLock = new ReentrantLock();
	static Condition Socket_LargeReceiveBuffer_MOPLock_cond = Socket_LargeReceiveBuffer_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Socket_LargeReceiveBuffer_set(Socket sock, int size) : (call(* Socket+.setReceiveBufferSize(int)) && target(sock) && args(size)) && MOP_CommonPointCut();
	before (Socket sock, int size) : Socket_LargeReceiveBuffer_set(sock, size) {
		Socket_LargeReceiveBufferRuntimeMonitor.setEvent(sock, size);
	}

	pointcut Socket_LargeReceiveBuffer_connect(Socket sock) : (call(* Socket+.connect(..)) && target(sock)) && MOP_CommonPointCut();
	before (Socket sock) : Socket_LargeReceiveBuffer_connect(sock) {
		Socket_LargeReceiveBufferRuntimeMonitor.connectEvent(sock);
	}

	pointcut Socket_LargeReceiveBuffer_create_connected() : (call(Socket.new(InetAddress, int)) || call(Socket.new(InetAddress, int, boolean)) || call(Socket.new(InetAddress, int, InetAddress, int)) || call(Socket.new(String, int)) || call(Socket.new(String, int, boolean)) || call(Socket.new(String, int, InetAddress, int))) && MOP_CommonPointCut();
	after () returning (Socket sock) : Socket_LargeReceiveBuffer_create_connected() {
		Socket_LargeReceiveBufferRuntimeMonitor.create_connectedEvent(sock);
	}

	pointcut Socket_LargeReceiveBuffer_create_unconnected() : (call(Socket.new()) || call(Socket.new(Proxy)) || call(Socket.new(SocketImpl))) && MOP_CommonPointCut();
	after () returning (Socket sock) : Socket_LargeReceiveBuffer_create_unconnected() {
		Socket_LargeReceiveBufferRuntimeMonitor.create_unconnectedEvent(sock);
	}

}
