package javamop.output.combinedaspect.indexingtree.decentralized;

import java.util.HashMap;

import javamop.MOPException;
import javamop.output.MOPVariable;
import javamop.output.combinedaspect.event.advice.LocalVariables;
import javamop.output.combinedaspect.indexingtree.IndexingCache;
import javamop.output.combinedaspect.indexingtree.IndexingTree;
import javamop.output.combinedaspect.indexingtree.reftree.RefTree;
import javamop.output.monitor.SuffixMonitor;
import javamop.output.monitorset.MonitorSet;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;

public class FullParamIndexingTree extends IndexingTree {
	MOPParameter firstKey;

	public FullParamIndexingTree(String aspectName, MOPParameters queryParam, MOPParameters contentParam, MOPParameters fullParam, MonitorSet monitorSet, SuffixMonitor monitor,
			HashMap<String, RefTree> refTrees, boolean perthread, boolean isGeneral) throws MOPException {
		super(aspectName, queryParam, contentParam, fullParam, monitorSet, monitor, refTrees, perthread, isGeneral);

		if (!isFullParam)
			throw new MOPException("FullParamIndexingTree can be created only when queryParam equals to fullParam.");

		if (queryParam.size() <= 1)
			throw new MOPException("Decentralized FullParamIndexingTree should contain at least two parameter.");

		if (anycontent) {
			this.name = new MOPVariable(aspectName + "_" + queryParam.parameterStringUnderscore() + "_Map");
			
			this.cache = new IndexingCache(this.name, this.queryParam, this.fullParam, this.monitorClass, this.monitorSet, refTrees, perthread, isGeneral);
			//this.cache = new LocalityIndexingCache(this.name, this.queryParam, this.fullParam, this.monitorClass, this.monitorSet, refTrees, perthread, isGeneral);
		} else {
			if (!contentParam.contains(queryParam))
				throw new MOPException("[Internal] contentParam should contain queryParam");
			
			this.name = new MOPVariable(aspectName + "_" + queryParam.parameterStringUnderscore() + "__To__" + contentParam.parameterStringUnderscore() + "_Map");
		}
		
		this.firstKey = queryParam.get(0);
	}

	public MOPParameter getLastParam(){
		return queryParam.get(queryParam.size() - 1);
	}

	protected String lookupIntermediateCreative(LocalVariables localVars, MOPVariable monitor, MOPVariable lastMap, MOPVariable lastSet, int i) throws MOPException {
		String ret = "";

		MOPVariable obj = localVars.get("obj");
		MOPVariable tempMap = localVars.get("tempMap");

		MOPParameter p = queryParam.get(i);
		MOPVariable tempRef = localVars.getTempRef(p);

		ret += obj + " = " + tempMap + ".getMap(" + tempRef + ");\n";

		ret += "if (" + obj + " == null) {\n";

		ret += createNewMap(i + 1) + ";\n";
		
		ret += tempMap + ".putMap(" + tempRef + ", " + obj + ");\n";
		ret += "}\n";

		if (i == queryParam.size() - 2) {
			ret += lastMap + " = (javamoprt.map.MOPAbstractMap)" + obj + ";\n";
			ret += lookupNodeLast(localVars, monitor, lastMap, lastSet, i + 1, true);
		} else {
			ret += tempMap + " = (javamoprt.map.MOPAbstractMap)" + obj + ";\n";
			ret += lookupIntermediateCreative(localVars, monitor, lastMap, lastSet, i + 1);
		}

		return ret;
	}
	
	protected String lookupIntermediateNonCreative(LocalVariables localVars, MOPVariable monitor, MOPVariable lastMap, MOPVariable lastSet, int i) throws MOPException {
		String ret = "";

		MOPVariable obj = localVars.get("obj");
		MOPVariable tempMap = localVars.get("tempMap");

		MOPParameter p = queryParam.get(i);
		MOPVariable tempRef = localVars.getTempRef(p);

		ret += obj + " = " + tempMap + ".getMap(" + tempRef + ");\n";

		ret += "if (" + obj + " != null) {\n";

		if (i == queryParam.size() - 2) {
			ret += lastMap + " = (javamoprt.map.MOPAbstractMap)" + obj + ";\n";
			ret += lookupNodeLast(localVars, monitor, lastMap, lastSet, i + 1, false);
		} else {
			ret += tempMap + " = (javamoprt.map.MOPAbstractMap)" + obj + ";\n";
			ret += lookupIntermediateNonCreative(localVars, monitor, lastMap, lastSet, i + 1);
		}

		ret += "}\n";

		return ret;
	}
	
	protected String lookupNodeLast(LocalVariables localVars, MOPVariable monitor, MOPVariable lastMap, MOPVariable lastSet, int i, boolean creative) {
		String ret = "";

		MOPParameter p = queryParam.get(i);
		MOPVariable tempRef = localVars.getTempRef(p);

		ret += monitor + " = " + "(" + monitorClass.getOutermostName() + ")" + lastMap + ".getNode(" + tempRef + ");\n";

		return ret;
	}
	
	public String lookupNode(LocalVariables localVars, String monitorStr, String lastMapStr, String lastSetStr, boolean creative) throws MOPException {
		String ret = "";

		MOPVariable monitor = localVars.get(monitorStr);
		MOPVariable lastMap = localVars.get(lastMapStr);

		if (creative){
			ret += createTree();
		}

		if (queryParam.size() == 2) {
			ret += lastMap + " = " + retrieveTree() + ";\n";
			if(creative){
				ret += lookupNodeLast(localVars, monitor, lastMap, null, 1, creative);
			} else {
				ret += "if (" + lastMap + " != null) {\n";
				ret += lookupNodeLast(localVars, monitor, lastMap, null, 1, creative);
				ret += "}\n";
			}
		} else {
			MOPVariable tempMap = localVars.get("tempMap");
			ret += tempMap + " = " + retrieveTree() + ";\n";

			if (creative) {
				ret += lookupIntermediateCreative(localVars, monitor, lastMap, null, 1);
			} else {
				ret += "if (" + lastMap + " != null) {\n";
				ret += lookupIntermediateNonCreative(localVars, monitor, lastMap, null, 1);
				ret += "}\n";
			}
		}

		return ret;
	}

