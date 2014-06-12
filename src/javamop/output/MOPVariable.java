package javamop.output;

import javamop.MOPNameSpace;

public class MOPVariable {
    private MOPVariable pred;
    private final String varName;
    
    public MOPVariable(String varName){
        this.varName = varName;
    }
    
    public MOPVariable(MOPVariable pred, String varName){
        this.pred = pred;
        this.varName = varName;
    }
    
    public String getVarName(){
        return varName;
    }
    
    public String toString(){
        if(pred != null)
            return pred.toString() + "." + MOPNameSpace.getMOPVar(varName);
        else
            return MOPNameSpace.getMOPVar(varName);
    }
    
}
