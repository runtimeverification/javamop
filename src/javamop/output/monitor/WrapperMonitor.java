package javamop.output.monitor;

import java.util.List;
import java.util.Set;

import javamop.MOPException;
import javamop.output.MOPVariable;
import javamop.output.OptimizedCoenableSet;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameters;

public class WrapperMonitor extends Monitor{

	MOPVariable thisJoinPoint = new MOPVariable("MOP_thisJoinPoint");
	MOPVariable lastevent = new MOPVariable("MOP_lastevent");
	MOPVariable skipAroundAdvice = new MOPVariable("skipAroundAdvice");

	MOPVariable monitor = new MOPVariable("monitor");
	MOPVariable disable = new MOPVariable("disable");
	MOPVariable tau = new MOPVariable("tau");

	List<EventDefinition> events;

	SuffixMonitor suffixMonitor = null;

	public WrapperMonitor(String name, JavaMOPSpec mopSpec, OptimizedCoenableSet coenableSet, boolean isOutermost, boolean doActions) throws MOPException {
		super(name, mopSpec, coenableSet, isOutermost, doActions);
		
		this.isDefined = mopSpec.isGeneral();
		
		if (this.isDefined) {
			monitorName = new MOPVariable(mopSpec.getName() + "Wrapper");

			if (isOutermost) {
				monitorTermination = new MonitorTermination(name, mopSpec, mopSpec.getEvents(), coenableSet);
				suffixMonitor = new SuffixMonitor(name, mopSpec, coenableSet, false, doActions);
			} else {
				suffixMonitor = new SuffixMonitor(name, mopSpec, coenableSet, false, doActions);
			}

			events = mopSpec.getEvents();
		} else {
			suffixMonitor = new SuffixMonitor(name, mopSpec, coenableSet, isOutermost, doActions);
		}

		if (this.isDefined && mopSpec.isGeneral()){
			if(mopSpec.isFullBinding() || mopSpec.isConnected())
				monitorInfo = new MonitorInfo(mopSpec);
		}
	}

	public String newWrapper(MOPVariable monitorVar, MOPParameters vars) {
		String ret = "";
		ret += monitorVar + " = new " + monitorName + "();\n";
		if(monitorInfo != null)
			monitorInfo.newInfo(monitorVar, vars);
		return ret;
	}

	public String copyAliveParameters(MOPVariable toWrapper, MOPVariable fromWrapper) {
		String ret = "";
		if (monitorTermination != null)
			ret += monitorTermination.copyAliveParameters(toWrapper, fromWrapper);
		return ret;
	}

	public String incDisable(MOPVariable monitorVar, MOPVariable timestamp) {
		String ret = "";
		ret += monitorVar + "." + disable + " = " + timestamp + "++;\n";
		return ret;
	}

	public String getSubMonitor(MOPVariable monitorVar) {
		return monitorVar + "." + this.monitor;
	}

	public SuffixMonitor getSubMonitorClass() {
		return suffixMonitor;
	}
	
	public MOPVariable getSubMonitorName() {
		return suffixMonitor.getOutermostName();
	}
	
	public String getTau(MOPVariable monitorVar) {
		return monitorVar + "." + this.tau;
	}

	public String getDisable(MOPVariable monitorVar) {
		return monitorVar + "." + this.disable;
	}

	public String getLastEvent(MOPVariable monitorVar) {
		return monitorVar + "." + this.lastevent;
	}
	
	public MOPVariable getOutermostName() {
		if (isDefined)
			return monitorName;
		else
			return suffixMonitor.getOutermostName();
	}

	public Set<String> getNames() {
		Set<String> ret = suffixMonitor.getNames();
		if (isDefined)
			ret.add(monitorName.toString());
		return ret;
	}

