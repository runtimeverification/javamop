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

public aspect HttpURLConnection_SetBeforeConnectMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public HttpURLConnection_SetBeforeConnectMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock HttpURLConnection_SetBeforeConnect_MOPLock = new ReentrantLock();
	static Condition HttpURLConnection_SetBeforeConnect_MOPLock_cond = HttpURLConnection_SetBeforeConnect_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut HttpURLConnection_SetBeforeConnect_connect(HttpURLConnection c) : ((call(* URLConnection+.connect(..)) || call(* URLConnection+.getContent(..)) || call(* URLConnection+.getContentEncoding(..)) || call(* URLConnection+.getContentLength(..)) || call(* URLConnection+.getContentType(..)) || call(* URLConnection+.getDate(..)) || call(* URLConnection+.getExpiration(..)) || call(* URLConnection+.getHeaderField(..)) || call(* URLConnection+.getHeaderFieldInt(..)) || call(* URLConnection+.getHeaderFields(..)) || call(* URLConnection+.getInputStream(..)) || call(* URLConnection+.getLastModified(..)) || call(* URLConnection+.getOutputStream(..)) || call(* HttpURLConnection+.getErrorStream(..)) || call(* HttpURLConnection+.getHeaderFieldDate(..)) || call(* HttpURLConnection+.getHeaderFieldKey(..)) || call(* HttpURLConnection+.getResponseCode(..)) || call(* HttpURLConnection+.getResponseMessage(..))) && target(c)) && MOP_CommonPointCut();
	before (HttpURLConnection c) : HttpURLConnection_SetBeforeConnect_connect(c) {
		HttpURLConnection_SetBeforeConnectRuntimeMonitor.connectEvent(c);
	}

	pointcut HttpURLConnection_SetBeforeConnect_set(HttpURLConnection c) : ((call(* HttpURLConnection+.setFixedLengthStreamingMode(..)) || call(* HttpURLConnection+.setChunkedStreamingMode(..)) || call(* HttpURLConnection+.setRequestMethod(..))) && target(c)) && MOP_CommonPointCut();
	before (HttpURLConnection c) : HttpURLConnection_SetBeforeConnect_set(c) {
		HttpURLConnection_SetBeforeConnectRuntimeMonitor.setEvent(c);
	}

}
