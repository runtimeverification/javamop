package mop;
import java.lang.*;
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

public aspect ShutdownHook_SystemExitMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public ShutdownHook_SystemExitMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock ShutdownHook_SystemExit_MOPLock = new ReentrantLock();
	static Condition ShutdownHook_SystemExit_MOPLock_cond = ShutdownHook_SystemExit_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut ShutdownHook_SystemExit_unregister(Thread t) : (call(* Runtime+.removeShutdownHook(..)) && args(t)) && MOP_CommonPointCut();
	before (Thread t) : ShutdownHook_SystemExit_unregister(t) {
		boolean MOP_skipAroundAdvice = false;
		ShutdownHook_SystemExitRuntimeMonitor.unregisterEvent(t);
	}

	pointcut ShutdownHook_SystemExit_register(Thread t) : (call(* Runtime+.addShutdownHook(..)) && args(t)) && MOP_CommonPointCut();
	before (Thread t) : ShutdownHook_SystemExit_register(t) {
		boolean MOP_skipAroundAdvice = false;
		ShutdownHook_SystemExitRuntimeMonitor.registerEvent(t);
	}

	pointcut ShutdownHook_SystemExit_exit() : (call(* System.exit(..))) && MOP_CommonPointCut();
	void around () : ShutdownHook_SystemExit_exit() {
		boolean MOP_skipAroundAdvice = false;
		Thread t = Thread.currentThread();
		ShutdownHook_SystemExitRuntimeMonitor.exitEvent(t);
		if(MOP_skipAroundAdvice){
			return;
		} else {
			proceed();
		}
	}

	static HashMap<Thread, Runnable> ShutdownHook_SystemExit_start_ThreadToRunnable = new HashMap<Thread, Runnable>();
	static Thread ShutdownHook_SystemExit_start_MainThread = null;

	after (Runnable r) returning (Thread t): ((call(Thread+.new(Runnable+,..)) && args(r,..))|| (initialization(Thread+.new(ThreadGroup+, Runnable+,..)) && args(ThreadGroup, r,..))) && MOP_CommonPointCut() {
		while (!ShutdownHook_SystemExit_MOPLock.tryLock()) {
			Thread.yield();
		}
		ShutdownHook_SystemExit_start_ThreadToRunnable.put(t, r);
		ShutdownHook_SystemExit_MOPLock.unlock();
	}

	before (Thread t_1): ( execution(void Thread+.run()) && target(t_1) ) && MOP_CommonPointCut() {
		if(Thread.currentThread() == t_1) {
			Thread t = Thread.currentThread();
			ShutdownHook_SystemExitRuntimeMonitor.startEvent(t);
		}
	}

	before (Runnable r_1): ( execution(void Runnable+.run()) && !execution(void Thread+.run()) && target(r_1) ) && MOP_CommonPointCut() {
		while (!ShutdownHook_SystemExit_MOPLock.tryLock()) {
			Thread.yield();
		}
		if(ShutdownHook_SystemExit_start_ThreadToRunnable.get(Thread.currentThread()) == r_1) {
			Thread t = Thread.currentThread();
			ShutdownHook_SystemExitRuntimeMonitor.startEvent(t);
		}
		ShutdownHook_SystemExit_MOPLock.unlock();
	}

	before (): (execution(void *.main(..)) ) && MOP_CommonPointCut() {
		if(ShutdownHook_SystemExit_start_MainThread == null){
			ShutdownHook_SystemExit_start_MainThread = Thread.currentThread();
			Thread t = Thread.currentThread();
			ShutdownHook_SystemExitRuntimeMonitor.startEvent(t);
		}
	}

}
