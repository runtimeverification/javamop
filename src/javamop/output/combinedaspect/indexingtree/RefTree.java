package javamop.output.combinedaspect.indexingtree;

import javamop.output.MOPVariable;
import javamop.parser.ast.mopspec.MOPParameter;

public class RefTree {
	MOPVariable name;
	String queryType;

	public RefTree(MOPVariable name, String queryType) {
		this.name = name;
		this.queryType = queryType;
	}


	public String get(MOPVariable tempRef, MOPParameter p){
		String ret = "";

		ret += tempRef + " = " + name + ".getRef(" + p.getName() + ")" + ";\n";
		
		return ret;
	}

	public String getRefNonCreative(MOPVariable tempRef, MOPParameter p){
		String ret = "";

		ret += tempRef + " = " + name + ".getRefNonCreative(" + p.getName() + ")" + ";\n";
		
		return ret;
	}

	public String toString(){
		String ret = "";
		
		ret += "static javamoprt.MOPRefMap ";
		ret += name;
		ret += " = ";
		ret += "new javamoprt.MOPRefMap();\n";

		return ret;
	}
	
	
}
