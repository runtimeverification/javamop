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

public aspect Socket_ReuseAddressMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Socket_ReuseAddressMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Socket_ReuseAddress_MOPLock = new ReentrantLock();
	static Condition Socket_ReuseAddress_MOPLock_cond = Socket_ReuseAddress_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Socket_ReuseAddress_set(Socket sock) : (call(* Socket+.setReuseAddress(..)) && target(sock)) && MOP_CommonPointCut();
	before (Socket sock) : Socket_ReuseAddress_set(sock) {
		Socket_ReuseAddressRuntimeMonitor.setEvent(sock);
	}

	pointcut Socket_ReuseAddress_bind(Socket sock) : (call(* Socket+.bind(..)) && target(sock)) && MOP_CommonPointCut();
	before (Socket sock) : Socket_ReuseAddress_bind(sock) {
		Socket_ReuseAddressRuntimeMonitor.bindEvent(sock);
	}

	pointcut Socket_ReuseAddress_create_connected() : (call(Socket.new(InetAddress, int)) || call(Socket.new(InetAddress, int, boolean)) || call(Socket.new(InetAddress, int, InetAddress, int)) || call(Socket.new(String, int)) || call(Socket.new(String, int, boolean)) || call(Socket.new(String, int, InetAddress, int))) && MOP_CommonPointCut();
	after () returning (Socket sock) : Socket_ReuseAddress_create_connected() {
		Socket_ReuseAddressRuntimeMonitor.create_connectedEvent(sock);
	}

	pointcut Socket_ReuseAddress_create_unconnected() : (call(Socket.new()) || call(Socket.new(Proxy)) || call(Socket.new(SocketImpl))) && MOP_CommonPointCut();
	after () returning (Socket sock) : Socket_ReuseAddress_create_unconnected() {
		Socket_ReuseAddressRuntimeMonitor.create_unconnectedEvent(sock);
	}

}
