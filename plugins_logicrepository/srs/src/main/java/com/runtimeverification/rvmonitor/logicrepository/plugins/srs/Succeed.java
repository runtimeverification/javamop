package com.runtimeverification.rvmonitor.logicrepository.plugins.srs;

import java.util.Map;

public class Succeed implements AbstractSequence {
    
    private Succeed() {}
    
    public String toString() {
        return "#succeed";
    }
    
    public String toDotString() {
        return "\\#succeed";
    }
    
    public static Succeed theSucceed = new Succeed();
    
    public static Succeed get() {
        return theSucceed;
    }
    
    @Override 
    public int dotLength() {
        return 8;
    }
    
    @Override
    public void getImpl(StringBuilder sb, Map<Symbol, Integer> symToNum) {
        sb.append(", ");
        sb.append(1);
    }
}
