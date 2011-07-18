package javamop.output.combinedaspect;

import java.util.HashMap;

import javamop.Main;
import javamop.output.MOPVariable;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.PropertyAndHandlers;

public class MOPStatistics {
	String aspectName;

	MOPVariable numMonitor;
	MOPVariable collectedMonitor;
	MOPVariable terminatedMonitor;
	HashMap<String, MOPVariable> eventVars = new HashMap<String, MOPVariable>();
	HashMap<PropertyAndHandlers, HashMap<String, MOPVariable>> categoryVars = new HashMap<PropertyAndHandlers, HashMap<String, MOPVariable>>();
	HashMap<MOPParameter, MOPVariable> paramVars = new HashMap<MOPParameter, MOPVariable>();

	String specName;
	
	public MOPStatistics(String name, JavaMOPSpec mopSpec) {
		this.aspectName = name + "MonitorAspect";
		this.specName = mopSpec.getName();
		this.numMonitor = new MOPVariable(mopSpec.getName() + "_Monitor_num");
		this.collectedMonitor = new MOPVariable(mopSpec.getName() + "_CollectedMonitor_num");
		this.terminatedMonitor = new MOPVariable(mopSpec.getName() + "_TerminatedMonitor_num");

		for (EventDefinition event : mopSpec.getEvents()) {
			MOPVariable eventVar = new MOPVariable(mopSpec.getName() + "_" + event.getId() + "_num");
			this.eventVars.put(event.getId(), eventVar);
		}

		for (PropertyAndHandlers prop : mopSpec.getPropertiesAndHandlers()) {
			HashMap<String, MOPVariable> categoryVarsforProp = new HashMap<String, MOPVariable>();
			for (String key : prop.getHandlers().keySet()) {
				MOPVariable categoryVar = new MOPVariable(mopSpec.getName() + "_" + prop.getPropertyId() + "_" + key + "_num");
				categoryVarsforProp.put(key, categoryVar);
			}
			this.categoryVars.put(prop, categoryVarsforProp);
		}

		for (MOPParameter param : mopSpec.getParameters()) {
			MOPVariable paramVar = new MOPVariable(mopSpec.getName() + "_" + param.getName() + "_set");
			this.paramVars.put(param, paramVar);
		}
	}

	public String fieldDecl() {
		String ret = "";
		if (!Main.statistics)
			return ret;

		ret += "static long " + numMonitor + " = 0;\n";
		ret += "static long " + collectedMonitor + " = 0;\n";
		ret += "static long " + terminatedMonitor + " = 0;\n";

		for (MOPVariable eventVar : eventVars.values()) {
			ret += "static long " + eventVar + " = 0;\n";
		}

		for (HashMap<String, MOPVariable> categoryVarsforProp : categoryVars.values()) {
			for (MOPVariable categoryVar : categoryVarsforProp.values()) {
				ret += "static long " + categoryVar + " = 0;\n";
			}
		}

		/* removed for buggy behavior */
		// for(MOPVariable paramVar : paramVars.values()){
		// ret += "static HashSet " + paramVar + " = new HashSet();\n";
		// }

		return ret;
	}

	public String paramInc(MOPParameter param) {
		String ret = "";
		if (!Main.statistics)
			return ret;

		/* removed for buggy behavior */
		// MOPVariable paramVar = null;
		//		
		// for(MOPParameter p : paramVars.keySet()){
		// if(p.getName().equals(param.getName()))
		// paramVar = paramVars.get(p);
		// }
		//		
		// if(paramVar != null)
		// ret += paramVar + ".add(" + param.getName() + ");\n";

		return ret;
	}

	public String eventInc(String eventName) {
		String ret = "";
		if (!Main.statistics)
			return ret;

		MOPVariable eventVar = eventVars.get(eventName);

		ret += eventVar + "++;\n";

		return ret;
	}

	public String categoryInc(PropertyAndHandlers prop, String category) {
		String ret = "";
		if (!Main.statistics)
			return ret;

		MOPVariable categoryVar = categoryVars.get(prop).get(category);

		ret += aspectName + "." + categoryVar + "++;\n";

		return ret;
	}

	public String incNumMonitor() {
		String ret = "";
		if (!Main.statistics)
			return ret;

		ret += aspectName + "." + numMonitor + "++;\n";

		return ret;
	}

	public String incCollectedMonitor() {
		String ret = "";
		if (!Main.statistics)
			return ret;

		ret += aspectName + "." + collectedMonitor + "++;\n";

		return ret;
	}

	public String incTerminatedMonitor() {
		String ret = "";
		if (!Main.statistics)
			return ret;

		ret += aspectName + "." + terminatedMonitor + "++;\n";

		return ret;
	}

	public String advice() {
		String ret = "";
		if (!Main.statistics)
			return ret;

		ret += "after () : execution(* *.main(..)) {\n";

		ret += "System.err.println(\"== " + this.specName + " ==\");\n";
		ret += "System.err.println(\"#monitors: \" + " + numMonitor + ");\n";

		for (String eventName : eventVars.keySet()) {
			MOPVariable eventVar = eventVars.get(eventName);
			ret += "System.err.println(\"#event - " + eventName + ": \" + " + eventVar + ");\n";
		}

		for (PropertyAndHandlers prop : categoryVars.keySet()) {
			HashMap<String, MOPVariable> categoryVarsforProp = categoryVars.get(prop);
			for (String categoryName : categoryVarsforProp.keySet()) {
				MOPVariable categoryVar = categoryVarsforProp.get(categoryName);
				ret += "System.err.println(\"#category - prop " + prop.getPropertyId() + " - " + categoryName + ": \" + " + categoryVar + ");\n";
			}
		}

		// for(MOPParameter param : paramVars.keySet()){
		// MOPVariable paramVar = paramVars.get(param);
		// ret += "System.err.println(\"#parameter - " + param.getName() +
		// ": \" + " + paramVar + ".size()" + ");\n";
		// }

		ret += "}\n";

		return ret;
	}

}
