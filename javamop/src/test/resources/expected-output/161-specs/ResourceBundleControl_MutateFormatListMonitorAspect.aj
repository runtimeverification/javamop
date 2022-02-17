package mop;
import java.util.*;
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

public aspect ResourceBundleControl_MutateFormatListMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public ResourceBundleControl_MutateFormatListMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock ResourceBundleControl_MutateFormatList_MOPLock = new ReentrantLock();
	static Condition ResourceBundleControl_MutateFormatList_MOPLock_cond = ResourceBundleControl_MutateFormatList_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut ResourceBundleControl_MutateFormatList_mutate(List l) : ((call(* Collection+.add*(..)) || call(* Collection+.clear(..)) || call(* Collection+.remove*(..)) || call(* Collection+.retain*(..))) && target(l)) && MOP_CommonPointCut();
	before (List l) : ResourceBundleControl_MutateFormatList_mutate(l) {
		ResourceBundleControl_MutateFormatListRuntimeMonitor.mutateEvent(l);
	}

	pointcut ResourceBundleControl_MutateFormatList_create() : (call(List ResourceBundle.Control.getFormats(..)) || call(List ResourceBundle.Control.getCandidateLocales(..))) && MOP_CommonPointCut();
	after () returning (List l) : ResourceBundleControl_MutateFormatList_create() {
		ResourceBundleControl_MutateFormatListRuntimeMonitor.createEvent(l);
	}

}
