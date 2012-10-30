package javamop.output.monitor;

import java.util.ArrayList;
import java.util.HashMap;

import javamop.MOPException;
import javamop.output.MOPVariable;
import javamop.output.OptimizedCoenableSet;
import javamop.output.combinedaspect.GlobalLock;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.PropertyAndHandlers;
import javamop.parser.ast.stmt.BlockStmt;

/***
 * 
 * Wrapper monitor class for enforcing properties
 *
 */
public class EnforceMonitor extends BaseMonitor {

	/**
	 * Deadlock handler code for enforcement monitor
	 * */
	private BlockStmt deadlockHandler = null;
	
	
	public EnforceMonitor(String name, JavaMOPSpec mopSpec,
			OptimizedCoenableSet coenableSet, boolean isOutermost)
			throws MOPException {
		super(name, mopSpec, coenableSet, isOutermost, "Enforcement");
		for (PropertyAndHandlers prop : props) {
			HashMap<String, BlockStmt> handlerBodies = prop.getHandlers();
			BlockStmt handlerBody = handlerBodies.get("deadlock");
			if (handlerBody != null) {
				//For now we assume there's only one deadlock handler.
				this.deadlockHandler = handlerBody;
				break;
			}
		}
	}
	
	/**
	 * 
	 * Print callback class declaration
	 * 
	 * */
	@Override
	public String printExtraDeclMethods() {
		String ret = "";
		
		// Callback class declaration
		ret += "static public class " + this.monitorName
				+ "DeadlockCallback implements javamoprt.MOPCallBack { \n";
		ret += "public void apply() {\n";
		if (this.deadlockHandler != null) {
			ret += this.deadlockHandler;
		}
		ret += "\n";
		ret += "}\n";
		ret += "}\n\n";
		return ret;
	}
	
	/**
	 * 
	 * notify all the other waiting threads after an event was executed
	 * 
	 * */
	@Override
	public String afterEventMethod(MOPVariable monitor, PropertyAndHandlers prop, 
			EventDefinition event, GlobalLock lock, String aspectName) {
		String ret = "";
		if (lock != null) {
			ret += lock.getName() + "_cond.signalAll();\n";
		}
		return ret;
	}

	/**
	 * 
	 * Clone the main monitor, and check whether executing current event on the cloned monitor will incur failure or not
	 * 
	 * */
	@Override
	public String beforeEventMethod(MOPVariable monitor, PropertyAndHandlers prop, 
			EventDefinition event, GlobalLock lock, String aspectName, boolean inMonitorSet) {
		
		String ret = "";
		PropMonitor propMonitor = propMonitors.get(prop);
		String uniqueId = event.getUniqueId();
		String methodName = propMonitor.eventMethods.get(uniqueId).toString();
		ArrayList<String> blockedThreads = event.getThreadBlockedVar();
		ret += "try {\n";
		if (event.getCondition() != null && event.getCondition().length() != 0) {
			ret += "boolean cloned_monitor_condition_fail = false;\n";
		}

		ret += "do {\n";
		MOPVariable clonedMonitor = new MOPVariable("clonedMonitor");
		ret += this.monitorName + " " + clonedMonitor + " = (" + this.monitorName +")" + monitor + ".clone();\n";
		
		MOPVariable enforceCategory = (MOPVariable)propMonitor.categoryVars.values().toArray()[0];
		ret += clonedMonitor + "." + methodName + "(" + event.getMOPParameters().parameterInvokeString() + ");\n";
		
		
		// Check if the condition fails, if it does, then return directly.
		if (event.getCondition() != null && event.getCondition().length() != 0) {
			ret += "if (" + clonedMonitor + "." + this.conditionFail + ") {\n";
			ret += "cloned_monitor_condition_fail = true;\n";
			ret += "break;\n";
			ret += "}\n";
		}

		ret += "if (!" + clonedMonitor + "." + enforceCategory + ") {\n";
		if (lock != null)
			ret += lock.getName() + "_cond.await();\n";
		ret += "}\n";
		ret += "else {\n";
		ret += "break;\n";	
		ret += "}\n";
		ret += "} while (true);\n\n";
		
		// If there is blocking event point cut, wait for the thread to be blocked

		if (blockedThreads != null) {
			
			if (event.getCondition() != null && event.getCondition().length() != 0) {
				ret += "if (!cloned_monitor_condition_fail){\n";
			}

			for (String var : blockedThreads) {
				
				if (!(var.startsWith("\"") && var.endsWith("\"")))
					var = monitor + "." + var;
				ret += "while (!" + aspectName + ".containsBlockedThread(" + var + ")) {\n";
				ret += "if (!" + aspectName + ".containsThread(" + var + ")) {\n";
				if (lock != null)
					ret += lock.getName() + "_cond.await();\n";
				ret += "}\n";
				if (lock != null)
					ret += lock.getName() + "_cond.await(50L, TimeUnit.MILLISECONDS);\n";
				ret += "}\n";
			}
			
			if (event.getCondition() != null && event.getCondition().length() != 0) {
				ret += "}\n";
			}
		}

		ret += "} catch (Exception e) {\n";
		ret += "e.printStackTrace();\n";
		ret += "}\n";
		return  ret;
	}
	
}
