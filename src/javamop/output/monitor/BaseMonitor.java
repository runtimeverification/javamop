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
import javamop.output.combinedaspect.indexingtree.reftree.RefTree;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;
import javamop.parser.ast.mopspec.PropertyAndHandlers;
import javamop.parser.ast.stmt.BlockStmt;

class PropMonitor {
	MOPJavaCode cloneCode;
	MOPJavaCode localDeclaration;
	MOPJavaCode stateDeclaration;
	MOPJavaCode resetCode;
	MOPJavaCode hashcodeCode;
	MOPJavaCode equalsCode;
	MOPJavaCode initilization;
	
	MOPVariable hashcodeMethod = null;

	HashMap<String, MOPVariable> categoryVars = new HashMap<String, MOPVariable>();
	HashMap<String, HandlerMethod> handlerMethods = new HashMap<String, HandlerMethod>();
	HashMap<String, MOPVariable> eventMethods = new HashMap<String, MOPVariable>();
}

public class BaseMonitor extends Monitor {
	// fields
	MOPVariable loc = new MOPVariable("MOP_loc");
	MOPVariable staticsig = new MOPVariable("MOP_staticsig");
	MOPVariable lastevent = new MOPVariable("MOP_lastevent");
	MOPVariable skipAroundAdvice = new MOPVariable("MOP_skipAroundAdvice");
	MOPVariable conditionFail = new MOPVariable("MOP_conditionFail");
	MOPVariable thisJoinPoint = new MOPVariable("thisJoinPoint");

	// methods
	MOPVariable reset = new MOPVariable("reset");

	// info about spec
	List<PropertyAndHandlers> props;
	List<EventDefinition> events;
	MOPParameters specParam;
	UserJavaCode monitorDeclaration;
	String systemAspectName;
	boolean existCondition = false;
	boolean existSkip = false;
	HashMap<MOPParameter, MOPVariable> varsToSave;

	HashMap<PropertyAndHandlers, PropMonitor> propMonitors = new HashMap<PropertyAndHandlers, PropMonitor>();

	public BaseMonitor(String name, JavaMOPSpec mopSpec, OptimizedCoenableSet coenableSet, boolean isOutermost) throws MOPException {
		super(name, mopSpec, coenableSet, isOutermost);

		this.isDefined = true;
		this.monitorName = new MOPVariable(mopSpec.getName() + "Monitor");
		this.systemAspectName = name + "SystemAspect";
		this.events = mopSpec.getEvents();
		this.props = mopSpec.getPropertiesAndHandlers();
		this.monitorDeclaration = new UserJavaCode(mopSpec.getDeclarationsStr());
		this.specParam = mopSpec.getParameters();

		if (isOutermost) {
			varInOutermostMonitor = new VarInOutermostMonitor(name, mopSpec, mopSpec.getEvents());
			monitorTermination = new MonitorTermination(name, mopSpec, mopSpec.getEvents(), coenableSet);
		}

		for (PropertyAndHandlers prop : props) {
			PropMonitor propMonitor = new PropMonitor();

			HashSet<String> cloneLocal = new HashSet<String>();
			cloneLocal.add("ret");
			
			propMonitor.cloneCode = new MOPJavaCode(prop, prop.getLogicProperty("clone"), monitorName, cloneLocal);
			propMonitor.localDeclaration = new MOPJavaCode(prop, prop.getLogicProperty("local declaration"), monitorName);
			propMonitor.stateDeclaration = new MOPJavaCode(prop, prop.getLogicProperty("state declaration"), monitorName);
			propMonitor.resetCode = new MOPJavaCode(prop, prop.getLogicProperty("reset"), monitorName);
			propMonitor.hashcodeCode = new MOPJavaCode(prop, prop.getLogicProperty("hashcode"), monitorName);
			propMonitor.equalsCode = new MOPJavaCode(prop, prop.getLogicProperty("equals"), monitorName);
			propMonitor.initilization = new MOPJavaCode(prop, prop.getLogicProperty("initialization"), monitorName);

			HashMap<String, BlockStmt> handlerBodies = prop.getHandlers();
			for (String category : prop.getHandlers().keySet()) {
				MOPVariable categoryVar = new MOPVariable("Prop_" + prop.getPropertyId() + "_Category_" + category);
				propMonitor.categoryVars.put(category, categoryVar);

				BlockStmt handlerBody = handlerBodies.get(category);

				if (handlerBody.toString().length() != 0)
					propMonitor.handlerMethods.put(category, new HandlerMethod(prop, category, specParam, mopSpec.getCommonParamInEvents(), varsToSave, handlerBody, categoryVar, this));
			}
			
			for(EventDefinition event : events){
				MOPVariable eventMethod = new MOPVariable("Prop_" + prop.getPropertyId() + "_event_" + event.getUniqueId());
				
				propMonitor.eventMethods.put(event.getUniqueId(), eventMethod);
			}			

			propMonitors.put(prop, propMonitor);
		}

		varsToSave = new HashMap<MOPParameter, MOPVariable>();
		for (MOPParameter p : mopSpec.getVarsToSave()) {
			varsToSave.put(p, new MOPVariable("Ref_" + p.getName()));
		}

		if (this.isDefined && mopSpec.isGeneral()) {
			if (mopSpec.isFullBinding() || mopSpec.isConnected())
				monitorInfo = new MonitorInfo(mopSpec);
		}
		
		for (PropertyAndHandlers prop : mopSpec.getPropertiesAndHandlers()) {
			if(!existSkip){
				for (BlockStmt handler : prop.getHandlers().values()) {
					if (handler.toString().indexOf("__SKIP") != -1){
						existSkip = true;
						break;
					}
				}
			}
		}

		for (EventDefinition event : events) {
			if (event.getCondition() != null && event.getCondition().length() != 0) {
				existCondition = true;
				break;
			}
			if (event.has__SKIP()){
				existSkip = true;
				break;
			}
		}
	}

