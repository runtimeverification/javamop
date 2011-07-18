package javamop.output.aspect.advice;

import javamop.output.MOPVariable;
import javamop.parser.ast.mopspec.MOPParameters;
import javamop.parser.ast.type.PrimitiveType;
import javamop.parser.ast.type.Type;
import javamop.parser.ast.type.VoidType;

public class AroundAdviceReturn {
	MOPVariable skipAroundAdvice;
	MOPParameters parameters;
	Type type;

	public AroundAdviceReturn(Type type, MOPParameters parameters) {
		skipAroundAdvice = new MOPVariable("skipAroundAdvice");
		this.parameters = parameters;
		
		this.type = type;
	}

	public String toString() {
		String ret = "";

		if(type instanceof VoidType){
			ret += "if(" + skipAroundAdvice + "){\n";
			ret += "return;\n";
			ret += "} else {\n";
			ret += "proceed(" + parameters.parameterString() + ");\n";
			ret += "}\n";
		}else if(type instanceof PrimitiveType){
			PrimitiveType pType = (PrimitiveType)type;
			switch(pType.getType()){
			// Do more cases!!!!
			case Int:
				ret += "if(" + skipAroundAdvice + "){\n";
				ret += "return 0;\n";
				ret += "} else {\n";
				ret += "return proceed(" + parameters.parameterString() + ");\n";
				ret += "}\n";
				break;
			default:
				ret += "if(" + skipAroundAdvice + "){\n";
				ret += "return null;\n";
				ret += "} else {\n";
				ret += "return proceed(" + parameters.parameterString() + ");\n";
				ret += "}\n";
				break;
			}
		} else {
			ret += "if(" + skipAroundAdvice + "){\n";
			ret += "return null;\n";
			ret += "} else {\n";
			ret += "return proceed(" + parameters.parameterString() + ");\n";
			ret += "}\n";
		}

		return ret;
	}

}
