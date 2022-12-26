package mop;
import java.io.*;
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

public aspect File_LengthOnDirectoryMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public File_LengthOnDirectoryMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock File_LengthOnDirectory_MOPLock = new ReentrantLock();
	static Condition File_LengthOnDirectory_MOPLock_cond = File_LengthOnDirectory_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut File_LengthOnDirectory_bad_length(File f) : (call(* File+.length()) && target(f)) && MOP_CommonPointCut();
	before (File f) : File_LengthOnDirectory_bad_length(f) {
		File_LengthOnDirectoryRuntimeMonitor.bad_lengthEvent(f);
	}

}
