// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.output.combinedaspect.event.advice;

import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import javamop.output.MOPVariable;
import javamop.parser.ast.mopspec.MOPParameters;

/**
 * Generates the return statements used in around advice.
 */
public class AroundAdviceReturn {
    private final MOPVariable skipAroundAdvice;
    private final MOPParameters parameters;
    private final Type type;
    
    /**
     * Construct the around advice return statement.
     * @param type The return type of the around advice.
     * @param parameters The parameters of the around advice.
     */
    public AroundAdviceReturn(final Type type, final MOPParameters parameters) {
        skipAroundAdvice = new MOPVariable("MOP_skipAroundAdvice");
        this.parameters = parameters;
        
        this.type = type;
    }
    
    /**
     * The return statement for the around advice.
     * @return Java source code of a return statement for the around advice.
     */
    @Override
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
                case INT:
                case BYTE:
                case SHORT:
                case CHAR:
                    ret += "return 0;\n";
                    break;
                case LONG:
                    ret += "return 0L;\n";
                    break;
                case FLOAT:
                    ret += "return 0.0f;\n";
                    break;
                case DOUBLE:
                    ret += "return 0.0d;\n";
                    break;
                case BOOLEAN:
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
