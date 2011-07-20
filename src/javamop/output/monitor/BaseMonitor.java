package javamop.output.monitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javamop.MOPException;
import javamop.Main;
import javamop.output.MOPJavaCode;
import javamop.output.MOPJavaCodeNoNewLine;
import javamop.output.MOPVariable;
import javamop.output.OptimizedCoenableSet;
import javamop.output.UserJavaCode;
import javamop.output.aspect.MOPStatistics;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.PropertyAndHandlers;
import javamop.parser.ast.stmt.BlockStmt;

public class BaseMonitor extends Monitor {

	MOPVariable thisJoinPoint = new MOPVariable("MOP_thisJoinPoint");
	MOPVariable reset = new MOPVariable("reset");
	MOPVariable lastevent = new MOPVariable("MOP_lastevent");
	MOPVariable skipAroundAdvice = new MOPVariable("skipAroundAdvice");

	PropertyAndHandlers prop;

	List<EventDefinition> events;

	MOPJavaCode cloneCode;
	MOPJavaCode localDeclaration;
	MOPJavaCode stateDeclaration;
	MOPJavaCode resetCode;
	MOPJavaCode hashcodeCode;
	MOPJavaCode equalsCode;
	UserJavaCode monitorDeclaration;
	HashMap<String, MOPVariable> categoryVars;
	MOPJavaCode initilization;
	HashMap<String, BlockStmt> handlers;
	
	String systemAspectName;

	public BaseMonitor(String name, JavaMOPSpec mopSpec, PropertyAndHandlers prop, OptimizedCoenableSet coenableSet, boolean isOutermost, boolean doActions)
			throws MOPException {
		super(name, mopSpec, coenableSet, isOutermost, doActions);

		this.systemAspectName = name + "SystemAspect"; 
		
		this.isDefined = true;

		this.prop = prop;
		this.handlers = prop.getHandlers();

		this.monitorName = new MOPVariable(mopSpec.getName() + "Monitor_" + prop.getPropertyId());

		if (isOutermost) {
			monitorTermination = new MonitorTermination(name, mopSpec, mopSpec.getEvents(), coenableSet);
		}

		cloneCode = new MOPJavaCode(prop.getLogicProperty("clone"), monitorName);
		localDeclaration = new MOPJavaCode(prop.getLogicProperty("local declaration"), monitorName);
		stateDeclaration = new MOPJavaCode(prop.getLogicProperty("state declaration"), monitorName);
		resetCode = new MOPJavaCode(prop.getLogicProperty("reset"), monitorName);
		hashcodeCode = new MOPJavaCode(prop.getLogicProperty("hashcode"), monitorName);
		equalsCode = new MOPJavaCode(prop.getLogicProperty("equals"), monitorName);

		monitorDeclaration = new UserJavaCode(mopSpec.getDeclarationsStr());
		categoryVars = new HashMap<String, MOPVariable>();
		for (String key : prop.getHandlers().keySet()) {
			categoryVars.put(key, new MOPVariable("Category_" + key));
		}

		initilization = new MOPJavaCode(prop.getLogicProperty("initialization"));

		events = mopSpec.getEvents();

		if (this.isDefined && mopSpec.isGeneral()){
			if(mopSpec.isFullBinding() || mopSpec.isConnected())
				monitorInfo = new MonitorInfo(mopSpec);
		}
	}

	public MOPVariable getOutermostName() {
		return monitorName;
	}

	public Set<String> getNames() {
		Set<String> ret = new HashSet<String>();

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
		return false;
	}

	public Set<String> getCategories() {
		HashSet<String> ret = new HashSet<String>(categoryVars.keySet());

		return ret;
	}

	public boolean isReturningSKIP(EventDefinition event) {
		boolean isAround = event.getPos().equals("around");
		return isAround && event.has__SKIP();
	}

