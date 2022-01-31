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

public aspect URLEncoder_EncodeUTF8MonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public URLEncoder_EncodeUTF8MonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock URLEncoder_EncodeUTF8_MOPLock = new ReentrantLock();
	static Condition URLEncoder_EncodeUTF8_MOPLock_cond = URLEncoder_EncodeUTF8_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut URLEncoder_EncodeUTF8_encode(String enc) : (call(* URLEncoder.encode(String, String)) && args(*, enc)) && MOP_CommonPointCut();
	before (String enc) : URLEncoder_EncodeUTF8_encode(enc) {
		URLEncoder_EncodeUTF8RuntimeMonitor.encodeEvent(enc);
	}

}
