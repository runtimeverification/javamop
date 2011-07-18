package javamop.output.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javamop.MOPException;
import javamop.Main;
import javamop.output.MOPJavaCode;
import javamop.output.MOPVariable;
import javamop.output.OptimizedCoenableSet;
import javamop.output.UserJavaCode;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.PropertyAndHandlers;

public class MultiMonitor extends Monitor {

	MOPVariable thisJoinPoint = new MOPVariable("MOP_thisJoinPoint");
	MOPVariable lastevent = new MOPVariable("MOP_lastevent");
	MOPVariable skipAroundAdvice = new MOPVariable("skipAroundAdvice");

	List<EventDefinition> events;

	ArrayList<BaseMonitor> baseMonitors = new ArrayList<BaseMonitor>();
	HashMap<BaseMonitor, MOPVariable> monitorVars = new HashMap<BaseMonitor, MOPVariable>();
	RawMonitor rawMonitor = null;

	UserJavaCode monitorDeclaration;

	public MultiMonitor(String name, JavaMOPSpec mopSpec, OptimizedCoenableSet coenableSet, boolean isOutermost, boolean doActions) throws MOPException {
		super(name, mopSpec, coenableSet, isOutermost, doActions);

		this.isDefined = mopSpec.getPropertiesAndHandlers().size() > 1;

		if (this.isDefined) {
			monitorName = new MOPVariable(mopSpec.getName() + "MultiMonitor");

			for (PropertyAndHandlers prop : mopSpec.getPropertiesAndHandlers()) {
				BaseMonitor baseMonitor = new BaseMonitor(name, mopSpec, prop, coenableSet, false, false);
				MOPVariable monitorVar = new MOPVariable("monitor_" + prop.getPropertyId());

				baseMonitors.add(baseMonitor);
				monitorVars.put(baseMonitor, monitorVar);
			}

			if (isOutermost)
				monitorTermination = new MonitorTermination(name, mopSpec, mopSpec.getEvents(), coenableSet);

			monitorDeclaration = new UserJavaCode(mopSpec.getDeclarationsStr());

			events = mopSpec.getEvents();
		} else {
			if (mopSpec.getPropertiesAndHandlers().size() == 1) {
				PropertyAndHandlers prop = mopSpec.getPropertiesAndHandlers().get(0);
				if (Main.toJavaLib)
					baseMonitors.add(new JavaLibMonitor(name, mopSpec, prop, coenableSet, isOutermost, doActions));
				else
					baseMonitors.add(new BaseMonitor(name, mopSpec, prop, coenableSet, isOutermost, doActions));
			} else if (mopSpec.getPropertiesAndHandlers().size() == 0) {
				rawMonitor = new RawMonitor(name, mopSpec, coenableSet, isOutermost, doActions);
			} else {
				throw new MOPException("The number of properties is weird. It should not happen. Report this to the developers.");
			}
		}

		if (this.isDefined && mopSpec.isGeneral()) {
			if (mopSpec.isFullBinding() || mopSpec.isConnected())
				monitorInfo = new MonitorInfo(mopSpec);
		}
	}

	public MOPVariable getOutermostName() {
		if (isDefined)
			return monitorName;
		else {
			if (!baseMonitors.isEmpty())
				return baseMonitors.get(0).getOutermostName();
			if (rawMonitor != null)
				return rawMonitor.getOutermostName();
		}
		return null;
	}

	public Set<String> getNames() {
		Set<String> ret = new HashSet<String>();
		for (BaseMonitor monitor : baseMonitors) {
			ret.add(monitor.getOutermostName().toString());
		}
		if (rawMonitor != null)
			ret.add(rawMonitor.getOutermostName().toString());
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
		}
		if (baseMonitors.size() > 0)
			return baseMonitors.get(0).isDoingHandlers();
		else if (rawMonitor != null)
			return rawMonitor.isDoingHandlers();

