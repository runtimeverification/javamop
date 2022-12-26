package mop;
import java.io.*;
import java.lang.*;
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

public aspect ProcessBuilder_NullKeyOrValueMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public ProcessBuilder_NullKeyOrValueMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock ProcessBuilder_NullKeyOrValue_MOPLock = new ReentrantLock();
	static Condition ProcessBuilder_NullKeyOrValue_MOPLock_cond = ProcessBuilder_NullKeyOrValue_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut ProcessBuilder_NullKeyOrValue_nullQuery(Map map, Object o) : ((call(* Map.containsKey(..)) || call(* Map.containsValue(..)) || call(* Map.get(..)) || call(* Map.remove(..))) && target(map) && args(o)) && MOP_CommonPointCut();
	before (Map map, Object o) : ProcessBuilder_NullKeyOrValue_nullQuery(map, o) {
		ProcessBuilder_NullKeyOrValueRuntimeMonitor.nullQueryEvent(map, o);
	}

	pointcut ProcessBuilder_NullKeyOrValue_nullPut_8(Map map, Map map2) : (call(* Map.putAll(Map)) && args(map2) && target(map)) && MOP_CommonPointCut();
	before (Map map, Map map2) : ProcessBuilder_NullKeyOrValue_nullPut_8(map, map2) {
		ProcessBuilder_NullKeyOrValueRuntimeMonitor.nullPutEvent(map, map2);
	}

	pointcut ProcessBuilder_NullKeyOrValue_nullPut_7(Map map, Object key, Object value) : (call(* Map.put(..)) && args(key, value) && target(map)) && MOP_CommonPointCut();
	before (Map map, Object key, Object value) : ProcessBuilder_NullKeyOrValue_nullPut_7(map, key, value) {
		ProcessBuilder_NullKeyOrValueRuntimeMonitor.nullPutEvent(map, key, value);
	}

	pointcut ProcessBuilder_NullKeyOrValue_createMap() : (call(* ProcessBuilder.environment())) && MOP_CommonPointCut();
	after () returning (Map map) : ProcessBuilder_NullKeyOrValue_createMap() {
		ProcessBuilder_NullKeyOrValueRuntimeMonitor.createMapEvent(map);
	}

}
