package com.runtimeverification.rvmonitor.logicrepository.plugins.srs;

public final class Terminal extends Symbol {
    
    private Terminal(String name) {
        this.name = name;
    }
    
    public static Symbol get(String name) {
        Symbol ret = symbolTable.get(name);
        if(ret == null) {
            ret = new Terminal(name);
            symbolTable.put(name, ret);
        } 
        return ret;
    }
} 
