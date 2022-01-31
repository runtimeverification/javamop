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

public aspect SocketPermission_ActionsMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public SocketPermission_ActionsMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock SocketPermission_Actions_MOPLock = new ReentrantLock();
	static Condition SocketPermission_Actions_MOPLock_cond = SocketPermission_Actions_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut SocketPermission_Actions_construct(String actions) : (call(SocketPermission.new(String, String)) && args(*, actions)) && MOP_CommonPointCut();
	before (String actions) : SocketPermission_Actions_construct(actions) {
		SocketPermission_ActionsRuntimeMonitor.constructEvent(actions);
	}

}
