package com.runtimeverification.rvmonitor.logicrepository.plugins.srs;

import java.util.Map;

public class Fail implements AbstractSequence {
    
    private Fail() {}
    
    public String toString() {
        return "#fail";
    }
    
    public String toDotString() {
        return "\\#fail";
    }
    
    public static Fail theFail = new Fail();
    
    public static Fail get() {
        return theFail;
    }
    
    @Override
    public int dotLength() {
        return 5;
    }
    
    @Override
    public void getImpl(StringBuilder sb, Map<Symbol, Integer> symToNum) {
        sb.append(", ");
        sb.append(0);
    }
}