	/*
	 * Whether handlers are done when event methods are called.
	 * 
	 * If it is not true, handlers need to be done outside of monitor by calling
	 * doHandlers.
	 * 
	 * If this monitor is the outermost monitor, calling Monitoring will do the
	 * job.
	 */
	public boolean isDoingHandlers() {
		return suffixMonitor.isDoingHandlers();
	}
	
	public Set<String> getCategories(){
		return suffixMonitor.getCategories();
	}

	public boolean isReturningSKIP(EventDefinition event) {
		if(!isDefined){
			return suffixMonitor.isReturningSKIP(event);
		}
		
		boolean isAround = event.getPos().equals("around");
		boolean anyReturningSKIP = suffixMonitor.isReturningSKIP(event);

		return isAround && anyReturningSKIP;
	}
	
	public String doEvent(EventDefinition event) {
		String ret = "";
		
		String uniqueId = event.getUniqueId();
		int idnum = event.getIdNum();

		if (isReturningSKIP(event)) {
			ret += "public final boolean event_" + uniqueId + "(" + event.getMOPParameters().parameterDeclString() + ") {\n";
		} else {
			ret += "public final void event_" + uniqueId + "(" + event.getMOPParameters().parameterDeclString() + ") {\n";
		}

		if (isOutermost) {
			ret += lastevent + " = " + idnum + ";\n";
		}

		if (isReturningSKIP(event)) {
			ret += "return " + monitor + ".event_" + uniqueId + "(" + event.getMOPParameters().parameterString() + ");\n";
		} else {
			ret += monitor + ".event_" + uniqueId + "(" + event.getMOPParameters().parameterString() + ");\n";
		}
		
		ret += "}\n";

		return ret;
	}

	public String Monitoring(MOPVariable monitorVar, EventDefinition event, MOPVariable thisJoinPoint) {
		String ret = "";

		if (!isDefined)
			return suffixMonitor.Monitoring(monitorVar, event, thisJoinPoint);

		ret += "if (" + monitorVar + "." + monitor + " != null){\n";

		if (has__LOC) {
			ret += monitorVar + "." + monitor + "." + this.thisJoinPoint + " = " + thisJoinPoint + ";\n";
		}

		if (isReturningSKIP(event)) {
			ret += skipAroundAdvice + " |= ";
		}
		
		ret += monitorVar + ".event_" + event.getUniqueId() + "(";
		ret += event.getMOPParameters().parameterString();
		ret += ");\n";

		if (!suffixMonitor.isDoingHandlers()){
			MOPVariable subMonitor = new MOPVariable(monitorVar, "monitor");
			ret += suffixMonitor.doHandlers(subMonitor, monitorVar, thisJoinPoint, subMonitor);
		}

		ret += "}\n";

		return ret;
	}

	public String doHandlers(MOPVariable monitorVar, MOPVariable monitorVarForReset, MOPVariable thisJoinPoint, MOPVariable monitorVarForMonitor) {
		return suffixMonitor.doHandlers(monitorVar, monitorVarForReset, thisJoinPoint, monitorVarForMonitor);
	}

	public String toString() {
		String ret = "";

		if (isDefined) {
			ret += "class " + monitorName;
			if (isOutermost)
				ret += " extends javamoprt.MOPMonitor" + " {\n";
			else
				ret += " implements javamoprt.MOPObject{\n";

			ret += "public " + suffixMonitor.getOutermostName() + " " + monitor + " = null;\n";

			ret += "public long " + disable + " = 1;\n";
			ret += "public long " + tau + " = 1;\n";
			ret += "\n";

			// events
			for (EventDefinition event : this.events) {
				ret += this.doEvent(event) + "\n";
			}

			// reset
			ret += "public final void reset() {\n";
			if (isOutermost) {
				ret += lastevent + " = -1;\n";
			}
			ret += monitor + ".reset();\n";
			ret += "}\n";
			ret += "\n";

			if (isOutermost)
				ret += monitorTermination;
			
			ret += "}\n";
			ret += "\n";
		}

		ret += suffixMonitor;

		return ret;
	}

}
