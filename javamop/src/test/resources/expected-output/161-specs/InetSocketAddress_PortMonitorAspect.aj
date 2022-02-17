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

public aspect InetSocketAddress_PortMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public InetSocketAddress_PortMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock InetSocketAddress_Port_MOPLock = new ReentrantLock();
	static Condition InetSocketAddress_Port_MOPLock_cond = InetSocketAddress_Port_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut InetSocketAddress_Port_construct(int port) : ((call(InetSocketAddress.new(int)) || call(InetSocketAddress.new(InetAddress, int)) || call(InetSocketAddress.new(String, int)) || call(* InetSocketAddress.createUnresolved(String, int))) && args(.., port)) && MOP_CommonPointCut();
	before (int port) : InetSocketAddress_Port_construct(port) {
		InetSocketAddress_PortRuntimeMonitor.constructEvent(port);
	}

}
