package javamop.output.monitor;

import java.util.HashMap;

import javamop.Main;
import javamop.output.MOPVariable;
import javamop.parser.ast.mopspec.MOPParameter;
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

	MOPParameters varsToRestore;
	HashMap<MOPParameter, MOPVariable> savedParams;

	public HandlerMethod(PropertyAndHandlers prop, String category, MOPParameters specParam, MOPParameters commonParamInEvents,
			HashMap<MOPParameter, MOPVariable> savedParams, BlockStmt body, MOPVariable categoryVar, Monitor monitor) {
		this.prop = prop;
		this.category = category;
		this.methodName = new MOPVariable("Prop_" + prop.getPropertyId() + "_handler_" + category);
		this.body = body;
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

		// local variable for now
		MOPVariable loc = new MOPVariable("MOP_loc");
		MOPVariable skipAroundAdvice = new MOPVariable("MOP_skipAroundAdvice");

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

		handlerBody = handlerBody.replaceAll("__RESET", "this.reset()");
		handlerBody = handlerBody.replaceAll("__LOC", "this." + loc);
		handlerBody = handlerBody.replaceAll("__SKIP", skipAroundAdvice + " = true");

		ret += handlerBody + "\n";

		ret += "}\n";

		return ret;
	}

}
