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

public aspect Socket_OutputStreamUnavailableMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Socket_OutputStreamUnavailableMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Socket_OutputStreamUnavailable_MOPLock = new ReentrantLock();
	static Condition Socket_OutputStreamUnavailable_MOPLock_cond = Socket_OutputStreamUnavailable_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Socket_OutputStreamUnavailable_shutdown(Socket sock) : (call(* Socket+.shutdownOutput()) && target(sock)) && MOP_CommonPointCut();
	before (Socket sock) : Socket_OutputStreamUnavailable_shutdown(sock) {
		Socket_OutputStreamUnavailableRuntimeMonitor.shutdownEvent(sock);
	}

	pointcut Socket_OutputStreamUnavailable_close(Socket sock) : (call(* Socket+.close()) && target(sock)) && MOP_CommonPointCut();
	before (Socket sock) : Socket_OutputStreamUnavailable_close(sock) {
		Socket_OutputStreamUnavailableRuntimeMonitor.closeEvent(sock);
	}

	pointcut Socket_OutputStreamUnavailable_get(Socket sock) : (call(* Socket+.getOutputStream(..)) && target(sock)) && MOP_CommonPointCut();
	before (Socket sock) : Socket_OutputStreamUnavailable_get(sock) {
		Socket_OutputStreamUnavailableRuntimeMonitor.getEvent(sock);
	}

	pointcut Socket_OutputStreamUnavailable_connect(Socket sock) : (call(* Socket+.connect(..)) && target(sock)) && MOP_CommonPointCut();
	before (Socket sock) : Socket_OutputStreamUnavailable_connect(sock) {
		Socket_OutputStreamUnavailableRuntimeMonitor.connectEvent(sock);
	}

	pointcut Socket_OutputStreamUnavailable_create_connected() : (call(Socket.new(InetAddress, int)) || call(Socket.new(InetAddress, int, boolean)) || call(Socket.new(InetAddress, int, InetAddress, int)) || call(Socket.new(String, int)) || call(Socket.new(String, int, boolean)) || call(Socket.new(String, int, InetAddress, int))) && MOP_CommonPointCut();
	after () returning (Socket sock) : Socket_OutputStreamUnavailable_create_connected() {
		Socket_OutputStreamUnavailableRuntimeMonitor.create_connectedEvent(sock);
	}

	pointcut Socket_OutputStreamUnavailable_create_unconnected() : (call(Socket.new()) || call(Socket.new(Proxy)) || call(Socket.new(SocketImpl))) && MOP_CommonPointCut();
	after () returning (Socket sock) : Socket_OutputStreamUnavailable_create_unconnected() {
		Socket_OutputStreamUnavailableRuntimeMonitor.create_unconnectedEvent(sock);
	}

}
