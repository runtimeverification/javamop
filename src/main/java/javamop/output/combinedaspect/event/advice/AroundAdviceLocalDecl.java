// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.output.combinedaspect.event.advice;

import javamop.output.MOPVariable;

/**
 * Generates a declaration for whether to skip the around advice or not.
 */
public class AroundAdviceLocalDecl {
    
    private final MOPVariable skipAroundAdvice;
    
    /**
     * Construct the declaration.
     */
    public AroundAdviceLocalDecl(){
        skipAroundAdvice = new MOPVariable("MOP_skipAroundAdvice"); 
    }
    
    /**
     * The java declaration for if the around advice should be skipped.
     * @return A java source code declaration.
     */
    @Override
    public String toString() {
        return "boolean " + skipAroundAdvice + " = false;\n";
    }
}
