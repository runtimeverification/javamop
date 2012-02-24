package javamop.output.monitor;

import java.util.List;
import java.util.Set;

import javamop.MOPException;
import javamop.output.MOPVariable;
import javamop.output.OptimizedCoenableSet;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameters;
import javamop.parser.ast.mopspec.PropertyAndHandlers;
import javamop.parser.ast.stmt.BlockStmt;

public class WrapperMonitor extends Monitor {

	MOPVariable loc = new MOPVariable("MOP_loc");
	MOPVariable staticsig = new MOPVariable("MOP_staticsig");
	MOPVariable lastevent = new MOPVariable("MOP_lastevent");
	MOPVariable skipAroundAdvice = new MOPVariable("MOP_skipAroundAdvice");
	MOPVariable thisJoinPoint = new MOPVariable("thisJoinPoint");

	MOPVariable monitor = new MOPVariable("monitor");
	MOPVariable disable = new MOPVariable("disable");
	MOPVariable tau = new MOPVariable("tau");

	List<EventDefinition> events;

	SuffixMonitor suffixMonitor = null;
	boolean existSkip = false;

	public WrapperMonitor(String name, JavaMOPSpec mopSpec, OptimizedCoenableSet coenableSet, boolean isOutermost) throws MOPException {
		super(name, mopSpec, coenableSet, isOutermost);

		this.isDefined = mopSpec.isGeneral();

		if (this.isDefined) {
			monitorName = new MOPVariable(mopSpec.getName() + "Wrapper");

			if (isOutermost) {
				monitorTermination = new MonitorTermination(name, mopSpec, mopSpec.getEvents(), coenableSet);
			}

			suffixMonitor = new SuffixMonitor(name, mopSpec, coenableSet, false);

			events = mopSpec.getEvents();

			if (mopSpec.isFullBinding() || mopSpec.isConnected()) {
				monitorInfo = new MonitorInfo(mopSpec);
			}

			for (PropertyAndHandlers prop : mopSpec.getPropertiesAndHandlers()) {
				if (!existSkip) {
					for (BlockStmt handler : prop.getHandlers().values()) {
						if (handler.toString().indexOf("__SKIP") != -1) {
							existSkip = true;
							break;
						}
					}
				}
			}

			for (EventDefinition event : events) {
				if (event.has__SKIP()) {
					existSkip = true;
					break;
				}
			}
		} else {
			suffixMonitor = new SuffixMonitor(name, mopSpec, coenableSet, isOutermost);
		}
	}

	public String newWrapper(MOPVariable monitorVar, MOPParameters vars) {
		String ret = "";
		ret += monitorVar + " = new " + monitorName + "();\n";
		if (monitorInfo != null)
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

	public Set<MOPVariable> getCategoryVars() {
		return suffixMonitor.getCategoryVars();
	}

	public String printEventMethod(EventDefinition event) {
		String ret = "";

		String uniqueId = event.getUniqueId();
		int idnum = event.getIdNum();

		ret += "public final void event_" + uniqueId + "(" + event.getMOPParameters().parameterDeclString() + ") {\n";

		if (isOutermost) {
			ret += lastevent + " = " + idnum + ";\n";
		}

		ret += suffixMonitor.Monitoring(monitor, event, loc, staticsig);

		ret += "}\n";

		return ret;
	}

	public String Monitoring(MOPVariable monitorVar, EventDefinition event, MOPVariable loc, MOPVariable staticsig) {
		String ret = "";
		boolean checkSkip = event.getPos().equals("around");

		if (!isDefined)
			return suffixMonitor.Monitoring(monitorVar, event, loc, staticsig);

		ret += "if (" + monitorVar + "." + monitor + " != null){\n";

		if (has__LOC) {
			if (loc != null)
				ret += monitorVar + "." + this.loc + " = " + loc + ";\n";
			else
				ret += monitorVar + "." + this.loc + " = " + "thisJoinPoint.getSourceLocation().toString()" + ";\n";
		}

		if (has__STATICSIG) {
			if(staticsig != null)
				ret += monitorVar + "." + this.staticsig + " = " + staticsig + ";\n";
			else
				ret += monitorVar + "." + this.staticsig + " = " + "thisJoinPoint.getStaticPart().getSignature()" + ";\n";
		}

		if (this.hasThisJoinPoint) {
			ret += monitorVar + "." + this.thisJoinPoint + " = " + this.thisJoinPoint + ";\n";
		}

		if (checkSkip && event.has__SKIP()) {
			ret += monitorVar + "." + skipAroundAdvice + " = false;\n";
		}

		ret += monitorVar + ".event_" + event.getUniqueId() + "(";
		ret += event.getMOPParameters().parameterString();
		ret += ");\n";

		if (checkSkip && event.has__SKIP()) {
			ret += skipAroundAdvice + " |= " + monitorVar + "." + skipAroundAdvice + ";\n";
		}

		if (this.hasThisJoinPoint) {
			ret += monitorVar + "." + this.thisJoinPoint + " = null;\n";
		}

		ret += "}\n";

		return ret;
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

			if (this.has__LOC)
				ret += "String " + loc + ";\n";
			if (this.hasThisJoinPoint)
				ret += "JoinPoint " + thisJoinPoint + " = null;\n";
			if (existSkip)
				ret += "boolean " + skipAroundAdvice + " = false;\n";

			ret += "public long " + disable + " = 1;\n";
			ret += "public long " + tau + " = 1;\n";
			ret += "\n";

			// events
			for (EventDefinition event : this.events) {
				ret += this.printEventMethod(event) + "\n";
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
