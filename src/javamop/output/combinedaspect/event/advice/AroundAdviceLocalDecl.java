package javamop.output.combinedaspect.event.advice;

import javamop.output.MOPVariable;

public class AroundAdviceLocalDecl {
    
    private MOPVariable skipAroundAdvice;
    
    public AroundAdviceLocalDecl(){
        skipAroundAdvice = new MOPVariable("MOP_skipAroundAdvice"); 
    }
    
    
    public String toString(){
        String ret = "";
        
        ret += "boolean " + skipAroundAdvice + " = false;\n";
        
        return ret;
    }
    
}
