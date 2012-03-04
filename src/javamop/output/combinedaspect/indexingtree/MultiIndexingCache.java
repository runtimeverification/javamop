package javamop.output.combinedaspect.indexingtree;

import java.util.ArrayList;

import javamop.MOPException;
import javamop.output.MOPVariable;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;

public class MultiIndexingCache {
	MOPVariable name;
	MOPParameterTypes queryTypes;

	MOPVariable value;
	ArrayList<MOPVariable> keys = new ArrayList<MOPVariable>();

	boolean perthread = false;

	public MultiIndexingCache(MOPVariable name, MOPParameterTypes queryTypes, boolean perthread) {
		this.name = name;
		this.queryTypes = queryTypes;
		this.perthread = perthread;

		this.value = new MOPVariable(name + "_cachevalue");

		for (int i = 0; i < this.queryTypes.size(); i++) {
			this.keys.add(new MOPVariable(name + "_cachekey_" + i));
		}
	}

	public String getCacheValue(MOPParameters param, MOPVariable obj, int current_index_id) {
		String ret = "";

		if (param.size() != queryTypes.size()) {
			ret += "*** ERR: MultiIndexingCache.getCacheValue() ***;";
			return ret;
		}

		if (perthread) {
			ret += "if(";
			for (int i = 0; i < param.size(); i++) {
				if (i > 0)
					ret += " && ";
				ret += param.get_lexicographic(i).getName() + " == " + keys.get(i) + ".get()";
			}
			ret += "){\n";
			ret += obj + " = " + value + ".get()[" + current_index_id + "];\n";
			ret += "}\n";
		} else {
			ret += "if(";
			for (int i = 0; i < param.size(); i++) {
				if (i > 0)
					ret += " && ";
				ret += param.get_lexicographic(i).getName() + " == " + keys.get(i);
			}
			ret += "){\n";
			ret += obj + " = " + value + "[" + current_index_id + "];\n";
			ret += "}\n";
		}

		return ret;
	}

	public String setCacheKeys(MOPParameters param) {
		String ret = "";

		for (int i = 0; i < param.size(); i++) {
			MOPParameter p = param.get_lexicographic(i);

			if (perthread) {
				ret += keys.get(i) + ".set(" + p.getName() + ");\n";
			} else {
				ret += keys.get(i) + " = " + p.getName() + ";\n";
			}
		}

		return ret;
	}

	public String setCacheValue(MOPVariable objs) {
		String ret = "";

		if (perthread) {
			ret += value + ".set(" + objs + ");\n";
		} else {
			ret += value + " = " + objs + ";\n";
		}

		return ret;
	}

	public String toString() {
		String ret = "";

		if (perthread) {
			for (MOPVariable key : keys) {
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
			for (MOPVariable key : keys) {
				ret += "static Object " + key + " = null;\n";
			}
			ret += "static Object[] " + value + " = null;\n";
		}

		return ret;
	}

}
