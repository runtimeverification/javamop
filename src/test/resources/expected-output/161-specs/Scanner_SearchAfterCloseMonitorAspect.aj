package mop;
import java.util.*;
import java.io.*;
import com.runtimeverification.rvmonitor.java.rt.RVMLogging;
import com.runtimeverification.rvmonitor.java.rt.RVMLogging.Level;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

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

public aspect Scanner_SearchAfterCloseMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Scanner_SearchAfterCloseMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Scanner_SearchAfterClose_MOPLock = new ReentrantLock();
	static Condition Scanner_SearchAfterClose_MOPLock_cond = Scanner_SearchAfterClose_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Scanner_SearchAfterClose_search(Scanner s) : ((call(* Scanner+.find*(..)) || call(* Scanner+.has*(..)) || call(* Scanner+.match(..)) || call(* Scanner+.next*(..)) || call(* Scanner+.skip(..))) && target(s)) && MOP_CommonPointCut();
	before (Scanner s) : Scanner_SearchAfterClose_search(s) {
		Scanner_SearchAfterCloseRuntimeMonitor.searchEvent(s);
	}

	pointcut Scanner_SearchAfterClose_close(Scanner s) : (call(* Scanner+.close(..)) && target(s)) && MOP_CommonPointCut();
	before (Scanner s) : Scanner_SearchAfterClose_close(s) {
		Scanner_SearchAfterCloseRuntimeMonitor.closeEvent(s);
	}

}
