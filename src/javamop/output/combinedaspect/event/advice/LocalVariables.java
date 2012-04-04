package javamop.output.combinedaspect.event.advice;

import java.util.ArrayList;
import java.util.HashMap;

import javamop.output.MOPVariable;
import javamop.output.combinedaspect.CombinedAspect;
import javamop.output.combinedaspect.indexingtree.reftree.RefTree;
import javamop.output.monitor.SuffixMonitor;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;

public class LocalVariables {
	HashMap<String, RefTree> refTrees;

	ArrayList<Variable> variables= new ArrayList<Variable>();

	HashMap<String, Variable> varMap = new HashMap<String, Variable>();
	HashMap<String, Variable> tempRefs = new HashMap<String, Variable>();

	
	public LocalVariables(JavaMOPSpec mopSpec, EventDefinition event, CombinedAspect combinedAspect){
		this.refTrees = combinedAspect.indexingTreeManager.refTrees;
		
		SuffixMonitor monitorClass = combinedAspect.monitors.get(mopSpec);
		String monitorName = monitorClass.getOutermostName().toString(); 
		String monitorSetName = combinedAspect.monitorSets.get(mopSpec).getName().toString();
		
		// default variables
		addVar("boolean", "cacheHit", "true");
		addVar("Object", "obj");
		addVar("javamoprt.map.MOPMap", "tempMap");
		
		addVar(monitorName,"mainMonitor", "null");
		addVar(monitorName,"origMonitor", "null");
		addVar(monitorName,"lastMonitor", "null");
		addVar(monitorName,"monitor", "null");

		addVar("javamoprt.map.MOPMap", "mainMap", "null");
		addVar("javamoprt.map.MOPMap", "origMap", "null");
		addVar("javamoprt.map.MOPMap", "lastMap", "null");
		
		addVar(monitorSetName,"mainSet", "null");
		addVar(monitorSetName,"origSet", "null");
		addVar(monitorSetName,"lastSet", "null");
		addVar(monitorSetName,"monitors", "null");
		
		for (MOPParameter p : mopSpec.getParameters()) {
			addTempRef(p.getName(), getRefTree(p).getResultType(), "TempRef_" + p.getName());
		}
	}
	
	public RefTree getRefTree(MOPParameter p) {
		return refTrees.get(p.getType().toString());
	}
	
	public void addVar(String type, String mopVarName){
		MOPVariable mopVar = new MOPVariable(mopVarName);
		
		if(varMap.get(mopVarName) == null){
			Variable var = new Variable(type, mopVar); 
			
			variables.add(var);
			varMap.put(mopVarName, var);
		}
	}
	
	public void addVar(String type, String mopVarName, String init){
		MOPVariable mopVar = new MOPVariable(mopVarName);
		
		if(varMap.get(mopVarName) == null){
			Variable var = new Variable(type, mopVar, init); 
			
			variables.add(var);
			varMap.put(mopVarName, var);
		}
	}

	
	public void addTempRef(String param, String type, String tempRefName){
		MOPVariable tempRef = new MOPVariable(tempRefName);
		
		if(tempRefs.get(param.toString()) == null){
			Variable var = new Variable(type, tempRef); 
			
			variables.add(var);
			tempRefs.put(param.toString(), var);
		}
	}

	public void init(){
		for(Variable var : variables){
			var.used = false;
		}
	}
	
	public MOPVariable get(String name){
		Variable var = varMap.get(name);
		
		if(var == null)
			return null;
		
		var.used = true;
		
		return var.var;
	}
	
	public MOPVariable getTempRef(MOPParameter p){
		return getTempRef(p.getName());
	}
	
	public MOPVariable getTempRef(String paramName){
		Variable var = tempRefs.get(paramName);
		
		if(var == null)
			return null;
		
		var.used = true;
		return var.var;
	}
	
	public String varDecl(){
		String ret = "";
		
		for(Variable var : variables){
			if(var.used){
				ret += var.type + " " + var.var;
				if(var.init != null)
					ret += " = " + var.init;
				ret += ";\n";
			}
		}
		
		if (ret.length() != 0)
			ret += "\n";
		
		return ret;
	}
	
	class Variable {
		String type;
		MOPVariable var;
		boolean used = false;
		String init = null;
		
		Variable(String type, MOPVariable var){
			this.type = type;
			this.var = var;
		}
		
		Variable(String type, MOPVariable var, String init){
			this.type = type;
			this.var = var;
			this.init = init;
		}
	}
}
