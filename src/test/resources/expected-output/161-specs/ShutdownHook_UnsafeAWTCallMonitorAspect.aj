package mop;
import java.lang.*;
import java.awt.EventQueue;
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

public aspect ShutdownHook_UnsafeAWTCallMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public ShutdownHook_UnsafeAWTCallMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock ShutdownHook_UnsafeAWTCall_MOPLock = new ReentrantLock();
	static Condition ShutdownHook_UnsafeAWTCall_MOPLock_cond = ShutdownHook_UnsafeAWTCall_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut ShutdownHook_UnsafeAWTCall_unregister(Thread t) : (call(* Runtime+.removeShutdownHook(..)) && args(t)) && MOP_CommonPointCut();
	before (Thread t) : ShutdownHook_UnsafeAWTCall_unregister(t) {
		boolean MOP_skipAroundAdvice = false;
		ShutdownHook_UnsafeAWTCallRuntimeMonitor.unregisterEvent(t);
	}

	pointcut ShutdownHook_UnsafeAWTCall_register(Thread t) : (call(* Runtime+.addShutdownHook(..)) && args(t)) && MOP_CommonPointCut();
	before (Thread t) : ShutdownHook_UnsafeAWTCall_register(t) {
		boolean MOP_skipAroundAdvice = false;
		ShutdownHook_UnsafeAWTCallRuntimeMonitor.registerEvent(t);
	}

	pointcut ShutdownHook_UnsafeAWTCall_awtcall() : (call(* EventQueue.invokeAndWait(..)) || call(* EventQueue.invokeLater(..))) && MOP_CommonPointCut();
	void around () : ShutdownHook_UnsafeAWTCall_awtcall() {
		boolean MOP_skipAroundAdvice = false;
		Thread t = Thread.currentThread();
		ShutdownHook_UnsafeAWTCallRuntimeMonitor.awtcallEvent(t);
		if(MOP_skipAroundAdvice){
			return;
		} else {
			proceed();
		}
	}

	static HashMap<Thread, Runnable> ShutdownHook_UnsafeAWTCall_start_ThreadToRunnable = new HashMap<Thread, Runnable>();
	static Thread ShutdownHook_UnsafeAWTCall_start_MainThread = null;

	after (Runnable r) returning (Thread t): ((call(Thread+.new(Runnable+,..)) && args(r,..))|| (initialization(Thread+.new(ThreadGroup+, Runnable+,..)) && args(ThreadGroup, r,..))) && MOP_CommonPointCut() {
		while (!ShutdownHook_UnsafeAWTCall_MOPLock.tryLock()) {
			Thread.yield();
		}
		ShutdownHook_UnsafeAWTCall_start_ThreadToRunnable.put(t, r);
		ShutdownHook_UnsafeAWTCall_MOPLock.unlock();
	}

	before (Thread t_1): ( execution(void Thread+.run()) && target(t_1) ) && MOP_CommonPointCut() {
		if(Thread.currentThread() == t_1) {
			Thread t = Thread.currentThread();
			ShutdownHook_UnsafeAWTCallRuntimeMonitor.startEvent(t);
		}
	}

	before (Runnable r_1): ( execution(void Runnable+.run()) && !execution(void Thread+.run()) && target(r_1) ) && MOP_CommonPointCut() {
		while (!ShutdownHook_UnsafeAWTCall_MOPLock.tryLock()) {
			Thread.yield();
		}
		if(ShutdownHook_UnsafeAWTCall_start_ThreadToRunnable.get(Thread.currentThread()) == r_1) {
			Thread t = Thread.currentThread();
			ShutdownHook_UnsafeAWTCallRuntimeMonitor.startEvent(t);
		}
		ShutdownHook_UnsafeAWTCall_MOPLock.unlock();
	}

	before (): (execution(void *.main(..)) ) && MOP_CommonPointCut() {
		if(ShutdownHook_UnsafeAWTCall_start_MainThread == null){
			ShutdownHook_UnsafeAWTCall_start_MainThread = Thread.currentThread();
			Thread t = Thread.currentThread();
			ShutdownHook_UnsafeAWTCallRuntimeMonitor.startEvent(t);
		}
	}

}
