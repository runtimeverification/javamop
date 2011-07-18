package javamop.output.aspect.advice;

import javamop.output.MOPVariable;

public class AroundAdviceLocalDecl {

	MOPVariable skipAroundAdvice;
	
	public AroundAdviceLocalDecl(){
		skipAroundAdvice = new MOPVariable("skipAroundAdvice");	
	}
	
	
	public String toString(){
		String ret = "";
		
		ret += "boolean " + skipAroundAdvice + " = false;\n";
		
		return ret;
	}
	
}
