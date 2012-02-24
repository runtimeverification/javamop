package javamop.output.monitor;

import java.util.HashMap;

import javamop.Main;
import javamop.output.MOPJavaCode;
import javamop.output.MOPVariable;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;
import javamop.parser.ast.mopspec.PropertyAndHandlers;
import javamop.parser.ast.stmt.BlockStmt;

public class HandlerMethod {
	PropertyAndHandlers prop;
	MOPVariable methodName;
	MOPJavaCode handlerCode = null;
	MOPParameters specParam;
	MOPVariable categoryVar;
	String category;
	Monitor monitor;

	MOPParameters varsToRestore;
	HashMap<MOPParameter, MOPVariable> savedParams;

	// local variables for now
	MOPVariable loc = new MOPVariable("MOP_loc");
	MOPVariable staticsig = new MOPVariable("MOP_staticsig");
	MOPVariable skipAroundAdvice = new MOPVariable("MOP_skipAroundAdvice");

	private boolean has__SKIP = false;

	public HandlerMethod(PropertyAndHandlers prop, String category, MOPParameters specParam, MOPParameters commonParamInEvents,
			HashMap<MOPParameter, MOPVariable> savedParams, BlockStmt body, MOPVariable categoryVar, Monitor monitor) {
		this.prop = prop;
		this.category = category;
		this.methodName = new MOPVariable("Prop_" + prop.getPropertyId() + "_handler_" + category);
		if(body != null){
			String handlerBody = body.toString();

			if (handlerBody.indexOf("__SKIP") != -1) {
				has__SKIP = true;
			}

			handlerBody = handlerBody.replaceAll("__RESET", "this.reset()");
			handlerBody = handlerBody.replaceAll("__LOC", "this." + loc);
			handlerBody = handlerBody.replaceAll("__STATICSIG", "this." + staticsig);
			handlerBody = handlerBody.replaceAll("__SKIP", skipAroundAdvice + " = true");
			
			this.handlerCode = new MOPJavaCode(handlerBody);
		}
		this.specParam = specParam;
		this.categoryVar = categoryVar;
		this.monitor = monitor;
		this.varsToRestore = new MOPParameters();
		this.savedParams = savedParams;

		for (MOPParameter p : prop.getUsedParametersIn(category, specParam)) {
			if (!commonParamInEvents.contains(p)) {
				this.varsToRestore.add(p);
			}
		}
	}

	public boolean has__SKIP() {
		return has__SKIP;
	}

	public MOPVariable getMethodName() {
		return methodName;
	}

	public String toString() {
		String ret = "";

		ret += "public final ";

		// if we want a handler to return some value, change it.
		ret += "void ";

		ret += methodName + " (" + this.specParam.parameterDeclString() + "){\n";

		if (Main.statistics) {
			ret += "if(" + categoryVar + ") {\n";
			ret += monitor.stat.categoryInc(prop, category);
			ret += "}\n";
		}

		for (MOPParameter p : this.varsToRestore) {
			MOPVariable v = this.savedParams.get(p);

			ret += "if(" + p.getName() + " == null && " + v + " != null){\n";
			ret += p.getName() + " = (" + p.getType() + ")" + v + ".get();\n";
			ret += "}\n";
		}

		ret += handlerCode + "\n";

		ret += "}\n";

		return ret;
	}

}
