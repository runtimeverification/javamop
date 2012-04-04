package javamop.output.combinedaspect.indexingtree;

import java.util.HashMap;

import javamop.output.MOPVariable;
import javamop.output.combinedaspect.event.advice.LocalVariables;
import javamop.output.combinedaspect.indexingtree.reftree.RefTree;
import javamop.output.monitor.SuffixMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;


// it turns out that this cache is inefficient.
// do not use.
public class LocalityIndexingCache extends IndexingCache{
	int size = 16;
	
	public LocalityIndexingCache(MOPVariable name, MOPParameters param, MOPParameters fullParam, SuffixMonitor monitor, MonitorSet monitorSet, HashMap<String, RefTree> refTrees, boolean perthread, boolean isGeneral) {
		super(name, param, fullParam, monitor, monitorSet, refTrees, perthread, isGeneral);
	}

	public String getKeyType(MOPParameter p) {
		return refTrees.get(p.getType().toString()).getResultType();
	}

	public String getTreeType(MOPParameter p) {
		return refTrees.get(p.getType().toString()).getType();
	}

	public MOPVariable getKey(MOPParameter p) {
		return keys.get(p.getName());
	}

	public MOPVariable getKey(int i) {
		return keys.get(param.get(i).getName());
	}

	public String getKeyComparison() {
		String ret = "";

		for (int i = 0; i < param.size(); i++) {
			if (i > 0) {
				ret += " && ";
			}
			if (perthread) {
				ret += param.get(i).getName() + " == " + getKey(i) + ".get().get()";
			} else {
				ret += param.get(i).getName() + " == " + getKey(i) + "[thisJoinPoint.getStaticPart().getId() & " + (size - 1) + "].get()";
			}
		}

		return ret;
	}

	public String getCacheKeys(LocalVariables localVars) {
		String ret = "";

		for (MOPParameter p : param) {
			MOPVariable tempRef = localVars.getTempRef(p);

			ret += tempRef + " = ";

			if (perthread) {
				ret += getKey(p) + ".get();\n";
			} else {
				ret += getKey(p) + "[thisJoinPoint.getStaticPart().getId() & " + (size - 1) + "];\n";
			}
		}

		return ret;
	}

	public String getCacheSet(MOPVariable obj) {
		String ret = "";

		if (!hasSet)
			return ret;

		if (perthread) {
			ret += obj + " = " + set + ".get();\n";
		} else {
			ret += obj + " = " + set + "[thisJoinPoint.getStaticPart().getId() & " + (size - 1) + "];\n";
		}

		return ret;
	}

	public String getCacheNode(MOPVariable obj) {
		String ret = "";

		if (perthread) {
			ret += obj + " = " + node + ".get();\n";
		} else {
			ret += obj + " = " + node + "[thisJoinPoint.getStaticPart().getId() & " + (size - 1) + "];\n";
		}

		return ret;
	}

	public String setCacheKeys(LocalVariables localVars) {
		String ret = "";

		for (MOPParameter p : param) {
			MOPVariable tempRef = localVars.getTempRef(p);

			if (perthread) {
				ret += getKey(p) + ".set(" + tempRef + ");\n";
			} else {
				ret += getKey(p) + "[thisJoinPoint.getStaticPart().getId() & " + (size - 1) + "] = " + tempRef + ";\n";
			}
		}

		return ret;
	}

	public String setCacheSet(MOPVariable obj) {
		String ret = "";

		if (!hasSet)
			return ret;

		if (perthread) {
			ret += set + ".set(" + obj + ");\n";
		} else {
			ret += set + "[thisJoinPoint.getStaticPart().getId() & " + (size - 1) + "] = " + obj + ";\n";
		}

		return ret;
	}

	public String setCacheNode(MOPVariable obj) {
		String ret = "";

		if(!hasNode)
			return ret;
		
		if (perthread) {
			ret += node + ".set(" + obj + ");\n";
		} else {
			ret += node + "[thisJoinPoint.getStaticPart().getId() & " + (size - 1) + "] = " + obj + ";\n";
		}

		return ret;
	}

	public String init(){
		String ret = "";

		MOPVariable i = new MOPVariable("i");
		
		if(perthread)
			return ret;

		for (MOPParameter p : param) {
			MOPVariable key = keys.get(p.getName());
			ret += key + " = new " + getKeyType(p) + "[" + size + "];\n";
		}
		if (hasSet) {
			ret += set + " = new " + setType + "[" + size + "];\n";
		}
		if (hasNode) {
			ret += node + " = new " + nodeType + "[16];\n";
		}
		
		ret += "for(int " + i + " = 0; " + i + " < 16; " + i + "++){\n";
		for (MOPParameter p : param) {
			MOPVariable key = keys.get(p.getName());
			ret += key + "[" + i + "] = " + getTreeType(p) + ".NULRef;\n";
		}
		if (hasSet) {
			ret += set + "[" + i + "] " + " = null;\n";
		}
		if (hasNode) {
			ret += node + "[" + i + "] " + " = null;\n";
		}
		ret += "}\n";
		
		return ret;
	}
	
	public String toString() {
		String ret = "";

		if (perthread) {
			for (MOPParameter p : param) {
				MOPVariable key = keys.get(p.getName());

				ret += "static final ThreadLocal " + key + " = new ThreadLocal() {\n";
				ret += "protected " + getKeyType(p) + " initialValue(){\n";
				ret += "return " + getKeyType(p) + ".NULRef;\n";
				ret += "}\n";
				ret += "};\n";
			}
			
			if (hasSet) {
				ret += "static final ThreadLocal " + set + " = new ThreadLocal() {\n";
				ret += "protected " + setType + " initialValue(){\n";
				ret += "return null;\n";
				ret += "}\n";
				ret += "};\n";
			}

			if (hasNode) {
				ret += "static final ThreadLocal " + node + " = new ThreadLocal() {\n";
				ret += "protected " + nodeType + " initialValue(){\n";
				ret += "return null;\n";
				ret += "}\n";
				ret += "};\n";
			}
		} else {
			for (MOPParameter p : param) {
				MOPVariable key = keys.get(p.getName());
				ret += "static " + getKeyType(p) + "[] " + key + ";\n";
			}
			if (hasSet) {
				ret += "static " + setType + "[] " + set + ";\n";
			}
			if (hasNode) {
				ret += "static " + nodeType + "[] " + node + ";\n";
			}
		}

		return ret;
	}

}
