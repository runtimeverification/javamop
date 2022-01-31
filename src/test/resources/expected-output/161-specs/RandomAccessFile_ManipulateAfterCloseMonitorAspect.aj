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

public aspect RandomAccessFile_ManipulateAfterCloseMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public RandomAccessFile_ManipulateAfterCloseMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock RandomAccessFile_ManipulateAfterClose_MOPLock = new ReentrantLock();
	static Condition RandomAccessFile_ManipulateAfterClose_MOPLock_cond = RandomAccessFile_ManipulateAfterClose_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut RandomAccessFile_ManipulateAfterClose_close(RandomAccessFile f) : (call(* RandomAccessFile+.close(..)) && target(f)) && MOP_CommonPointCut();
	before (RandomAccessFile f) : RandomAccessFile_ManipulateAfterClose_close(f) {
		RandomAccessFile_ManipulateAfterCloseRuntimeMonitor.closeEvent(f);
	}

	pointcut RandomAccessFile_ManipulateAfterClose_manipulate(RandomAccessFile f) : ((call(* RandomAccessFile+.read*(..)) || call(* RandomAccessFile+.write*(..))) && target(f)) && MOP_CommonPointCut();
	before (RandomAccessFile f) : RandomAccessFile_ManipulateAfterClose_manipulate(f) {
		RandomAccessFile_ManipulateAfterCloseRuntimeMonitor.manipulateEvent(f);
	}

}
