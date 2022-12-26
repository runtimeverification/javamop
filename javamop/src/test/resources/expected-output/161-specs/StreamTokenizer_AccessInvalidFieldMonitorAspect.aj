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

public aspect StreamTokenizer_AccessInvalidFieldMonitorAspect implements com.runtimeverification.rvmonitor.java.rt.RVMObject {
	public StreamTokenizer_AccessInvalidFieldMonitorAspect(){
	}

	// Declarations for the Lock
	static ReentrantLock StreamTokenizer_AccessInvalidField_MOPLock = new ReentrantLock();
	static Condition StreamTokenizer_AccessInvalidField_MOPLock_cond = StreamTokenizer_AccessInvalidField_MOPLock.newCondition();

	pointcut MOP_CommonPointCut() : !within(com.runtimeverification.rvmonitor.java.rt.RVMObject+) && !adviceexecution() && BaseAspect.notwithin();
	pointcut StreamTokenizer_AccessInvalidField_nval(StreamTokenizer s) : (get(* StreamTokenizer.nval) && target(s)) && MOP_CommonPointCut();
	before (StreamTokenizer s) : StreamTokenizer_AccessInvalidField_nval(s) {
		StreamTokenizer_AccessInvalidFieldRuntimeMonitor.nvalEvent(s);
	}

	pointcut StreamTokenizer_AccessInvalidField_sval(StreamTokenizer s) : (get(* StreamTokenizer.sval) && target(s)) && MOP_CommonPointCut();
	before (StreamTokenizer s) : StreamTokenizer_AccessInvalidField_sval(s) {
		StreamTokenizer_AccessInvalidFieldRuntimeMonitor.svalEvent(s);
	}

	pointcut StreamTokenizer_AccessInvalidField_nexttoken_word(StreamTokenizer s) : (call(* StreamTokenizer+.nextToken(..)) && target(s)) && MOP_CommonPointCut();
	after (StreamTokenizer s) returning (int t) : StreamTokenizer_AccessInvalidField_nexttoken_word(s) {
		//StreamTokenizer_AccessInvalidField_nexttoken_word
		StreamTokenizer_AccessInvalidFieldRuntimeMonitor.nexttoken_wordEvent(s, t);
		//StreamTokenizer_AccessInvalidField_nexttoken_num
		StreamTokenizer_AccessInvalidFieldRuntimeMonitor.nexttoken_numEvent(s, t);
		//StreamTokenizer_AccessInvalidField_nexttoken_eol
		StreamTokenizer_AccessInvalidFieldRuntimeMonitor.nexttoken_eolEvent(s, t);
		//StreamTokenizer_AccessInvalidField_nexttoken_eof
		StreamTokenizer_AccessInvalidFieldRuntimeMonitor.nexttoken_eofEvent(s, t);
	}

}
