package javamop.output.combinedaspect.event.advice;

import javamop.output.MOPVariable;
import javamop.parser.ast.mopspec.MOPParameters;
import javamop.parser.ast.type.PrimitiveType;
import javamop.parser.ast.type.Type;
import javamop.parser.ast.type.VoidType;

public class AroundAdviceReturn {
    private MOPVariable skipAroundAdvice;
    private MOPParameters parameters;
    private Type type;
    
    public AroundAdviceReturn(Type type, MOPParameters parameters) {
        skipAroundAdvice = new MOPVariable("MOP_skipAroundAdvice");
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
            ret += "if(" + skipAroundAdvice + "){\n";
            switch(pType.getType()){
                case Int:
                case Byte:
                case Short:
                case Char:
                    ret += "return 0;\n";
                    break;
                case Long:
                    ret += "return 0L;\n";
                    break;
                case Float:
                    ret += "return 0.0f;\n";
                    break;
                case Double:
                    ret += "return 0.0d;\n";
                    break;
                case Boolean:
                    ret += "return false;\n";
                    break;
                    
                default:
                    ret += "return null;\n";
                    break;
            }
            ret += "} else {\n";
            ret += "return proceed(" + parameters.parameterString() + ");\n";
            ret += "}\n";
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
