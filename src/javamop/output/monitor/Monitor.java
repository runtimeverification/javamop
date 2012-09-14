package javamop.output.monitor;

import java.util.HashMap;
import java.util.Set;

import javamop.MOPException;
import javamop.output.MOPVariable;
import javamop.output.OptimizedCoenableSet;
import javamop.output.Util;
import javamop.output.combinedaspect.MOPStatistics;
import javamop.output.combinedaspect.indexingtree.reftree.RefTree;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.PropertyAndHandlers;
import javamop.parser.ast.stmt.BlockStmt;

public abstract class Monitor {
	MOPVariable monitorName;

	boolean isDefined;
	boolean isOutermost;

	boolean has__LOC;
	boolean has__DEFAULT_MESSAGE;
	boolean has__STATICSIG;
	boolean has__SKIP;
	boolean handlersHave__SKIP;
	boolean hasThisJoinPoint;

  String defaultMessage =  "\"Specification ";

  public String getDefaultMessage() {
    return defaultMessage;
  }

	OptimizedCoenableSet coenableSet;

	MonitorTermination monitorTermination = null;
	MonitorInfo monitorInfo = null;

	MOPStatistics stat;
	
	VarInOutermostMonitor varInOutermostMonitor = null;

	HashMap<String, MOPVariable> mopRefs = new HashMap<String, MOPVariable>();

	HashMap<String, RefTree> refTrees;

	public Monitor(String name, JavaMOPSpec mopSpec, OptimizedCoenableSet coenableSet, boolean isOutermost) throws MOPException {
		this.isOutermost = isOutermost;

		this.has__LOC = mopSpec.has__LOC();
		this.has__DEFAULT_MESSAGE = mopSpec.has__DEFAULT_MESSAGE();
		this.has__STATICSIG = mopSpec.has__STATICSIG();
		this.has__SKIP = mopSpec.has__SKIP();
		this.hasThisJoinPoint = mopSpec.hasThisJoinPoint();
		
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

    this.defaultMessage += name + " has been violated on line \" + " 
                   + "__LOC" +" + \". Documentation for this property can be found at " 
                   + Util.packageAndNameToUrl(mopSpec.getPackage(), name) + "\""; 
		
		for (MOPParameter p : mopSpec.getParameters()) {
			mopRefs.put(p.getName(), new MOPVariable("MOPRef_" + p.getName()));
		}

	}
	
	public MOPVariable getMOPRef(MOPParameter p){
		return mopRefs.get(p.getName());
	}
	
	public abstract void setRefTrees(HashMap<String, RefTree> refTrees);

	public abstract MOPVariable getOutermostName();
	
	public abstract Set<String> getNames();

	public abstract Set<MOPVariable> getCategoryVars();

	public abstract String Monitoring(MOPVariable monitorVar, EventDefinition event, MOPVariable loc, MOPVariable staticsig);

	public abstract MonitorInfo getMonitorInfo();
	
	public abstract String toString();

}
