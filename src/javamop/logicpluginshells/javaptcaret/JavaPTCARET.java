package javamop.logicpluginshells.javaptcaret;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import javamop.MOPException;
import javamop.logicpluginshells.LogicPluginShell;
import javamop.logicpluginshells.LogicPluginShellResult;
import javamop.logicpluginshells.javaptcaret.ast.PseudoCode;
import javamop.logicpluginshells.javaptcaret.ast.PseudoCode_Expr;
import javamop.logicpluginshells.javaptcaret.ast.PseudoCode_NotExpr;
import javamop.logicpluginshells.javaptcaret.parser.PTCARET_PseudoCode_Parser;
import javamop.logicpluginshells.javaptcaret.visitor.EvalVisitor;
import javamop.logicpluginshells.javaptcaret.visitor.JavaCodeGenVisitor;
import javamop.logicpluginshells.javaptcaret.visitor.MaxAlphaVisitor;
import javamop.logicpluginshells.javaptcaret.visitor.MaxBetaVisitor;
import javamop.parser.logicrepositorysyntax.LogicRepositoryType;

public class JavaPTCARET extends LogicPluginShell {
	public JavaPTCARET() {
		super();
		monitorType = "PTCARET PSEUDO-CODE";
	}

	ArrayList<String> allEvents;

	private ArrayList<String> getEvents(String eventStr) throws MOPException {
		ArrayList<String> events = new ArrayList<String>();

		for (String event : eventStr.trim().split(" ")) {
			if (event.trim().length() != 0)
				events.add(event.trim());
		}

		return events;
	}