		return false;
	}

	public Set<String> getCategories() {
		if (isDefined) {
			HashSet<String> ret = new HashSet<String>();
			for (BaseMonitor baseMonitor : baseMonitors)
				ret.addAll(baseMonitor.getCategories());

			return ret;
		} else if (!baseMonitors.isEmpty()) {
			return baseMonitors.get(0).getCategories();
		} else if (rawMonitor != null) {
			return rawMonitor.getCategories();
		}

		return null;
	}

	public boolean isReturningSKIP(EventDefinition event) {
		if (isDefined) {
			boolean isAround = event.getPos().equals("around");
			boolean anyReturningSKIP = false;
			for (BaseMonitor baseMonitor : baseMonitors)
				anyReturningSKIP |= baseMonitor.isReturningSKIP(event);

			return isAround && (anyReturningSKIP || handlersHave__SKIP);
		} else if (!baseMonitors.isEmpty()) {
			return baseMonitors.get(0).isReturningSKIP(event);
		} else if (rawMonitor != null) {
			return rawMonitor.isReturningSKIP(event);
		}
		return false;
	}

	public String doEvent(EventDefinition event) {
		String ret = "";

		boolean isAround = event.getPos().equals("around");
		String uniqueId = event.getUniqueId();
		int idnum = event.getIdNum();
		MOPJavaCode condition = new MOPJavaCode(event.getCondition(), monitorName);
		MOPJavaCode eventAction = null;

		if (doActions && event.getAction() != null && event.getAction().getStmts() != null && event.getAction().getStmts().size() != 0) {
			String eventActionStr = event.getAction().toString();

			eventActionStr = eventActionStr.replaceAll("__RESET", "this.reset()");
			eventActionStr = eventActionStr.replaceAll("__LOC", "this." + thisJoinPoint + ".getSourceLocation().toString()");
			eventActionStr = eventActionStr.replaceAll("__MONITOR", "this");
			eventActionStr = eventActionStr.replaceAll("__SKIP", skipAroundAdvice + " = true");

			eventAction = new MOPJavaCode(eventActionStr);
		}

		boolean anyReturningSKIP = false;
		for (BaseMonitor baseMonitor : baseMonitors)
			anyReturningSKIP |= baseMonitor.isReturningSKIP(event);

		if (isAround && (anyReturningSKIP || handlersHave__SKIP)) {
			ret += "public final boolean event_" + uniqueId + "(" + event.getMOPParameters().parameterDeclString() + ") {\n";
		} else {
			ret += "public final void event_" + uniqueId + "(" + event.getMOPParameters().parameterDeclString() + ") {\n";
		}

		if (handlersHave__SKIP || anyReturningSKIP)
			ret += "boolean " + skipAroundAdvice + " = false;\n";

		if (doActions && !condition.isEmpty()) {
			ret += "if (!(" + condition + ")) {\n";
			if (isAround && (anyReturningSKIP || handlersHave__SKIP)) {
				ret += "return false;\n";
			} else {
				ret += "return;\n";
			}
			ret += "}\n";
		}

		if (isOutermost) {
			ret += lastevent + " = " + idnum + ";\n";
		}

		for (BaseMonitor baseMonitor : baseMonitors) {
			if (event.isStartEvent()) {
				ret += "if (" + monitorVars.get(baseMonitor) + " == null){\n";
				ret += monitorVars.get(baseMonitor) + " = new " + baseMonitor.getOutermostName() + "();\n";
				if (monitorInfo != null)
					ret += monitorInfo.copy(monitorVars.get(baseMonitor));
				ret += "}\n";

				ret += baseMonitor.Monitoring(monitorVars.get(baseMonitor), event, thisJoinPoint);
				ret += baseMonitor.doHandlers(monitorVars.get(baseMonitor), new MOPVariable("this"), thisJoinPoint, new MOPVariable("this"));
			} else {
				ret += "if (" + monitorVars.get(baseMonitor) + " != null){\n";
				ret += baseMonitor.Monitoring(monitorVars.get(baseMonitor), event, thisJoinPoint);
				ret += baseMonitor.doHandlers(monitorVars.get(baseMonitor), new MOPVariable("this"), thisJoinPoint, new MOPVariable("this"));
				ret += "}\n";
			}
		}

		if (eventAction != null)
			ret += eventAction;

		if (isAround && (anyReturningSKIP || handlersHave__SKIP)) {
			ret += "return " + skipAroundAdvice + ";\n";
		}

		ret += "}\n";

		return ret;
	}

	public String Monitoring(MOPVariable monitorVar, EventDefinition event, MOPVariable thisJoinPoint) {
		String ret = "";

		if (!isDefined) {
			if (!baseMonitors.isEmpty())
				return baseMonitors.get(0).Monitoring(monitorVar, event, thisJoinPoint);
			else if (rawMonitor != null)
				return rawMonitor.Monitoring(monitorVar, event, thisJoinPoint);
			else
				return ret;
		}

		boolean isAround = event.getPos().equals("around");
		boolean anyReturningSKIP = false;
		for (BaseMonitor baseMonitor : baseMonitors)
			anyReturningSKIP |= baseMonitor.isReturningSKIP(event);

		if (doActions) {
			if (has__LOC) {
				ret += monitorVar + "." + this.thisJoinPoint + " = " + thisJoinPoint + ";\n";
			}
		}

		if (isAround && (anyReturningSKIP || handlersHave__SKIP)) {
			ret += skipAroundAdvice + " |= ";
		}
		ret += monitorVar + ".event_" + event.getUniqueId() + "(";
		ret += event.getMOPParameters().parameterString();
		ret += ");\n";

		return ret;
	}

	public String doHandlers(MOPVariable monitorVar, MOPVariable monitorVarForReset, MOPVariable thisJoinPoint, MOPVariable monitorVarForMonitor) {
		String ret = "";

		if (!isDefined) {
			if (!baseMonitors.isEmpty())
				return baseMonitors.get(0).doHandlers(monitorVar, monitorVarForReset, thisJoinPoint, monitorVarForMonitor);
			else if (rawMonitor != null)
				return rawMonitor.doHandlers(monitorVar, monitorVarForReset, thisJoinPoint, monitorVarForMonitor);
			else
				return ret;
		}

		// There is no need to do handlers for MultiMonitor!!

		return ret;
	}

	public String toString() {
		String ret = "";

		if (isDefined) {
			ret += "class " + monitorName;
			if (isOutermost)
				ret += " extends javamoprt.MOPMonitor";
			ret += " implements Cloneable, javamoprt.MOPObject {\n";

			if (doActions) {
				ret += monitorDeclaration + "\n";
				if (this.has__LOC)
					ret += "org.aspectj.lang.JoinPoint " + thisJoinPoint + ";\n";
			}

			for (BaseMonitor baseMonitor : baseMonitors) {
				ret += baseMonitor.getOutermostName() + " " + monitorVars.get(baseMonitor) + " = null;\n";
			}

			// clone()
			ret += "public Object clone() {\n";
			ret += "try {\n";
			ret += monitorName + " ret = (" + monitorName + ") super.clone();\n";
			if (monitorInfo != null)
				ret += monitorInfo.copy("ret", "this");
			for (BaseMonitor baseMonitor : baseMonitors) {
				ret += "ret." + monitorVars.get(baseMonitor) + " = (" + baseMonitor.getOutermostName() + ")" + "this." + monitorVars.get(baseMonitor)
						+ ".clone();\n";
				if (monitorInfo != null)
					ret += monitorInfo.copy("ret." + monitorVars.get(baseMonitor), "this." + monitorVars.get(baseMonitor));
			}
			ret += "return ret;\n";
			ret += "}\n";
			ret += "catch (CloneNotSupportedException e) {\n";
			ret += "throw new InternalError(e.toString());\n";
			ret += "}\n";
			ret += "}\n";

			// events
			for (EventDefinition event : this.events) {
				ret += this.doEvent(event) + "\n";
			}

			// reset
			ret += "public final void reset() {\n";
			if (isOutermost) {
				ret += lastevent + " = -1;\n";
			}
			for (BaseMonitor baseMonitor : baseMonitors) {
				ret += "if (" + monitorVars.get(baseMonitor) + " != null){\n";
				ret += monitorVars.get(baseMonitor) + ".reset();\n";
				ret += "}\n";
			}
			ret += "}\n";
			ret += "\n";

			// endObject and some declarations
			if (isOutermost && monitorTermination != null) {
				ret += monitorTermination;
			}

			if (monitorInfo != null)
				ret += monitorInfo.monitorDecl();

			ret += "}\n";
			ret += "\n";
		}

		for (BaseMonitor baseMonitor : baseMonitors)
			ret += baseMonitor + "\n";

		if (rawMonitor != null)
			ret += rawMonitor + "\n";

		return ret;
	}
}
