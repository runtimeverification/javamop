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

public aspect NetPermission_NameMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public NetPermission_NameMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock NetPermission_Name_MOPLock = new ReentrantLock();
	static Condition NetPermission_Name_MOPLock_cond = NetPermission_Name_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut NetPermission_Name_construct(String name) : ((call(NetPermission.new(String)) || call(NetPermission.new(String, String))) && args(name, ..)) && MOP_CommonPointCut();
	before (String name) : NetPermission_Name_construct(name) {
		NetPermission_NameRuntimeMonitor.constructEvent(name);
	}

}
