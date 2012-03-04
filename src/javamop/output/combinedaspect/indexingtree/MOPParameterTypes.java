package javamop.output.combinedaspect.indexingtree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;

public class MOPParameterTypes implements Iterable<String>{

	ArrayList<String> types;
	
	public MOPParameterTypes(MOPParameters params){
		types = new ArrayList<String>();
		if(params != null){
			for(MOPParameter param : params){
				types.add(param.getType().getOp());
			}
			
			Collections.sort(types);
		}
	}
	
	public Iterator<String> iterator(){
		return this.types.iterator();
	}
	
	public int size(){
		return types.size();
	}
	
	public boolean contains(MOPParameterTypes ts) {
		for (String t : ts.types) {
			if (!this.types.contains(t))
				return false;
		}
		return true;
	}
	
	public boolean equals(Object ts) {
		if (!(ts instanceof MOPParameterTypes))
			return false;
		MOPParameterTypes ts2 = (MOPParameterTypes) ts;
		if (this.size() != ts2.size())
			return false;
		return this.contains(ts2) && ts2.contains(this);
	}

	public int hashCode(){
		int code;
		if(types.size() == 0)
			return 0;
		
		code = types.get(0).hashCode();
		for(int i = 1; i < size(); i++){
			code = code * 31 + types.get(i).hashCode();
		}
		
		return code;
	}
	
	public String toString(){
		String ret = "";
		
		for(String t : types){
			if(ret.length() > 0)
				ret += "_";
			ret += t;
		}
		
		return ret;
	}
}
