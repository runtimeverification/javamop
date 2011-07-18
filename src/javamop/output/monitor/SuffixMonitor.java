package javamop.output.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javamop.MOPException;
import javamop.output.MOPVariable;
import javamop.output.OptimizedCoenableSet;
import javamop.output.aspect.MOPStatistics;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;

public class SuffixMonitor extends Monitor {

	MOPVariable thisJoinPoint = new MOPVariable("MOP_thisJoinPoint");
	MOPVariable lastevent = new MOPVariable("MOP_lastevent");
	MOPVariable skipAroundAdvice = new MOPVariable("skipAroundAdvice");

	List<EventDefinition> events;

	MultiMonitor multiMonitor = null;

	ArrayList<String> categories;
	MOPVariable monitorList = new MOPVariable("monitorList");

	public SuffixMonitor(String name, JavaMOPSpec mopSpec, OptimizedCoenableSet coenableSet, boolean isOutermost, boolean doActions) throws MOPException {
		super(name, mopSpec, coenableSet, isOutermost, doActions);

		this.isDefined = mopSpec.isSuffixMatching();

		if (this.isDefined) {
			monitorName = new MOPVariable(mopSpec.getName() + "SuffixMonitor");

			if (isOutermost) {
				monitorTermination = new MonitorTermination(name, mopSpec, mopSpec.getEvents(), coenableSet);
				multiMonitor = new MultiMonitor(name, mopSpec, coenableSet, false, doActions);
			} else {
				multiMonitor = new MultiMonitor(name, mopSpec, coenableSet, false, doActions);
			}

			events = mopSpec.getEvents();
		} else {
			multiMonitor = new MultiMonitor(name, mopSpec, coenableSet, isOutermost, doActions);
		}

		if (this.isDefined && mopSpec.isGeneral()){
			if(mopSpec.isFullBinding() || mopSpec.isConnected())
				monitorInfo = new MonitorInfo(mopSpec);
		}
	}

	public MOPVariable getOutermostName() {
		if (isDefined)
			return monitorName;
		else
			return multiMonitor.getOutermostName();
	}

	public Set<String> getNames() {
		Set<String> ret = multiMonitor.getNames();
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
		if (isDefined) {
			return true;
		} else {
			return multiMonitor.isDoingHandlers();
		}
	}

	public Set<String> getCategories() {
		return multiMonitor.getCategories();
	}

	public boolean isReturningSKIP(EventDefinition event) {
		if (!isDefined) {
			return multiMonitor.isReturningSKIP(event);
		}

		boolean isAround = event.getPos().equals("around");
		boolean anyReturningSKIP = multiMonitor.isReturningSKIP(event);

		return isAround && (anyReturningSKIP || handlersHave__SKIP);
	}

	public String doEvent(EventDefinition event) {
		String ret = "";

		String uniqueId = event.getUniqueId();
		int idnum = event.getIdNum();
		boolean anyReturningSKIP = multiMonitor.isReturningSKIP(event);

		MOPVariable monitor = new MOPVariable("monitor");
		MOPVariable monitorSet = new MOPVariable("monitorSet");
		MOPVariable newMonitor = new MOPVariable("newMonitor");
		MOPVariable it = new MOPVariable("it");
		HashMap<String, MOPVariable> categoryVars = new HashMap<String, MOPVariable>();

		for (String category : multiMonitor.getCategories()) {
			categoryVars.put(category, new MOPVariable("Category_" + category));
		}

		if (isReturningSKIP(event)) {
			ret += "public final boolean event_" + uniqueId + "(" + event.getMOPParameters().parameterDeclString() + ") {\n";
		} else {
			ret += "public final void event_" + uniqueId + "(" + event.getMOPParameters().parameterDeclString() + ") {\n";
		}

		if (anyReturningSKIP || handlersHave__SKIP)
			ret += "boolean " + skipAroundAdvice + " = false;\n";

		if (isOutermost) {
			ret += lastevent + " = " + idnum + ";\n";
		}

		ret += "HashSet " + monitorSet + " = new HashSet();\n";

		if (event.isStartEvent()) {
			ret += multiMonitor.getOutermostName() + " " + newMonitor + " = new " + multiMonitor.getOutermostName() + "();\n";
			if (monitorInfo != null){
				ret += monitorInfo.copy(newMonitor);
			}
			ret += monitorList + ".add(" + newMonitor + ");\n";
		}

		ret += "Iterator " + it + " = " + monitorList + ".iterator();\n";
		ret += "while (" + it + ".hasNext()){\n";
		ret += multiMonitor.getOutermostName() + " " + monitor + " = (" + multiMonitor.getOutermostName() + ")" + it + ".next();\n";

		ret += multiMonitor.Monitoring(monitor, event, thisJoinPoint);
		if (!multiMonitor.isDoingHandlers()) {
			ret += multiMonitor.doHandlers(monitor, monitor, thisJoinPoint, monitor);
		}

		if (multiMonitor.isDoingHandlers()) {
			ret += "if(" + monitorSet + ".contains(" + monitor + ") ) {\n";
			ret += it + ".remove();\n";
			ret += "} else {\n";
			ret += monitorSet + ".add(" + monitor + ");\n";
			ret += "}\n";
		} else {
			ret += "if(" + monitorSet + ".contains(" + monitor + ")";
			for (MOPVariable categoryVar : categoryVars.values()) {
				ret += " || " + monitor + "." + categoryVar;
			}
			ret += " ) {\n";
			ret += it + ".remove();\n";
			ret += "} else {\n";
			ret += monitorSet + ".add(" + monitor + ");\n";
			ret += "}\n";
		}

		ret += "}\n";

		if (isReturningSKIP(event)) {
			ret += "return " + skipAroundAdvice + ";\n";
		}

		ret += "}\n";

		return ret;
	}