	public String lookupSet(LocalVariables localVars, String monitorStr, String lastMapStr, String lastSetStr, boolean creative) {
		return "";
	}
	
	public String lookupNodeAndSet(LocalVariables localVars, String monitorStr, String lastMapStr, String lastSetStr, boolean creative) throws MOPException {
		return lookupNode(localVars, monitorStr, lastMapStr, lastSetStr, creative);
	}

	public String attachNode(LocalVariables localVars, String monitorStr, String lastMapStr, String lastSetStr){
		String ret = "";

		MOPVariable monitor = localVars.get(monitorStr);

		MOPVariable tempRef = localVars.getTempRef(getLastParam());

		if (queryParam.size() == 2) {
			ret += retrieveTree() + ".putNode(" + tempRef + ", " + monitor + ");\n";
		} else {
			MOPVariable lastMap = localVars.get(lastMapStr);

			ret += lastMap + ".putNode(" + tempRef + ", " + monitor + ");\n";
		}

		return ret;
	}

	public String attachSet(LocalVariables localVars, String monitorStr, String lastMapStr, String lastSetStr){
		return "";
	}
	
	public String addMonitor(LocalVariables localVars, String monitorStr, String tempMapStr, String tempSetStr) throws MOPException {
		String ret = "";

		MOPVariable obj = localVars.get("obj");
		MOPVariable tempMap = localVars.get(tempMapStr);
		MOPVariable monitor = localVars.get(monitorStr);

		ret += createTree();

		ret += tempMap + " = " + retrieveTree() + ";\n";

		for (int i = 1; i < queryParam.size() - 1; i++) {
			MOPParameter p = queryParam.get(i);
			MOPVariable tempRef = localVars.getTempRef(p);

			ret += obj + " = " + tempMap + ".getMap(" + tempRef + ");\n";

			ret += "if (" + obj + " == null) {\n";

			ret += createNewMap(i + 1) + ";\n";

			ret += tempMap + ".putMap(" + tempRef + ", " + obj + ");\n";
			ret += "}\n";

			ret += tempMap + " = (javamoprt.map.MOPAbstractMap)" + obj + ";\n";
 		}

		MOPParameter p = getLastParam();
		MOPVariable tempRef = localVars.getTempRef(p);

		ret += tempMap + ".putNode(" + tempRef + ", " + monitor + ");\n";

		return ret;
	}


	public boolean containsSet() {
		return false;
	}

	public String retrieveTree() {
		if(parentTree != null)
			return parentTree.retrieveTree();
		
		return firstKey.getName() + "." + name.toString();
	}

	protected String createTree() throws MOPException {
		String ret = "";
		
		ret += "if (" + retrieveTree() + " == null) {\n";
		ret += retrieveTree() + " = " + createNewMap(1) + ";\n";
		ret += "}\n";

		return ret;
	}

	protected String createNewMap(int paramIndex) throws MOPException {
		String ret = "";

		if(paramIndex < 1)
			throw new MOPException("The first parameter cannot use getMapType(int).");
		
		if(isGeneral){
			if (paramIndex == queryParam.size() - 1) {
				ret += "new javamoprt.map.MOPMapOfMonitor(" + fullParam.getIdnum(queryParam.get(paramIndex)) + ")";
			} else {
				ret += "new javamoprt.map.MOPMapOfAll(" + fullParam.getIdnum(queryParam.get(paramIndex)) + ")";
			}
		} else {
			if (paramIndex == queryParam.size() - 1) {
				ret += "new javamoprt.map.MOPMapOfMonitor(" + fullParam.getIdnum(queryParam.get(paramIndex)) + ")";
			} else {
				ret += "new javamoprt.map.MOPMapOfMapSet(" + fullParam.getIdnum(queryParam.get(paramIndex)) + ")";
			}
		}

		return ret;
	}

	public String getRefTreeType(){
		String ret = "";
		
		if(parentTree != null)
			return parentTree.getRefTreeType();

		return ret;
	}

	public String toString() {
		String ret = "";

		if(parentTree == null){
			if(isGeneral){
				if (queryParam.size() == 1) {
					ret += "static javamoprt.map.MOPAbstractMap " + name + " = null;\n";
				} else {
					ret += "static javamoprt.map.MOPAbstractMap " + name + " = null;\n";
				}
			} else {
				if (queryParam.size() == 1) {
					ret += "static javamoprt.map.MOPAbstractMap " + name + " = null;\n";
				} else {
					ret += "static javamoprt.map.MOPAbstractMap " + name + " = null;\n";
				}
			}
		}
		
		if (cache != null)
			ret += cache;

		return ret;
	}
	
	public String reset() {
		String ret = "";

		if(parentTree == null){
			if(isGeneral){
				if (queryParam.size() == 1) {
					ret += name + " = null;\n";
				} else {
					ret += name + " = null;\n";
				}
			} else {
				if (queryParam.size() == 1) {
					ret += name + " = null;\n";
				} else {
					ret += name + " = null;\n";
				}
			}
		}
		
		if (cache != null)
			ret += cache.reset();

		return ret;
	}

}