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

public aspect Arrays_SortBeforeBinarySearchMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public Arrays_SortBeforeBinarySearchMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock Arrays_SortBeforeBinarySearch_MOPLock = new ReentrantLock();
	static Condition Arrays_SortBeforeBinarySearch_MOPLock_cond = Arrays_SortBeforeBinarySearch_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut Arrays_SortBeforeBinarySearch_bsearch2_4(Object[] arr, int src, int dest, Comparator comp2) : (call(int Arrays.binarySearch(Object[], int, int, Object, Comparator)) && args(arr, src, dest, .., comp2)) && MOP_CommonPointCut();
	before (Object[] arr, int src, int dest, Comparator comp2) : Arrays_SortBeforeBinarySearch_bsearch2_4(arr, src, dest, comp2) {
		Arrays_SortBeforeBinarySearchRuntimeMonitor.bsearch2Event(arr, src, dest, comp2);
	}

	pointcut Arrays_SortBeforeBinarySearch_bsearch2_3(Object[] arr, Comparator comp2) : (call(int Arrays.binarySearch(Object[], Object, Comparator)) && args(arr, .., comp2)) && MOP_CommonPointCut();
	before (Object[] arr, Comparator comp2) : Arrays_SortBeforeBinarySearch_bsearch2_3(arr, comp2) {
		Arrays_SortBeforeBinarySearchRuntimeMonitor.bsearch2Event(arr, comp2);
	}

	pointcut Arrays_SortBeforeBinarySearch_bsearch1_4(Object[] arr, int src, int dest) : (call(int Arrays.binarySearch(Object[], int, int, Object)) && args(arr, src, dest, ..)) && MOP_CommonPointCut();
	before (Object[] arr, int src, int dest) : Arrays_SortBeforeBinarySearch_bsearch1_4(arr, src, dest) {
		Arrays_SortBeforeBinarySearchRuntimeMonitor.bsearch1Event(arr, src, dest);
	}

	pointcut Arrays_SortBeforeBinarySearch_bsearch1_3(Object[] arr) : (call(int Arrays.binarySearch(Object[], Object)) && args(arr, ..)) && MOP_CommonPointCut();
	before (Object[] arr) : Arrays_SortBeforeBinarySearch_bsearch1_3(arr) {
		Arrays_SortBeforeBinarySearchRuntimeMonitor.bsearch1Event(arr);
	}

	pointcut Arrays_SortBeforeBinarySearch_modify(Object[] arr) : (set(Object[] *) && args(arr)) && MOP_CommonPointCut();
	before (Object[] arr) : Arrays_SortBeforeBinarySearch_modify(arr) {
		Arrays_SortBeforeBinarySearchRuntimeMonitor.modifyEvent(arr);
	}

	pointcut Arrays_SortBeforeBinarySearch_sort2_4(Object[] arr, int src, int dest, Comparator comp2) : (call(void Arrays.sort(Object[], int, int, Comparator)) && args(arr, src, dest, comp2)) && MOP_CommonPointCut();
	before (Object[] arr, int src, int dest, Comparator comp2) : Arrays_SortBeforeBinarySearch_sort2_4(arr, src, dest, comp2) {
		Arrays_SortBeforeBinarySearchRuntimeMonitor.sort2Event(arr, src, dest, comp2);
	}

	pointcut Arrays_SortBeforeBinarySearch_sort2_3(Object[] arr, Comparator comp2) : (call(void Arrays.sort(Object[], Comparator)) && args(arr, comp2)) && MOP_CommonPointCut();
	before (Object[] arr, Comparator comp2) : Arrays_SortBeforeBinarySearch_sort2_3(arr, comp2) {
		Arrays_SortBeforeBinarySearchRuntimeMonitor.sort2Event(arr, comp2);
	}

	pointcut Arrays_SortBeforeBinarySearch_sort1_4(Object[] arr, int src, int dest) : (call(void Arrays.sort(Object[], int, int)) && args(arr, src, dest)) && MOP_CommonPointCut();
	before (Object[] arr, int src, int dest) : Arrays_SortBeforeBinarySearch_sort1_4(arr, src, dest) {
		Arrays_SortBeforeBinarySearchRuntimeMonitor.sort1Event(arr, src, dest);
	}

	pointcut Arrays_SortBeforeBinarySearch_sort1_3(Object[] arr) : (call(void Arrays.sort(Object[])) && args(arr)) && MOP_CommonPointCut();
	before (Object[] arr) : Arrays_SortBeforeBinarySearch_sort1_3(arr) {
		Arrays_SortBeforeBinarySearchRuntimeMonitor.sort1Event(arr);
	}

}