	public String Monitoring(MOPVariable monitorVar, EventDefinition event, MOPVariable thisJoinPoint) {
		String ret = "";

		if (!isDefined)
			return multiMonitor.Monitoring(monitorVar, event, thisJoinPoint);

		if (doActions) {
			if (has__LOC) {
				ret += monitorVar + "." + this.thisJoinPoint + " = " + thisJoinPoint + ";\n";
			}
		}

		if (isReturningSKIP(event)) {
			ret += skipAroundAdvice + " |= ";
		}

		ret += monitorVar + ".event_" + event.getUniqueId() + "(";
		ret += event.getMOPParameters().parameterString();
		ret += ");\n";

		return ret;
	}

	public String doHandlers(MOPVariable monitorVar, MOPVariable monitorVarForReset, MOPVariable thisJoinPoint, MOPVariable monitorVarForMonitor) {
		if (!isDefined)
			return multiMonitor.doHandlers(monitorVar, monitorVarForReset, thisJoinPoint, monitorVarForMonitor);
		else
			return ""; // handlers will be taken care of inside of suffixMonitor
	}

	public String toString() {
		String ret = "";

		MOPVariable monitor = new MOPVariable("monitor");
		MOPVariable newMonitor = new MOPVariable("newMonitor");

		if (isDefined) {
			ret += "class " + monitorName;
			if (isOutermost)
				ret += " extends javamoprt.MOPMonitor";
			ret += " implements Cloneable, javamoprt.MOPObject {\n";

			ret += "Vector<" + multiMonitor.getOutermostName() + "> " + monitorList + " = new Vector<" + multiMonitor.getOutermostName() + ">();\n";

			if (doActions) {
				if (this.has__LOC)
					ret += "org.aspectj.lang.JoinPoint " + thisJoinPoint + ";\n";
			}

			// clone()
			ret += "public Object clone() {\n";
			ret += "try {\n";
			ret += monitorName + " ret = (" + monitorName + ") super.clone();\n";
			if (monitorInfo != null)
				ret += monitorInfo.copy("ret", "this");
			ret += "ret." + monitorList + " = new Vector<" + multiMonitor.getOutermostName() + ">();\n";
			ret += "for(" + multiMonitor.getOutermostName() + " " + monitor + " : this." + monitorList + "){\n";
			ret += multiMonitor.getOutermostName() + " " + newMonitor + " = ";
			ret += "(" + multiMonitor.getOutermostName() + ")" + monitor + ".clone()" + ";\n";
			if (monitorInfo != null)
				ret += monitorInfo.copy(newMonitor, monitor);
			ret += "ret." + monitorList + ".add(" + newMonitor + ");\n";
			ret += "}\n";
			ret += "return ret;\n";
			ret += "}\n";
			ret += "catch (CloneNotSupportedException e) {\n";
			ret += "throw new InternalError(e.toString());\n";
			ret += "}\n";
			ret += "}\n";
			ret += "\n";
			
			// events
			for (EventDefinition event : this.events) {
				ret += this.doEvent(event) + "\n";
			}

			// endObject and some declarations
			if (isOutermost && monitorTermination != null) {
				ret += monitorTermination;
			}

			if (monitorInfo != null){
				ret += monitorInfo.monitorDecl();
			}

			ret += "}\n";
			ret += "\n";
		}

		ret += multiMonitor;

		return ret;
	}
}
