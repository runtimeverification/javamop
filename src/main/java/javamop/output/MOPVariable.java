// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.output;

import javamop.util.MOPNameSpace;

/**
 * A variable used in the generated code, with an optional package component.
 */
public class MOPVariable {
    private final MOPVariable pred;
    private final String varName;
    
    /**
     * A MOP variable in the local scope.
     * @param varName The name of the variable.
     */
    public MOPVariable(String varName){
        this.pred = null;
        this.varName = varName;
    }
    
    /**
     * A MOP variable in a scope.
     * @param pred The scope/package of the variable.
     * @param varName The name of the variable.
     */
    public MOPVariable(MOPVariable pred, String varName){
        this.pred = pred;
        this.varName = varName;
    }
    
    /**
     * The name of the variable without scope/package.
     * @return The variable name.
     */
    public String getVarName(){
        return varName;
    }
    
    /**
     * Retrieve the full variable name with scope and ensured uniqueness.
     * @return The variable name and scope.
     */
    @Override
    public String toString(){
        if(pred != null) {
            return pred.toString() + "." + MOPNameSpace.getMOPVar(varName);
        } else {
            return MOPNameSpace.getMOPVar(varName);
        }
    }
    
}
