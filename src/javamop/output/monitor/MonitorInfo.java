package javamop.output.monitor;

import javamop.output.MOPVariable;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;

public class MonitorInfo {
	MOPParameters parameters;
	MOPVariable monitorInfo = new MOPVariable("monitorInfo");

	boolean isFullBinding;
	boolean isConnected;

	public MonitorInfo(JavaMOPSpec mopSpec) {
		this.parameters = mopSpec.getParameters();
		this.isFullBinding = mopSpec.isFullBinding();
		this.isConnected = mopSpec.isConnected();
	}

	public String newInfo(MOPVariable monitorVar, MOPParameters vars) {
		return newInfo(monitorVar.toString(), vars);
	}

	public String newInfo(String monitorVar, MOPParameters vars) {
		String ret = "";

		ret += monitorVar + "." + monitorInfo + " = " + "new javamoprt.MOPMonitorInfo();\n";

		if (isFullBinding) {
			if (vars.size() == parameters.size())
				ret += monitorVar + "." + monitorInfo + ".isFullParam" + " = " + "true;\n";
			else
				ret += monitorVar + "." + monitorInfo + ".isFullParam" + " = " + "false;\n";
		}

		if (isConnected) {
			ret += monitorVar + "." + monitorInfo + ".connected" + " = " + "new int[" + parameters.size() + "];\n";

			for (int i = 0; i < parameters.size(); i++) {
				MOPParameter p = parameters.get(i);
				if (vars.contains(p))
					ret += monitorVar + "." + monitorInfo + ".connected" + "[" + i + "] = -1;\n";
				else
					ret += monitorVar + "." + monitorInfo + ".connected" + "[" + i + "] = -2;\n";
			}
		}

		return ret;
	}

	public String initConnected() {
		String ret = "";

		if (isConnected) {
			for (int i = 0; i < parameters.size(); i++) {
				ret += "this" + "." + monitorInfo + ".connected" + "[" + i + "]" + " = ";
				ret += "(" + "this" + "." + monitorInfo + ".connected" + "[" + i + "]" + " == " + "-2" + ")" + "?";
				ret += "-2" + ":" + "-1";
				ret += ";\n";
			}
		}

		return ret;
	}

	public String copy(MOPVariable monitorVar) {
		String ret = "";

		ret += monitorVar + "." + monitorInfo + " = " + "(javamoprt.MOPMonitorInfo)" + "this." + monitorInfo + ".clone();\n";

		return ret;
	}

	public String copy(MOPVariable targetVar, MOPVariable origVar) {
		return copy(targetVar.toString(), origVar.toString());
	}

	public String copy(String targetVar, String origVar) {
		String ret = "";

		ret += targetVar + "." + monitorInfo + " = " + "(javamoprt.MOPMonitorInfo)" + origVar + "." + monitorInfo + ".clone();\n";

		return ret;
	}

	public String expand(String monitorVar, SuffixMonitor suffixMonitor, MOPParameters vars) {
		String ret = "";
		MOPVariable monitor = new MOPVariable("monitor");
		MOPVariable monitorList = new MOPVariable("monitorList");

		boolean isFullParam = vars.size() == parameters.size();

		if (isFullBinding)
			ret += monitorVar + "." + monitorInfo + ".isFullParam" + " = " + isFullParam + ";\n";
		if (isConnected) {
			for (int i = 0; i < parameters.size(); i++) {
				MOPParameter p = parameters.get(i);
				if (vars.contains(p)) {
					ret += monitorVar + "." + monitorInfo + ".connected" + "[" + i + "]" + " = ";
					ret += "(" + monitorVar + "." + monitorInfo + ".connected" + "[" + i + "]" + " == " + "-2" + ")" + "?";
					ret += "-1" + ":";
					ret += monitorVar + "." + monitorInfo + ".connected" + "[" + i + "]";
					ret += ";\n";
				}
			}
		}

		if (suffixMonitor.isDefined) {
			Monitor innerMonitor = suffixMonitor.innerMonitor;

			ret += "for(" + innerMonitor.getOutermostName() + " " + monitor + " : " + monitorVar + "." + monitorList + "){\n";
			if (isFullBinding)
				ret += monitor + "." + monitorInfo + ".isFullParam" + " = " + isFullParam + ";\n";
			if (isConnected) {
				for (int i = 0; i < parameters.size(); i++) {
					MOPParameter p = parameters.get(i);
					if (vars.contains(p)) {
						ret += monitor + "." + monitorInfo + ".connected" + "[" + i + "]" + " = ";
						ret += "(" + monitor + "." + monitorInfo + ".connected" + "[" + i + "]" + " == " + "-2" + ")" + "?";
						ret += "-1" + ":";
						ret += monitor + "." + monitorInfo + ".connected" + "[" + i + "]";
						ret += ";\n";
					}
				}
			}

			ret += "}\n";
		}

		return ret;
	}