	public String doEvent(EventDefinition event) {
		String ret = "";

		boolean isAround = event.getPos().equals("around");
		String uniqueId = event.getUniqueId();
		int idnum = event.getIdNum();
		MOPJavaCode condition = new MOPJavaCode(event.getCondition(), monitorName);
		MOPJavaCode eventMonitoringCode = new MOPJavaCode(prop.getEventMonitoringCode(event.getId()), monitorName);
		MOPJavaCode aftereventMonitoringCode = new MOPJavaCode(prop.getAfterEventMonitoringCode(event.getId()), monitorName);
		MOPJavaCode monitoringBody = new MOPJavaCode(prop.getLogicProperty("monitoring body"), monitorName);
		MOPJavaCode stackManage = new MOPJavaCode(prop.getLogicProperty("stack manage"), monitorName);
		HashMap<String, MOPJavaCode> categoryConditions = new HashMap<String, MOPJavaCode>();
		MOPJavaCode eventAction = null;

		for (String handlerName : prop.getHandlers().keySet()) {
			String conditionStr = prop.getLogicProperty(handlerName + " condition");
			if(conditionStr.contains(":{")){
				HashMap<String, String> conditions = new HashMap<String, String>();
				prop.parseMonitoredEvent(conditions, conditionStr);
				conditionStr = conditions.get(event.getId());
			}
			
			if (conditionStr != null) {
				categoryConditions.put(handlerName, new MOPJavaCodeNoNewLine(conditionStr, monitorName));
			}
		}

		if (doActions && event.getAction() != null && event.getAction().getStmts() != null && event.getAction().getStmts().size() != 0) {
			String eventActionStr = event.getAction().toString();

			eventActionStr = eventActionStr.replaceAll("__RESET", "this.reset()");
			eventActionStr = eventActionStr.replaceAll("__LOC", "this." + thisJoinPoint + ".getSourceLocation().toString()");
			eventActionStr = eventActionStr.replaceAll("__SKIP", skipAroundAdvice + " = true");

			eventAction = new MOPJavaCode(eventActionStr);
		}

		if (isAround && event.has__SKIP()) {
			ret += "public final boolean event_" + uniqueId + "(" + event.getMOPParameters().parameterDeclString() + ") {\n";
		} else {
			ret += "public final void event_" + uniqueId + "(" + event.getMOPParameters().parameterDeclString() + ") {\n";
		}

		if (event.has__SKIP())
			ret += "boolean " + skipAroundAdvice + " = false;\n";

		if (doActions && !condition.isEmpty()) {
			ret += "if (!(" + condition + ")) {\n";
			if (isAround && event.has__SKIP()) {
				ret += "return false;\n";
			} else {
				ret += "return;\n";
			}
			ret += "}\n";
		}

		if (isOutermost) {
			ret += lastevent + " = " + idnum + ";\n";
		}
		
		if (monitorInfo != null)
			ret += monitorInfo.union(event.getMOPParametersOnSpec());

		ret += localDeclaration;

		if(prop.getVersionedStack()){
			MOPVariable global_depth = new MOPVariable("global_depth");
			MOPVariable version = new MOPVariable("version");
			
			ret += "int[] " + global_depth + " = (int[])(" + systemAspectName + ".t_global_depth.get());\n";
			ret += "int[] " + version + " = (int[])(" + systemAspectName + ".t_version.get());\n";
		}

		ret += stackManage + "\n";
		
		ret += eventMonitoringCode;

		ret += monitoringBody;

		String categoryCode = "";
		for (Entry<String, MOPJavaCode> entry : categoryConditions.entrySet()) {
			categoryCode += categoryVars.get(entry.getKey()) + " = " + entry.getValue() + ";\n";
		}
		
		if (monitorInfo != null)
			ret += monitorInfo.computeCategory(categoryCode);
		else
			ret += categoryCode;

		ret += aftereventMonitoringCode;

		if (eventAction != null)
			ret += eventAction;

		if (isAround && event.has__SKIP()) {
			ret += "return " + skipAroundAdvice + ";\n";
		}

		ret += "}\n";

		return ret;
	}

	public String Monitoring(MOPVariable monitorVar, EventDefinition event, MOPVariable thisJoinPoint) {
		String ret = "";

		if (doActions) {
			if (has__LOC) {
				ret += monitorVar + "." + this.thisJoinPoint + " = " + thisJoinPoint + ";\n";
			}
		}

		if (event.getPos().equals("around") && event.has__SKIP()) {
			ret += skipAroundAdvice + " |= ";
		}
		ret += monitorVar + ".event_" + event.getUniqueId() + "(";
		ret += event.getMOPParameters().parameterString();
		ret += ");\n";

		if (isOutermost)
			ret += this.doHandlers(monitorVar, monitorVar, thisJoinPoint, monitorVar);

		return ret;
	}

