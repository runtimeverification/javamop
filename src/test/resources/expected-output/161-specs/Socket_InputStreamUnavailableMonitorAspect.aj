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

public aspect Socket_InputStreamUnavailableMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Socket_InputStreamUnavailableMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Socket_InputStreamUnavailable_MOPLock = new ReentrantLock();
	static Condition Socket_InputStreamUnavailable_MOPLock_cond = Socket_InputStreamUnavailable_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Socket_InputStreamUnavailable_shutdown(Socket sock) : (call(* Socket+.shutdownInput()) && target(sock)) && MOP_CommonPointCut();
	before (Socket sock) : Socket_InputStreamUnavailable_shutdown(sock) {
		Socket_InputStreamUnavailableRuntimeMonitor.shutdownEvent(sock);
	}

	pointcut Socket_InputStreamUnavailable_close(Socket sock) : (call(* Socket+.close()) && target(sock)) && MOP_CommonPointCut();
	before (Socket sock) : Socket_InputStreamUnavailable_close(sock) {
		Socket_InputStreamUnavailableRuntimeMonitor.closeEvent(sock);
	}

	pointcut Socket_InputStreamUnavailable_get(Socket sock) : (call(* Socket+.getInputStream(..)) && target(sock)) && MOP_CommonPointCut();
	before (Socket sock) : Socket_InputStreamUnavailable_get(sock) {
		Socket_InputStreamUnavailableRuntimeMonitor.getEvent(sock);
	}

	pointcut Socket_InputStreamUnavailable_connect(Socket sock) : (call(* Socket+.connect(..)) && target(sock)) && MOP_CommonPointCut();
	before (Socket sock) : Socket_InputStreamUnavailable_connect(sock) {
		Socket_InputStreamUnavailableRuntimeMonitor.connectEvent(sock);
	}

	pointcut Socket_InputStreamUnavailable_create_connected() : (call(Socket.new(InetAddress, int)) || call(Socket.new(InetAddress, int, boolean)) || call(Socket.new(InetAddress, int, InetAddress, int)) || call(Socket.new(String, int)) || call(Socket.new(String, int, boolean)) || call(Socket.new(String, int, InetAddress, int))) && MOP_CommonPointCut();
	after () returning (Socket sock) : Socket_InputStreamUnavailable_create_connected() {
		Socket_InputStreamUnavailableRuntimeMonitor.create_connectedEvent(sock);
	}

	pointcut Socket_InputStreamUnavailable_create_unconnected() : (call(Socket.new()) || call(Socket.new(Proxy)) || call(Socket.new(SocketImpl))) && MOP_CommonPointCut();
	after () returning (Socket sock) : Socket_InputStreamUnavailable_create_unconnected() {
		Socket_InputStreamUnavailableRuntimeMonitor.create_unconnectedEvent(sock);
	}

}