	MOPVariable MOPNum1 = new MOPVariable("MOPNum1");
	MOPVariable MOPNum2 = new MOPVariable("MOPNum2");
	MOPVariable MOPNum3 = new MOPVariable("MOPNum3");
	MOPVariable MOPNumTemp = new MOPVariable("MOPNumTemp");

	public String union(MOPParameters params) {
		String ret = "";
		
		if (isConnected) {
			if (params.size() > 1) {
				ret += "int " + MOPNum1 + ";\n";
				ret += "int " + MOPNum2 + ";\n";
				ret += "int " + MOPNum3 + ";\n";
				ret += "int " + MOPNumTemp + ";\n";
				ret += "\n";
				
				MOPParameter p = params.get(0);
				for (int i = 1; i < params.size(); i++)
					ret += union(p, params.get(i));
			}
		}

		return ret;
	}

	protected String union(MOPParameter p1, MOPParameter p2) {
		return union(findNum(p1), findNum(p2));
	}

	protected String union(int i1, int i2) {
		String ret = "";

		if (i1 < 0 || i2 < 0)
			return ret;

		ret += MOPNum1 + " = " + i1 + ";\n";
		ret += "while(" + monitorInfo + ".connected[" + MOPNum1 + "]" + " >= 0){\n";
		ret += MOPNum1 + " = " + monitorInfo + ".connected[" + MOPNum1 + "]" + ";\n";
		ret += "}\n";

		ret += MOPNum3 + " = " + i1 + ";\n";
		ret += "while(" + monitorInfo + ".connected[" + MOPNum3 + "]" + " >= 0){\n";
		ret += MOPNumTemp + " = " + MOPNum3 + ";\n";
		ret += MOPNum3 + " = " + monitorInfo + ".connected[" + MOPNum3 + "]" + ";\n";
		ret += monitorInfo + ".connected[" + MOPNumTemp + "]" + " = " + MOPNum1 + ";\n";
		ret += "}\n";
		
		ret += MOPNum2 + " = " + i2 + ";\n";
		ret += "while(" + monitorInfo + ".connected[" + MOPNum2 + "]" + " >= 0){\n";
		ret += MOPNum2 + " = " + monitorInfo + ".connected[" + MOPNum2 + "]" + ";\n";
		ret += "}\n";

		ret += MOPNum3 + " = " + i2 + ";\n";
		ret += "while(" + monitorInfo + ".connected[" + MOPNum3 + "]" + " >= 0){\n";
		ret += MOPNumTemp + " = " + MOPNum3 + ";\n";
		ret += MOPNum3 + " = " + monitorInfo + ".connected[" + MOPNum3 + "]" + ";\n";
		ret += monitorInfo + ".connected[" + MOPNumTemp + "]" + " = " + MOPNum2 + ";\n";
		ret += "}\n";

		ret += "if(" + MOPNum1 + " < " + MOPNum2 + "){\n";
		ret += monitorInfo + ".connected[" + MOPNum2 + "]" + " = " + MOPNum1 + ";\n";
		ret += "} else if (" + MOPNum1 + " > " + MOPNum2 + "){\n";
		ret += monitorInfo + ".connected[" + MOPNum1 + "]" + " = " + MOPNum2 + ";\n";
		ret += "}\n";
		
		return ret;
	}

	protected int findNum(MOPParameter p) {
		for (int i = 0; i < parameters.size(); i++) {
			MOPParameter param = parameters.get(i);

			if (param.getName().equals(p.getName()))
				return i;
		}
		return -1;
	}

	public String computeCategory(String statements) {
		String ret = "";
		MOPVariable MOPFlag = new MOPVariable("MOPFlag");
		MOPVariable i = new MOPVariable("i");
		
		if (statements == null || statements.length() == 0)
			return ret;

		if (isFullBinding) {
			ret += "if(" + "this." + monitorInfo + ".isFullParam" + "){\n";
		}

		if (isConnected){
			ret += "boolean " + MOPFlag + " = false;\n";
			
			ret += "for(int " + i + " = 0; " + i + " < " + parameters.size() + "; " + i + "++){\n";
			ret += "if(" + monitorInfo + ".connected[" + i + "] == -1){\n";
			ret += MOPFlag + " = true;\n";
			ret += "}\n";
			ret += "}\n";
			
			ret += "if(" + "!" + MOPFlag + "){\n";
		}
		
		ret += statements;

		if (isConnected){
			ret += "}\n";
		}
		
		if (isFullBinding) {
			ret += "}\n";
		}

		// do something for connected

		return ret;
	}

	public String monitorDecl() {
		String ret = "";

		ret += "javamoprt.MOPMonitorInfo " + monitorInfo + ";\n";

		return ret;
	}
}