	public String doHandlers(MOPVariable monitorVar, MOPVariable monitorVarForReset, MOPVariable thisJoinPoint, MOPVariable monitorVarForMonitor) {
		String ret = "";

		for (String handlerName : categoryVars.keySet()) {
			String handlerBody = handlers.get(handlerName).toString();
			if (handlerBody.length() != 0) {

				if (Main.statistics) {
					ret += "if(" + monitorVar + "." + categoryVars.get(handlerName) + ") {\n";
					ret += stat.categoryInc(prop, handlerName);
					ret += "}\n";
				}

				ret += "if(" + monitorVar + "." + categoryVars.get(handlerName) + ") ";

				handlerBody = handlerBody.replaceAll("__RESET", monitorVarForReset + ".reset()");
				handlerBody = handlerBody.replaceAll("__LOC", thisJoinPoint + ".getSourceLocation().toString()");
				handlerBody = handlerBody.replaceAll("__SKIP", skipAroundAdvice + " = true");

				ret += handlerBody + "\n";
			}
		}

		return ret;
	}

	public String toString() {
		String ret = "";

		ret += "class " + monitorName;
		if (isOutermost)
			ret += " extends javamoprt.MOPMonitor";
		ret += " implements Cloneable, javamoprt.MOPObject {\n";

		// clone()
		ret += "public Object clone() {\n";
		if (Main.statistics) {
			ret += stat.incNumMonitor();
		}
		ret += "try {\n";
		ret += monitorName + " ret = (" + monitorName + ") super.clone();\n";
		if (monitorInfo != null)
			ret += monitorInfo.copy("ret", "this");
		ret += cloneCode;
		ret += "return ret;\n";
		ret += "}\n";
		ret += "catch (CloneNotSupportedException e) {\n";
		ret += "throw new InternalError(e.toString());\n";
		ret += "}\n";
		ret += "}\n";

		if (doActions) {
			// monitor variables
			ret += monitorDeclaration + "\n";
			if (this.has__LOC)
				ret += "org.aspectj.lang.JoinPoint " + thisJoinPoint + ";\n";
		}

		// state declaration
		ret += stateDeclaration + "\n";

		// category condition
		for (String category : categoryVars.keySet()) {
			ret += "boolean " + categoryVars.get(category) + " = false;\n";
		}
		ret += "\n";

		// constructor
		ret += "public " + monitorName + " () {\n";
		if(prop.getVersionedStack()){
			MOPVariable global_depth = new MOPVariable("global_depth");
			MOPVariable version = new MOPVariable("version");
			
			ret += "int[] " + global_depth + " = (int[])(" + systemAspectName + ".t_global_depth.get());\n";
			ret += "int[] " + version + " = (int[])(" + systemAspectName + ".t_version.get());\n";
		}
		ret += localDeclaration;
		ret += initilization;
		if (Main.statistics) {
			ret += stat.incNumMonitor();
		}
		ret += "}\n";
		ret += "\n";

		// events
		for (EventDefinition event : this.events) {
			ret += this.doEvent(event) + "\n";
		}

		// reset
		ret += "public final void reset() {\n";
		if (monitorInfo != null)
			ret += monitorInfo.initConnected();

		if(prop.getVersionedStack()){
			MOPVariable global_depth = new MOPVariable("global_depth");
			MOPVariable version = new MOPVariable("version");
			
			ret += "int[] " + global_depth + " = (int[])(" + systemAspectName + ".t_global_depth.get());\n";
			ret += "int[] " + version + " = (int[])(" + systemAspectName + ".t_version.get());\n";
		}
		
		ret += localDeclaration;
		ret += resetCode;
		if (isOutermost) {
			ret += lastevent + " = -1;\n";
		}
		for (String category : categoryVars.keySet()) {
			ret += categoryVars.get(category) + " = false;\n";
		}
		ret += "}\n";
		ret += "\n";

		// hashcode
		if (!hashcodeCode.isEmpty()) {
			ret += "public final int hashCode() {\n";
			ret += hashcodeCode;
			ret += "}\n";
			ret += "\n";
		}

		// equals
		if (!equalsCode.isEmpty()) {
			ret += "public final boolean equals(Object o) {\n";
			ret += equalsCode;
			ret += "}\n";
			ret += "\n";
		}

		// endObject and some declarations
		if (isOutermost) {
			ret += monitorTermination;
		}

		if (monitorInfo != null)
			ret += monitorInfo.monitorDecl();

		ret += "}\n";

		return ret;
	}
}
