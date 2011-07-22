package javamop.output.monitor;

import java.util.Set;

import javamop.MOPException;
import javamop.output.MOPVariable;
import javamop.output.OptimizedCoenableSet;
import javamop.output.aspect.MOPStatistics;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameters;
import javamop.parser.ast.mopspec.PropertyAndHandlers;
import javamop.parser.ast.stmt.BlockStmt;

public abstract class Monitor {
	MOPVariable monitorName;

	boolean isDefined;
	boolean isOutermost;
	boolean doActions;

	boolean has__LOC;
	boolean has__SKIP;
	boolean handlersHave__SKIP;

	OptimizedCoenableSet coenableSet;

	MonitorTermination monitorTermination = null;
	MonitorInfo monitorInfo = null;

	MOPStatistics stat;

	public Monitor(String name, JavaMOPSpec mopSpec, OptimizedCoenableSet coenableSet, boolean isOutermost, boolean doActions) throws MOPException {
		this.isOutermost = isOutermost;
		this.doActions = doActions;

		this.has__LOC = mopSpec.has__LOC();
		this.has__SKIP = mopSpec.has__SKIP();
		
		this.handlersHave__SKIP = false; 
		for (PropertyAndHandlers prop : mopSpec.getPropertiesAndHandlers()) {
			for (BlockStmt handler : prop.getHandlers().values()) {
				if (handler.toString().indexOf("__SKIP") != -1){
					this.handlersHave__SKIP = true; 
				}
			}
		}
		
		this.coenableSet = coenableSet;
		
		this.stat = new MOPStatistics(name, mopSpec);
	}

	public abstract MOPVariable getOutermostName();
	
	public abstract Set<String> getNames();

	public abstract boolean isDoingHandlers();
	
	public abstract Set<String> getCategories();

	public abstract boolean isReturningSKIP(EventDefinition event);
	
	public abstract String doEvent(EventDefinition event);

	public abstract String Monitoring(MOPVariable monitorVar, EventDefinition event, MOPVariable thisJoinPoint);

	public abstract String callHandlers(MOPVariable monitorVar, MOPVariable monitorVarForReset, EventDefinition event, MOPParameters eventParam, MOPVariable thisJoinPoint, MOPVariable monitorVarForMonitor, boolean checkSkip);

	public abstract String toString();

	
}
