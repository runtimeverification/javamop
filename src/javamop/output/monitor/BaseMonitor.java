package javamop.output.monitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javamop.MOPException;
import javamop.Main;
import javamop.output.MOPJavaCode;
import javamop.output.MOPJavaCodeNoNewLine;
import javamop.output.MOPVariable;
import javamop.output.OptimizedCoenableSet;
import javamop.output.UserJavaCode;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;
import javamop.parser.ast.mopspec.PropertyAndHandlers;
import javamop.parser.ast.stmt.BlockStmt;

public class BaseMonitor extends Monitor {

	MOPVariable thisJoinPoint = new MOPVariable("MOP_thisJoinPoint");
	MOPVariable reset = new MOPVariable("reset");
	MOPVariable lastevent = new MOPVariable("MOP_lastevent");
	MOPVariable skipAroundAdvice = new MOPVariable("skipAroundAdvice");
	MOPVariable conditionFail = new MOPVariable("MOP_conditionFail");

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
	HashMap<String, HandlerMethod> handlerMethods;
	MOPJavaCode initilization;

	String systemAspectName;

	MOPParameters specParam;

	boolean existCondition = false;

	HashMap<MOPParameter, MOPVariable> varsToSave;

	public BaseMonitor(String name, JavaMOPSpec mopSpec, PropertyAndHandlers prop, OptimizedCoenableSet coenableSet, boolean isOutermost,
			boolean doActions) throws MOPException {
		super(name, mopSpec, coenableSet, isOutermost, doActions);

		this.systemAspectName = name + "SystemAspect";

		this.isDefined = true;

		this.prop = prop;

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
		handlerMethods = new HashMap<String, HandlerMethod>();

		this.specParam = mopSpec.getParameters();

		if (doActions) {
			varsToSave = new HashMap<MOPParameter, MOPVariable>();

			for (MOPParameter p : mopSpec.getVarsToSave()) {
				varsToSave.put(p, new MOPVariable("Ref_" + p.getName()));
			}
		}

		HashMap<String, BlockStmt> handlerBodies = prop.getHandlers();
		for (String category : prop.getHandlers().keySet()) {
			MOPVariable categoryVar = new MOPVariable("Category_" + category);
			categoryVars.put(category, categoryVar);

			BlockStmt handlerBody = handlerBodies.get(category);
			if (handlerBody.toString().length() != 0) // if there is a handler for this cateory
				// register the method name for the category
				handlerMethods.put(category, new HandlerMethod(prop, category, specParam, mopSpec.getCommonParamInEvents(), varsToSave, handlerBody, categoryVar, this)); 
		}

		initilization = new MOPJavaCode(prop.getLogicProperty("initialization"));

		events = mopSpec.getEvents();

		if (this.isDefined && mopSpec.isGeneral()) {
			if (mopSpec.isFullBinding() || mopSpec.isConnected())
				monitorInfo = new MonitorInfo(mopSpec);
		}

		for (EventDefinition event : events) {
			if (event.getCondition() != null && event.getCondition().length() != 0) {
				existCondition = true;
				break;
			}
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
			if (conditionStr.contains(":{")) {
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

		if (!condition.isEmpty()) {
			ret += "if (!(" + condition + ")) {\n";

			ret += conditionFail + " = true;\n";

			if (isAround && event.has__SKIP()) {
				ret += "return false;\n";
			} else {
				ret += "return;\n";
			}
			ret += "}\n";
		}

		if (doActions) {
			for (MOPParameter p : varsToSave.keySet()) {
				if (event.getMOPParametersOnSpec().contains(p)) {
					MOPVariable v = varsToSave.get(p);

					ret += "if(" + v + " == null){\n";
					ret += v + " = new WeakReference(" + p.getName() + ");\n";
					ret += "}\n";
				}
			}

		}

		if (isOutermost) {
			ret += lastevent + " = " + idnum + ";\n";
		}

		if (monitorInfo != null)
			ret += monitorInfo.union(event.getMOPParametersOnSpec());

		ret += localDeclaration;

		if (prop.getVersionedStack()) {
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

		if (eventAction != null){
			
			for (MOPParameter p : event.getUsedParametersIn(specParam)) {
				if(!event.getMOPParametersOnSpec().contains(p)){
					MOPVariable v = this.varsToSave.get(p);

					ret += p.getType() + " " + p.getName() + " = null;\n";
					ret += "if(" + v + " != null){\n";
					ret += p.getName() + " = (" + p.getType() + ")" + v + ".get();\n";
					ret += "}\n";
				}
			}

			
			ret += eventAction;
		}

		if (isAround && event.has__SKIP()) {
			ret += "return " + skipAroundAdvice + ";\n";
		}

		ret += "}\n";

		return ret;
	}

	public String Monitoring(MOPVariable monitorVar, EventDefinition event, MOPVariable thisJoinPoint) {
		String ret = "";
		boolean checkSkip = event.getPos().equals("around");

		if (doActions) {
			if (has__LOC) {
				ret += monitorVar + "." + this.thisJoinPoint + " = " + thisJoinPoint + ";\n";
			}
		}

		if (checkSkip && event.has__SKIP()) {
			ret += skipAroundAdvice + " |= ";
		}
		ret += monitorVar + ".event_" + event.getUniqueId() + "(";
		ret += event.getMOPParameters().parameterString();
		ret += ");\n";

		if (isOutermost) {
			ret += this.callHandlers(monitorVar, monitorVar, event, event.getMOPParametersOnSpec(), thisJoinPoint, monitorVar, checkSkip);
		}

		return ret;
	}

	public String callHandlers(MOPVariable monitorVar, MOPVariable monitorVarForReset, EventDefinition event, MOPParameters eventParam,
			MOPVariable thisJoinPoint, MOPVariable monitorVarForMonitor, boolean checkSkip) {
		String ret = "";

		if (event.getCondition() != null && event.getCondition().length() != 0) {
			ret += "if(" + monitorVar + "." + conditionFail + "){\n";
			ret += monitorVar + "." + conditionFail + " = false;\n";
			ret += "} else {";
		}

		for (String category : handlerMethods.keySet()) {
			HandlerMethod handlerMethod = handlerMethods.get(category);

			ret += "if(" + monitorVar + "." + categoryVars.get(category) + ") {\n";

			if (checkSkip && handlerMethod.has__SKIP()) {
				ret += skipAroundAdvice + " |= ";
			}

			ret += monitorVar + "." + handlerMethod.getMethodName() + "(";
			ret += eventParam.parameterStringIn(specParam);
			ret += ");\n";

			ret += "}\n";
		}

		if (event.getCondition() != null && event.getCondition().length() != 0) {
			ret += "}\n";
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

			// references for saved parameters
			for (MOPVariable v : varsToSave.values()) {
				ret += "WeakReference " + v + " = null;\n";
			}

		}

		if (existCondition) {
			ret += "boolean " + conditionFail + " = false;\n";
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
		if (prop.getVersionedStack()) {
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

		// handlers
		for (HandlerMethod handlerMethod : this.handlerMethods.values()) {
			ret += handlerMethod + "\n";
		}

		// reset
		ret += "public final void reset() {\n";
		if (monitorInfo != null)
			ret += monitorInfo.initConnected();

		if (prop.getVersionedStack()) {
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
