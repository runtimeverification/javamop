package javamop.output.combinedaspect.indexingtree;

import java.util.HashMap;

import javamop.output.MOPVariable;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;

public class IndexingCache{
	MOPVariable name;
	MOPParameters param;
	MOPParameters fullParam;

	MOPVariable value;
	HashMap<String, MOPVariable> keys = new HashMap<String, MOPVariable>();
	
	boolean perthread = false;
	
	public IndexingCache(MOPVariable name, MOPParameters param, MOPParameters fullParam, boolean perthread) {
		this.name = name;
		this.param = param;
		this.fullParam = fullParam;
		this.perthread = perthread;

		this.value = new MOPVariable(name + "_cachevalue");
		for (int i = 0; i < param.size(); i++) {
			this.keys.put(param.get(i).getName(), new MOPVariable(name + "_cachekey_" + fullParam.getIdnum(param.get(i))));
		}
	}

	public String getCacheValue(MOPVariable obj) {
		String ret = "";

		if(perthread){
			ret += "if(";
			for (int i = 0; i < param.size(); i++) {
				if (i > 0)
					ret += " && ";
				ret += param.get(i).getName() + " == " + keys.get(param.get(i).getName()) + ".get()";
			}
			ret += "){\n";
			ret += obj + " = " + value + ".get();\n";
			ret += "}\n";
		} else {
			ret += "if(";
			for (int i = 0; i < param.size(); i++) {
				if (i > 0)
					ret += " && ";
				ret += param.get(i).getName() + " == " + keys.get(param.get(i).getName());
			}
			ret += "){\n";
			ret += obj + " = " + value + ";\n";
			ret += "}\n";
		}

		return ret;
	}

	public String setCacheKeys() {
		String ret = "";

		for (MOPParameter p : param) {
			if(perthread){
				ret += keys.get(p.getName()) + ".set(" + p.getName() + ");\n";
			} else {
				ret += keys.get(p.getName()) + " = " + p.getName() + ";\n";
			}
		}

		return ret;
	}

	public String setCacheValue(MOPVariable monitor) {
		String ret = "";

		if(perthread){
			ret += value + ".set(" + monitor + ");\n";
		} else {
			ret += value + " = " + monitor + ";\n";
		}

		return ret;
	}

	public String toString() {
		String ret = "";

		if(perthread){
			for (MOPVariable key : keys.values()) {
				ret += "static final ThreadLocal " + key + " = new ThreadLocal() {\n";
				ret += "protected Object initialValue(){\n";
				ret += "return null;\n";
				ret += "}\n";
				ret += "};\n";
			}
			ret += "static final ThreadLocal " + value + " = new ThreadLocal() {\n";
			ret += "protected Object initialValue(){\n";
			ret += "return null;\n";
			ret += "}\n";
			ret += "};\n";
		} else {
			for (MOPVariable key : keys.values()) {
				ret += "static Object " + key + " = null;\n";
			}
			ret += "static Object " + value + " = null;\n";
		}

		return ret;
	}

}
