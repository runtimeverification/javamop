package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.LinkedHashSet;

public class ATransition {
    public LinkedHashSet<ATuple> tuples; 
    
    public ATransition(LinkedHashSet<ATuple> tuples){
        this.tuples = tuples;
    }
    
    public ATransition(){
        this.tuples = new LinkedHashSet<ATuple>(0);
    }
    
    public int size(){
        return tuples.size();
    }
    
    public ATransition and(ATransition second){
        LinkedHashSet<ATuple> retTuples = new LinkedHashSet<ATuple>(size() * second.size());
        for(ATuple tuple : tuples){
            for(ATuple secondTuple : second.tuples){
                retTuples.add(tuple.and(secondTuple));
            }
        }
        return new ATransition(retTuples);
    }
    
    public ATransition or(ATransition second){
        LinkedHashSet<ATuple> retTuples = new LinkedHashSet<ATuple>(size() + second.size());
        retTuples.addAll(tuples);
        retTuples.addAll(second.tuples);
        return new ATransition(retTuples);
    }
    
    
    public String toString(){
        return tuples.toString();
    }
}
