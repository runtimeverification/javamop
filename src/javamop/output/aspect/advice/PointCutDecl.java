package javamop.output.aspect.advice;

import javamop.output.MOPVariable;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameters;

public class PointCutDecl {
	MOPVariable pointcutName;
	MOPParameters parameters;
	String pointcutStr;
	
	public PointCutDecl(JavaMOPSpec mopSpec, EventDefinition event){
		this.pointcutName = new MOPVariable(mopSpec.getName() + "_" + event.getUniqueId());
		this.parameters = event.getParametersWithoutThreadVar();
		this.pointcutStr = event.getPurePointCutString();
	}
	
	public MOPVariable getName(){
		return pointcutName;
	}
	
	public String toString() {
		String ret = "";

		ret += "pointcut " + pointcutName;
		ret += "(";
		ret += parameters.parameterDeclString();
		ret += ")";

		ret += " : ";
		if(pointcutStr != null && pointcutStr.length() != 0){
			ret += "(";
			ret += pointcutStr;
			ret += ")";
			ret += " && ";
		}
		ret += "!within(javamoprt.MOPObject+) && !adviceexecution()";
		
		
		ret += ";\n";

		return ret;
	}
}
