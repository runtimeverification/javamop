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

public aspect HttpCookie_DomainMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public HttpCookie_DomainMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock HttpCookie_Domain_MOPLock = new ReentrantLock();
	static Condition HttpCookie_Domain_MOPLock_cond = HttpCookie_Domain_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut HttpCookie_Domain_setdomain(String domain) : (call(void HttpCookie.setDomain(String)) && args(domain)) && MOP_CommonPointCut();
	before (String domain) : HttpCookie_Domain_setdomain(domain) {
		HttpCookie_DomainRuntimeMonitor.setdomainEvent(domain);
	}

}
