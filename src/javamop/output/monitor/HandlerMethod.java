package javamop.output.monitor;

import javamop.Main;
import javamop.output.MOPVariable;
import javamop.parser.ast.mopspec.MOPParameters;
import javamop.parser.ast.mopspec.PropertyAndHandlers;
import javamop.parser.ast.stmt.BlockStmt;

public class HandlerMethod {
	PropertyAndHandlers prop;
	MOPVariable methodName;
	BlockStmt body;
	MOPParameters specParam;
	MOPVariable categoryVar;
	String category;
	Monitor monitor;

	public HandlerMethod(PropertyAndHandlers prop, String category, MOPParameters specParam, BlockStmt body, MOPVariable categoryVar, Monitor monitor) {
		this.prop = prop;
		this.category = category;
		this.methodName = new MOPVariable("handler_" + category);
		this.body = body;
		this.specParam = specParam;
		this.categoryVar = categoryVar;
		this.monitor = monitor;
	}

	private Boolean cachedHas__SKIP = null;

	public boolean has__SKIP() {
		if (cachedHas__SKIP != null)
			return cachedHas__SKIP.booleanValue();

		if (body != null) {
			String handlerBody = body.toString();
			if (handlerBody.indexOf("__SKIP") != -1) {
				cachedHas__SKIP = new Boolean(true);
				return true;
			}
		}
		cachedHas__SKIP = new Boolean(false);
		return false;
	}

	public MOPVariable getMethodName() {
		return methodName;
	}

	public String toString() {
		String ret = "";
		String handlerBody = body.toString();
		MOPVariable thisJoinPoint = new MOPVariable("MOP_thisJoinPoint");
		MOPVariable skipAroundAdvice = new MOPVariable("skipAroundAdvice"); // local variable for now
		
		ret += "public final ";
		
		// if we want a handler to return some value, change it.
		if (has__SKIP()) {
			ret += "boolean ";
		} else {
			ret += "void ";
		}
		
		ret += methodName + " (" + this.specParam.parameterDeclString() + "){\n";
		
		if (has__SKIP()) {
			ret += "boolean " + skipAroundAdvice + " = false;\n";
		}		

		if (Main.statistics) {
			ret += "if(" + categoryVar + ") {\n";
			ret += monitor.stat.categoryInc(prop, category);
			ret += "}\n";
		}

		handlerBody = handlerBody.replaceAll("__RESET", "this.reset()");
		handlerBody = handlerBody.replaceAll("__LOC", thisJoinPoint + ".getSourceLocation().toString()");
		handlerBody = handlerBody.replaceAll("__SKIP", skipAroundAdvice + " = true");

		ret += handlerBody + "\n";
		
		if (has__SKIP()) {
			ret += "return " + skipAroundAdvice + ";\n";
		}		
		
		ret += "}\n";

		return ret;
	}

}
