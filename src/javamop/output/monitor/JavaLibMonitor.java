// Handle the creation of java code to be used as a library for the base monitor
// Mostly modified from BaseMonitor.java

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

public class JavaLibMonitor extends BaseMonitor {
	private BaseMonitor basemon;

	public JavaLibMonitor( String name, JavaMOPSpec mopSpec, PropertyAndHandlers prop
			     , OptimizedCoenableSet coenableSet, boolean isOutermost, boolean doActions
			     ) throws MOPException {
		super(name, mopSpec, prop, coenableSet, isOutermost, doActions);
		basemon = new BaseMonitor(name, mopSpec, prop, coenableSet, isOutermost, doActions);
		this.monitorName = new MOPVariable(mopSpec.getName() + "JavaLibMonitor");
	}

	// Create the event-handling methods, but do not have them accept parameters. This is so as to be able
	// to use these methods to implement the JavaLibInterface in the javamoprt.
	public String doBaseEvent(EventDefinition event) {
		String ret = "";

		boolean isAround = event.getPos().equals("around");
		String uniqueId = event.getUniqueId();
		int idnum = event.getIdNum();
		MOPJavaCode condition = new MOPJavaCode(event.getCondition(), monitorName);
		MOPJavaCode eventMonitoringCode = new MOPJavaCode(prop.getEventMonitoringCode(event.getId()), monitorName);
		MOPJavaCode monitoringBody = new MOPJavaCode(prop.getLogicProperty("monitoring body"), monitorName);
		HashMap<String, MOPJavaCode> categoryConditions = new HashMap<String, MOPJavaCode>();
		MOPJavaCode eventAction = null;

		for (String handlerName : prop.getHandlers().keySet()) {
			String conditionStr = prop.getLogicProperty(handlerName + " condition");
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

		// The parameter is commented out so as to be able to implement a more general interface
		if (isAround && event.has__SKIP()) {
			ret += "public final boolean event_" + uniqueId + "(" /* + event.getMOPParameters().parameterDeclString()*/ + ") {\n";
		} else {
			ret += "public final void event_" + uniqueId + "(" /* + event.getMOPParameters().parameterDeclString()*/ + ") {\n";
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

		// Commented out so as to not actually take any actions on the event
		// if (eventAction != null)
		//   ret += "// eventAction\n";
		//   ret += eventAction;

		if (isAround && event.has__SKIP()) {
			ret += "return " + skipAroundAdvice + ";\n";
		}

		ret += "}\n";

		return ret;
	}

	@Override
	public String toString() {
		boolean hasFail = false;
		boolean hasMatch = false;
		String ret = basemon.toString();

		ret += "public class " + monitorName;
		if (isOutermost)
			ret += " extends " + basemon.monitorName;
		ret += " implements JavaLibInterface, javamoprt.MOPObject {\n";

		// category condition
		for (String category : categoryVars.keySet()) {
			ret += "boolean " + categoryVars.get(category) + " = false;\n";
			if (category.equals("fail")) hasFail = true;
			if (category.equals("match")) hasMatch = true;
		}
		ret += "\n";

		// constructor
		ret += "public " + monitorName + " () {\n";
		ret += "super();\n";
		ret += "}\n";
		ret += "\n";

		// events
		for (EventDefinition event : this.events) {
			ret += "// event: " + event.getId() + "\n";
			ret += this.doBaseEvent(event) + "\n";
		}

		ret += "@Override\n";
		ret += "public boolean isCoreachable() {\n";
		// todo: check to make sure this is correct for coreachable
		ret += "return " + (hasFail ? "!Category_fail" : "true") + ";\n";
		ret += "}\n\n";

		ret += "@Override\n";
		ret += "public void process(String s) {\n";
		for (EventDefinition event : this.events) {
			String e = event.getId();
			ret += "if (s.equals(\"" + e + "\")) { event_" + e + "(); return; }\n";
		}
		ret += "return;\n";
		ret += "}\n\n";

		ret += "@Override\n";
		ret += "public Category getCategory() {\n";
		if (hasMatch) ret += "if (Category_match) return Category.Match;\n";
		if (hasFail) ret += "if (Category_fail) return Category.Fail;\n";
		ret += "return Category.Unknown;\n";
		ret += "}\n\n";

		ret += "}\n";

		return ret;
	}
}