	public void setRefTrees(HashMap<String, RefTree> refTrees){
		this.refTrees = refTrees;
		
		if(monitorTermination != null)
			monitorTermination.setRefTrees(refTrees);
	}

	public MOPVariable getOutermostName() {
		return monitorName;
	}

	public Set<String> getNames() {
		Set<String> ret = new HashSet<String>();

		ret.add(monitorName.toString());
		return ret;
	}

	public Set<MOPVariable> getCategoryVars() {
		HashSet<MOPVariable> ret = new HashSet<MOPVariable>();

		for (PropertyAndHandlers prop : props) {
			ret.addAll(propMonitors.get(prop).categoryVars.values());
		}

		return ret;
	}

	public String printEventMethod(PropertyAndHandlers prop, EventDefinition event) {
		String ret = "";

		PropMonitor propMonitor = propMonitors.get(prop);
		
		String uniqueId = event.getUniqueId();
		int idnum = event.getIdNum();
		MOPJavaCode condition = new MOPJavaCode(event.getCondition(), monitorName);
		MOPJavaCode eventMonitoringCode = new MOPJavaCode(prop, prop.getEventMonitoringCode(event.getId()), monitorName);
		MOPJavaCode aftereventMonitoringCode = new MOPJavaCode(prop, prop.getAfterEventMonitoringCode(event.getId()), monitorName);
		MOPJavaCode monitoringBody = new MOPJavaCode(prop, prop.getLogicProperty("monitoring body"), monitorName);
		MOPJavaCode stackManage = new MOPJavaCode(prop, prop.getLogicProperty("stack manage"), monitorName);
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
				categoryConditions.put(handlerName, new MOPJavaCodeNoNewLine(prop, conditionStr, monitorName));
			}
		}

		if(prop == props.get(props.size() - 1)){
			if (event.getAction() != null && event.getAction().getStmts() != null && event.getAction().getStmts().size() != 0) {
				String eventActionStr = event.getAction().toString();
	
				eventActionStr = eventActionStr.replaceAll("__RESET", "this.reset()");
				eventActionStr = eventActionStr.replaceAll("__LOC", "this." + loc);
				eventActionStr = eventActionStr.replaceAll("__STATICSIG", "this." + staticsig);
				eventActionStr = eventActionStr.replaceAll("__SKIP", "this." + skipAroundAdvice + " = true");
	
				eventAction = new MOPJavaCode(eventActionStr);
			}
		}

		ret += "public final void " + propMonitor.eventMethods.get(uniqueId) + "(" + event.getMOPParameters().parameterDeclString() + ") {\n";

		if (!condition.isEmpty()) {
			ret += "if (!(" + condition + ")) {\n";

			ret += conditionFail + " = true;\n";

			ret += "return;\n";
			ret += "}\n";
		}

		for (MOPParameter p : varsToSave.keySet()) {
			if (event.getMOPParametersOnSpec().contains(p)) {
				MOPVariable v = varsToSave.get(p);

				ret += "if(" + v + " == null){\n";
				ret += v + " = new WeakReference(" + p.getName() + ");\n";
				ret += "}\n";
			}
		}

		if (isOutermost) {
			ret += lastevent + " = " + idnum + ";\n";
		}

		if (monitorInfo != null)
			ret += monitorInfo.union(event.getMOPParametersOnSpec());

		ret += propMonitors.get(prop).localDeclaration;

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
			categoryCode += propMonitors.get(prop).categoryVars.get(entry.getKey()) + " = " + entry.getValue() + ";\n";
		}

		if (monitorInfo != null)
			ret += monitorInfo.computeCategory(categoryCode);
		else
			ret += categoryCode;

		ret += aftereventMonitoringCode;

		if (prop == props.get(props.size() - 1) && eventAction != null) {
			for (MOPParameter p : event.getUsedParametersIn(specParam)) {
				if (!event.getMOPParametersOnSpec().contains(p)) {
					MOPVariable v = this.varsToSave.get(p);

					ret += p.getType() + " " + p.getName() + " = null;\n";
					ret += "if(" + v + " != null){\n";
					ret += p.getName() + " = (" + p.getType() + ")" + v + ".get();\n";
					ret += "}\n";
				}
			}

			ret += eventAction;
		}

		ret += "}\n";

		return ret;
	}

	public String Monitoring(MOPVariable monitorVar, EventDefinition event, MOPVariable loc, MOPVariable staticsig) {
		String ret = "";
		boolean checkSkip = event.getPos().equals("around");

		if (has__LOC) {
			if(loc != null)
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

		if (this.hasThisJoinPoint){
			ret += monitorVar + "." + this.thisJoinPoint + " = " + this.thisJoinPoint + ";\n";
		}

		if (checkSkip && event.has__SKIP()) {
			ret += monitorVar + "." + skipAroundAdvice + " = false;\n";
		}
		
		for(PropertyAndHandlers prop : props){
			PropMonitor propMonitor = propMonitors.get(prop);
			
			ret += monitorVar + "." + propMonitor.eventMethods.get(event.getUniqueId()) + "(";
			ret += event.getMOPParameters().parameterString();
			ret += ");\n";

			if (event.getCondition() != null && event.getCondition().length() != 0) {
				ret += "if(" + monitorVar + "." + conditionFail + "){\n";
				ret += monitorVar + "." + conditionFail + " = false;\n";
				ret += "} else {\n";
			}
			
			if (checkSkip && event.has__SKIP()) {
				ret += skipAroundAdvice + " |= " + monitorVar + "." + skipAroundAdvice + ";\n";
			}

			for (String category : propMonitor.handlerMethods.keySet()) {
				HandlerMethod handlerMethod = propMonitor.handlerMethods.get(category);

				ret += "if(" + monitorVar + "." + propMonitor.categoryVars.get(category) + ") {\n";

				ret += monitorVar + "." + handlerMethod.getMethodName() + "(";
				ret += event.getMOPParametersOnSpec().parameterStringIn(specParam);
				ret += ");\n";

				if (checkSkip && handlerMethod.has__SKIP()) {
					ret += skipAroundAdvice + " |= " + monitorVar + "." + skipAroundAdvice + ";\n";
				}
				
				ret += "}\n";
			}

			if (event.getCondition() != null && event.getCondition().length() != 0) {
				ret += "}\n";
			}
		}
		
		if (this.hasThisJoinPoint){
			ret += monitorVar + "." + this.thisJoinPoint + " = null;\n";
		}

		return ret;
	}

	public MonitorInfo getMonitorInfo(){
		return monitorInfo;
	}
	
	public String toString() {
		String ret = "";

		ret += "class " + monitorName;
		if (isOutermost)
			ret += " extends javamoprt.MOPMonitor";
		ret += " implements Cloneable, javamoprt.MOPObject {\n";
		
		if (isOutermost && varInOutermostMonitor != null)
			ret += varInOutermostMonitor;

		// clone()
		ret += "public Object clone() {\n";
		if (Main.statistics) {
			ret += stat.incNumMonitor();
		}
		ret += "try {\n";
		ret += monitorName + " ret = (" + monitorName + ") super.clone();\n";
		if (monitorInfo != null)
			ret += monitorInfo.copy("ret", "this");
		for(PropertyAndHandlers prop : props)
			ret += propMonitors.get(prop).cloneCode;
		ret += "return ret;\n";
		ret += "}\n";
		ret += "catch (CloneNotSupportedException e) {\n";
		ret += "throw new InternalError(e.toString());\n";
		ret += "}\n";
		ret += "}\n";

		// monitor variables
		ret += monitorDeclaration + "\n";
		if (this.has__LOC)
			ret += "String " + loc + ";\n";
		if (this.has__STATICSIG)
			ret += "org.aspectj.lang.Signature " + staticsig + ";\n";
		if (this.hasThisJoinPoint)
			ret += "JoinPoint " + thisJoinPoint + " = null;\n";

		// references for saved parameters
		for (MOPVariable v : varsToSave.values()) {
			ret += "WeakReference " + v + " = null;\n";
		}

		if (existCondition) {
			ret += "boolean " + conditionFail + " = false;\n";
		}
		if (existSkip){
			ret += "boolean " + skipAroundAdvice + " = false;\n";
		}

		// state declaration
		for(PropertyAndHandlers prop : props){
			ret += propMonitors.get(prop).stateDeclaration;
		}
		ret += "\n";

		// category condition
		for(PropertyAndHandlers prop : props){
			PropMonitor propMonitor = propMonitors.get(prop);
			for (String category : propMonitor.categoryVars.keySet()) {
				ret += "boolean " + propMonitor.categoryVars.get(category) + " = false;\n";
			}
		}
		ret += "\n";

		// constructor
		ret += "public " + monitorName + " () {\n";
		for(PropertyAndHandlers prop : props){
			if (prop.getVersionedStack()) {
				MOPVariable global_depth = new MOPVariable("global_depth");
				MOPVariable version = new MOPVariable("version");
	
				ret += "int[] " + global_depth + " = (int[])(" + systemAspectName + ".t_global_depth.get());\n";
				ret += "int[] " + version + " = (int[])(" + systemAspectName + ".t_version.get());\n";
				break;
			}
		}
		for(PropertyAndHandlers prop : props){
			PropMonitor propMonitor = propMonitors.get(prop);
			ret += propMonitor.localDeclaration;
			ret += propMonitor.initilization;
			ret += "\n";
		}
		if (Main.statistics) {
			ret += stat.incNumMonitor();
		}
		ret += "}\n";
		ret += "\n";

		// events
		for(PropertyAndHandlers prop : props){
			for (EventDefinition event : this.events) {
				ret += this.printEventMethod(prop, event) + "\n";
			}
		}

		// handlers
		for(PropertyAndHandlers prop : props){
			PropMonitor propMonitor = propMonitors.get(prop);
			for (HandlerMethod handlerMethod : propMonitor.handlerMethods.values()) {
				ret += handlerMethod + "\n";
			}
		}

		// reset
		ret += "public final void reset() {\n";
		if (monitorInfo != null)
			ret += monitorInfo.initConnected();
		for(PropertyAndHandlers prop : props){
			if (prop.getVersionedStack()) {
				MOPVariable global_depth = new MOPVariable("global_depth");
				MOPVariable version = new MOPVariable("version");
	
				ret += "int[] " + global_depth + " = (int[])(" + systemAspectName + ".t_global_depth.get());\n";
				ret += "int[] " + version + " = (int[])(" + systemAspectName + ".t_version.get());\n";
			}
		}
		if (isOutermost) {
			ret += lastevent + " = -1;\n";
		}
		for(PropertyAndHandlers prop : props){
			PropMonitor propMonitor = propMonitors.get(prop);

			ret += propMonitor.localDeclaration;
			ret += propMonitor.resetCode;
			for (String category : propMonitor.categoryVars.keySet()) {
				ret += propMonitor.categoryVars.get(category) + " = false;\n";
			}
		}
		ret += "}\n";
		ret += "\n";

		// hashcode
		if(props.size() > 1){
			boolean newHashCode = false;
			for(PropertyAndHandlers prop : props){
				PropMonitor propMonitor = propMonitors.get(prop);
				if (!propMonitor.hashcodeCode.isEmpty()) {
					newHashCode = true;
					
					propMonitor.hashcodeMethod = new MOPVariable("Prop_" + prop.getPropertyId() + "_hashCode");
					
					ret += "public final int " + propMonitor.hashcodeMethod + "() {\n";
					ret += propMonitor.hashcodeCode;
					ret += "}\n";
				}
			}
			if(newHashCode){
				ret += "public final int hashCode() {\n";
				ret += "return ";
				boolean first = true;
				for(PropertyAndHandlers prop : props){
					PropMonitor propMonitor = propMonitors.get(prop);
					if(propMonitor.hashcodeMethod != null){
						if(first){
							first = false;
						} else {
							ret += "^";
						}
						
						ret += propMonitor.hashcodeMethod;
					}
				}
				ret += ";\n";
				ret += "}\n";
				ret += "\n";
			}
		} else if(props.size() == 1){
			for(PropertyAndHandlers prop : props){
				PropMonitor propMonitor = propMonitors.get(prop);
				if (!propMonitor.hashcodeCode.isEmpty()) {
					
					ret += "public final int hashCode() {\n";
					ret += propMonitor.hashcodeCode;
					ret += "}\n";
					ret += "\n";
				}
			}
		}

		// equals
		// if there are more than 1 property, there is no state collapsing.
		if(props.size() == 1){
			for(PropertyAndHandlers prop : props){
				PropMonitor propMonitor = propMonitors.get(prop);
				if (!propMonitor.equalsCode.isEmpty()) {
					ret += "public final boolean equals(Object o) {\n";
					ret += propMonitor.equalsCode;
					ret += "}\n";
					ret += "\n";
				}
			}
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
