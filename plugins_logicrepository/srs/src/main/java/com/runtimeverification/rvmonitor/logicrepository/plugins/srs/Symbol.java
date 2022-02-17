package com.runtimeverification.rvmonitor.logicrepository.plugins.srs;

import java.util.HashMap;

public class Symbol {
    protected static HashMap<String, Symbol> symbolTable;
    protected String name;
    
    protected Symbol() { name = ""; }
    
    protected Symbol(String name) {
        this.name = name;
    }
    
    static {
        symbolTable = new HashMap<String, Symbol>();
    }
    
    public static Symbol get(String name) {
        Symbol ret = symbolTable.get(name);
        if(ret == null) {
            ret = new Symbol(name);
            symbolTable.put(name, ret);
        } 
        return ret;
    }
    
    public String toString() {
        return name;
    }
    
    public String toDotString() {
        if(name.equals("^")) return "\\mathbin{\\char`\\^}";
        if(name.equals("$")) return "\\$";
        return name;
    }
    
    public int length() {
        return name.length();
    }
}