	private Properties getMonitorCode(LogicRepositoryType logicOutput) throws MOPException {
		Properties result = new Properties();

		String monitor = logicOutput.getProperty().getFormula();

		PseudoCode code = null;
		try {
			code = PTCARET_PseudoCode_Parser.parse(monitor);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new MOPException("PTCaRet to Java Plugin cannot parse PTCaRet formula");
		}

		if (code == null)
			throw new MOPException("PTCaRet to Java Plugin cannot parse PTCaRet formula");

		/*
		 * State Declaration in Monitor and Monitor Set
		 */
		Integer alpha_size = code.accept(new MaxAlphaVisitor(), null);
		Integer beta_size = code.accept(new MaxBetaVisitor(), null);
		String stateDecl = "";
		String reset = "";

		// I think the framework should take care of it.
		if (beta_size > 0) {
			stateDecl += "javamoprt.MOPVersionedBooleanArrayStack $stack$;\n";
			stateDecl += "int $current_depth$ = 0;\n";
			stateDecl += "int $start_depth$ = 0;\n";
		}
		if (alpha_size > 0) {
			stateDecl += "boolean[] $alpha$;\n";
			reset += "$alpha$ = new boolean[" + alpha_size + "];\n";
		}
		if (beta_size > 0) {
			stateDecl += "boolean[] $beta$;\n";
			reset += "$beta$ = new boolean[" + beta_size + "];\n";
		}
		result.put("state declaration", stateDecl);
		result.put("state declaration for set", "");

		if (beta_size > 0) {
			reset += "$current_depth$ = $global_depth$[0];\n";
			reset += "$start_depth$ = $global_depth$[0];\n";
			reset += "$stack$ = new javamoprt.MOPVersionedBooleanArrayStack();\n";
			reset += "$stack$.push($beta$, $version$[$current_depth$]);\n";
		}
		result.put("reset", reset);
		result.put("initialization", reset);

		if (beta_size > 0) {
			String stackManage = "";

			stackManage += "if($global_depth$[0] < $start_depth$){\n";
			stackManage += "$beta$ = new boolean[" + beta_size + "];\n";
			stackManage += "$current_depth$ = $global_depth$[0];\n";
			stackManage += "$start_depth$ = $global_depth$[0];\n";
			stackManage += "$stack$ = new javamoprt.MOPVersionedBooleanArrayStack();\n";
			stackManage += "$stack$.push($beta$, $version$[$current_depth$]);\n";
			stackManage += "}\n";
			stackManage += "\n";
			stackManage += "while($current_depth$ > $start_depth$){\n";
			stackManage += "if($version$[$current_depth$] != $stack$.peek_version()){\n";
			stackManage += "boolean[] $temp_beta$ = (boolean[])($stack$.popAndNext());\n";
			stackManage += "$current_depth$--;\n";
			stackManage += "if($temp_beta$ != null){\n";
			stackManage += "$beta$ = $temp_beta$;\n";
			stackManage += "continue;\n";
			stackManage += "} else {\n";
			stackManage += "$beta$ = new boolean[" + beta_size + "];\n";
			stackManage += "$stack$.push($beta$, $version$[$current_depth$]);\n";
			stackManage += "continue;\n";
			stackManage += "}\n";
			stackManage += "} else {\n";
			stackManage += "break;\n";
			stackManage += "}\n";
			stackManage += "}\n";
			stackManage += "\n";
			stackManage += "while($current_depth$ < $global_depth$[0]){\n";
			stackManage += "$current_depth$++;\n";
			stackManage += "$beta$ = Arrays.copyOf($beta$, " + beta_size + ");\n";
			stackManage += "$stack$.push($beta$, $version$[$current_depth$]);\n";
			stackManage += "}\n";
			stackManage += "\n";

			result.put("stack manage", stackManage);
		}

		/*
		 * Codes for Each Event
		 */
		String monitoredEventsStr = "";
		String aftermonitoredEventsStr = "";
		String validationCondStr = "";
		String violationCondStr = "";
		for (String event : allEvents) {
			PseudoCode evaluated = (PseudoCode) code.accept(new EvalVisitor(), event);

			/*
			 * Monitoring Code for the event
			 */
			monitoredEventsStr += event + ":{\n " + evaluated.getBefore().accept(new JavaCodeGenVisitor(), null) + "\n}\n\n";
			aftermonitoredEventsStr += event + ":{\n " + evaluated.getAfter().accept(new JavaCodeGenVisitor(), null) + "\n}\n\n";

			/*
			 * Validation condition for the event
			 */
			validationCondStr += event + ":{\n";
			validationCondStr += evaluated.getOutput().getExpr().accept(new JavaCodeGenVisitor(), null);
			validationCondStr += "\n}\n";

			/*
			 * Violation condition for the event
			 */
			PseudoCode_Expr violationCond = new PseudoCode_NotExpr(evaluated.getOutput().getExpr());
			violationCond = (PseudoCode_Expr) violationCond.accept(new EvalVisitor(), event);
			violationCondStr += event + ":{\n";
			violationCondStr += violationCond.accept(new JavaCodeGenVisitor(), null);
			violationCondStr += "\n}\n";

		}
		result.put("monitored events", monitoredEventsStr);
		result.put("after monitored events", aftermonitoredEventsStr);
		result.put("validation condition", validationCondStr);
		result.put("violation condition", violationCondStr);

		result.put("monitoring body", "");

		String cloneCode = "";
		if (beta_size > 0) {
			cloneCode += "$ret$.$stack$ = this.$stack$.fclone();\n";
		}
		
		// alpha
		if (alpha_size > 0) {
			cloneCode += "$ret$.$alpha$ = this.$alpha$.clone();\n";
		}
		// beta1
		if (beta_size > 0) {
			cloneCode += "$ret$.$beta$ = $ret$.$stack$.peek();\n";
		}
		result.put("clone", cloneCode);

		return result;
	}

	public LogicPluginShellResult process(LogicRepositoryType logicOutputXML, String events) throws MOPException {
		if (logicOutputXML.getProperty().getLogic().toLowerCase().compareTo(monitorType.toLowerCase()) != 0)
			throw new MOPException("Wrong type of monitor is given to PTCaRet Monitor.");
		allEvents = getEvents(events);

		LogicPluginShellResult logicShellResult = new LogicPluginShellResult();
		// logicShellResult.startEvents =
		// getEvents(logicOutputXML.getCreationEvents());
		logicShellResult.startEvents = allEvents;
		logicShellResult.properties = getMonitorCode(logicOutputXML);
		logicShellResult.properties = addEnableSets(logicShellResult.properties, logicOutputXML);

		return logicShellResult;
	}
}
