package javamop.output.monitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javamop.MOPException;
import javamop.output.MOPJavaCode;
import javamop.output.MOPVariable;
import javamop.output.OptimizedCoenableSet;
import javamop.output.UserJavaCode;
import javamop.output.combinedaspect.GlobalLock;
import javamop.output.combinedaspect.indexingtree.reftree.RefTree;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;

public class RawMonitor extends Monitor{

	MOPVariable loc = new MOPVariable("MOP_loc");
	MOPVariable staticsig = new MOPVariable("MOP_staticsig");
	MOPVariable wrapper = new MOPVariable("wrapper");
	MOPVariable reset = new MOPVariable("reset");
	MOPVariable lastevent = new MOPVariable("MOP_lastevent");
	MOPVariable skipAroundAdvice = new MOPVariable("skipAroundAdvice");
	MOPVariable thisJoinPoint = new MOPVariable("thisJoinPoint");
	
	JavaMOPSpec mopSpec;
	List<EventDefinition> events;
	
	boolean isGeneral;
	
	UserJavaCode monitorDeclaration;

	public RawMonitor(String name, JavaMOPSpec mopSpec, OptimizedCoenableSet coenableSet, boolean isOutermost) throws MOPException {
		super(name, mopSpec, coenableSet, isOutermost);
		
		this.isDefined = true;

		this.mopSpec = mopSpec;
		this.isGeneral = mopSpec.isGeneral();

		this.monitorName = new MOPVariable(mopSpec.getName() + "RawMonitor");

		if (isOutermost) {
			varInOutermostMonitor = new VarInOutermostMonitor(name, mopSpec, mopSpec.getEvents());
			monitorTermination = new MonitorTermination(name, mopSpec, mopSpec.getEvents(), coenableSet);
		}

		monitorDeclaration = new UserJavaCode(mopSpec.getDeclarationsStr());

		events = mopSpec.getEvents();
		
		if (this.isDefined && mopSpec.isGeneral()){
			if(mopSpec.isFullBinding() || mopSpec.isConnected())
				monitorInfo = new MonitorInfo(mopSpec);
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

	public Set<String> getNames(){
		Set<String> ret = new HashSet<String>();
		
		ret.add(monitorName.toString());
		return ret;
	}
	
	public Set<MOPVariable> getCategoryVars(){
		HashSet<MOPVariable> ret = new HashSet<MOPVariable>();
		return ret;
	}

	public boolean isReturningSKIP(EventDefinition event){
		boolean isAround = event.getPos().equals("around");
		return isAround && event.has__SKIP();
	}
	
	public String doEvent(EventDefinition event){
		String ret = "";

		boolean isAround = event.getPos().equals("around");
		String uniqueId = event.getUniqueId();
		int idnum = event.getIdNum();
		MOPJavaCode condition = new MOPJavaCode(event.getCondition(), monitorName);
		MOPJavaCode eventAction = null;

		if (event.getAction() != null && event.getAction().getStmts() != null && event.getAction().getStmts().size() != 0) {
			String eventActionStr = event.getAction().toString();

			eventActionStr = eventActionStr.replaceAll("__RESET", "this.reset()");
 			eventActionStr = eventActionStr.replaceAll("__DEFAULT_MESSAGE", defaultMessage);
      //__DEFAULT_MESSAGE may contain __LOC, make sure to sub in __DEFAULT_MESSAGE first
      // -P
			eventActionStr = eventActionStr.replaceAll("__LOC", "this." + loc);
			eventActionStr = eventActionStr.replaceAll("__STATICSIG", "this." + staticsig);
			eventActionStr = eventActionStr.replaceAll("__SKIP", skipAroundAdvice + " = true");

			eventAction = new MOPJavaCode(eventActionStr);
		}

		if (isAround) {
			ret += "public final boolean event_" + uniqueId + "(" + event.getMOPParameters().parameterDeclString() + ") {\n";
		} else {
			ret += "public final void event_" + uniqueId + "(" + event.getMOPParameters().parameterDeclString() + ") {\n";
		}

		if (isAround || has__SKIP)
			ret += "boolean " + skipAroundAdvice + " = false;\n";

		if (!condition.isEmpty()) {
			ret += "if (!(" + condition + ")) {\n";
			if (isAround && has__SKIP) {
				ret += "return false;\n";
			} else {
				ret += "return;\n";
			}
			ret += "}\n";
		}

		if (isOutermost) {
			ret += lastevent + " = " + idnum + ";\n";
		}

		if(eventAction != null)
			ret += eventAction;

		if (isAround) {
			ret += "return " + skipAroundAdvice + ";\n";
		}
		
		ret += "}\n";

		return ret;
	}

	public String Monitoring(MOPVariable monitorVar, EventDefinition event, MOPVariable loc, MOPVariable staticsig, GlobalLock l) {
		String ret = "";

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

		if (event.getPos().equals("around") && event.has__SKIP()) {
			ret += skipAroundAdvice + " |= ";
		}
		ret += monitorVar + ".event_" + event.getUniqueId() + "(";
		ret += event.getMOPParameters().parameterString();
		ret += ");\n";
		
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
	
		if(varInOutermostMonitor != null)
			ret += varInOutermostMonitor;

		//clone()
		ret += "public Object clone() {\n";
		ret += "try {\n";
		ret += monitorName + " ret = (" + monitorName + ") super.clone();\n";
		if (monitorInfo != null)
			ret += monitorInfo.copy("ret", "this");
		ret += "return ret;\n";
		ret += "}\n";
		ret += "catch (CloneNotSupportedException e) {\n";
		ret += "throw new InternalError(e.toString());\n";
		ret += "}\n";
		ret += "}\n";

		ret += monitorDeclaration + "\n";
		if (this.has__LOC)
			ret += "String " + loc + ";\n";
		if (this.has__STATICSIG)
			ret += "org.aspectj.lang.Signature " + staticsig + ";\n";

		if (this.hasThisJoinPoint)
			ret += "JoinPoint " + thisJoinPoint + " = null;\n";
		
		// events
		for (EventDefinition event : this.events) {
			ret += this.doEvent(event) + "\n";
		}
		
		//reset
		ret += "public final void reset() {\n";
		if (isOutermost) {
			ret += lastevent + " = -1;\n";
		}
		ret += "}\n";
		ret += "\n";
		
		//endObject and some declarations
		if (isOutermost) {
			ret += monitorTermination + "\n";
		}
		
		if (monitorInfo != null)
			ret += monitorInfo.monitorDecl();

		ret += "}\n";
		
		return ret;
	}
}
