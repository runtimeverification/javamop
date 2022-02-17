package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class BA {
    public LinkedHashSet<BAState> Q;
    public LinkedHashSet<LinkedHashSet<Atom>> sigma;
    public LinkedHashMap<BAState, BATransition> T;
    public LinkedHashSet<BAState> I;
    public LinkedHashSet<BAState> F;
    public LinkedHashSet<BAState> R;
    private int r;
    private ArrayList<LinkedHashSet<GBATransition>> gbaTF;
    
    //create an empty BA
    BA(){}
    
    BA(GBA gba){
        r = gba.TF.size() - 1;
        gbaTF = gba.TF;
        int qsize = gba.Q.size();
        //this flag is set if there is a terminal state in the machine,
        //i.e. a state that has a transition to itself on all sigma that
        //is in R.  For example the LTL formula "o a" or "a and b", but
        //NOT "[]a" .  We know this is the case if we see a transition
        //in the GBA where the next state is the empty LTL formula set
        boolean terminal = false;
        LinkedHashSet<LTLFormula> empty = new LinkedHashSet();
        BAState emptyState = new BAState(empty, empty, r);
        
        //This is the Q specified by the paper... extra useless states
        //  Q = new LinkedHashSet(qsize * qsize * r);
        //for(int i = 0; i <= r; ++i)
        //for(LinkedHashSet<LTLFormula> s1 : gba.Q)
        // for(LinkedHashSet<LTLFormula> s2: gba.Q) 
        //  Q.add(new BAState(s1,s2,i));
        
        sigma = gba.sigma;
        
        T = new LinkedHashMap();
        for(int i = 0; i <= r; ++i){
            for(GBATransition t : gba.T){
                if(t.next.size() == 0) terminal = true;
                BAState now = new BAState(t.previous,t.now, i);
                BAState next = new BAState(t.now, t.next, next(i, t));
                BATransition trans;
                if(!T.containsKey(now)){
                    trans = new BATransition();
                    T.put(now, trans);
                }
                else {
                    trans = T.get(now);
                }
                trans.put(t.symbols, next);
            }
        }
        
        //Instead, add only those states that appear in transitions
        Q = new LinkedHashSet();
        for(BAState s : T.keySet()){
            Q.add(s);
            BATransition trans = T.get(s);
            for(LinkedHashSet<LinkedHashSet<Atom>> symbols : trans.keySet()){
                Q.addAll(trans.get(symbols));
            }
        }
        
        //if this BA is terminal, add the accept state with [] as
        //current and next states, and sigma as the edge symboles.
        if(terminal){
            LinkedHashSet<BAState> moreQ = new LinkedHashSet<BAState>();
            for(BAState s : Q){
                if(s.r == r){
                    BAState now = new BAState(s.second, empty, r);
                    moreQ.add(now);
                    BATransition trans;
                    if(!T.containsKey(now)){
                        trans = new BATransition();
                        T.put(now, trans);
                    }
                    else {
                        trans = T.get(now);
                    }
                    trans.put(sigma, emptyState);
                    LinkedHashSet<BAState> set;
                }
            }
            Q.addAll(moreQ);
            BATransition trans = new BATransition();
            T.put(emptyState, trans);
            trans.put(sigma, emptyState);
        }
        
        I = new LinkedHashSet(gba.I.size());
        for(GBAState s : gba.I)
            I.add(new BAState(s.first, s.second, 0));  
        
        
        F = new LinkedHashSet(gba.F.size());
        for(int i = 0; i <= r; ++i)
            for(GBAState s : gba.F)
                F.add(new BAState(s.first, s.second, i)); 
            
            R = new LinkedHashSet(qsize * qsize);
        for(BAState s : Q)
            R.add(new BAState(s.first, s.second, r)); 
        
        R.add(emptyState);
    } 
    
    
    private int next(int j, GBATransition t){
        j = (j == r)? 0 : j; 
        for(; j < r; ++j){
            if(!(gbaTF.get(j + 1).contains(t))) 
                return j; 
        }
        return r;
    }
    
    public String toString(){
        return "Q = \n " + Q
        + "\nT = \n  " + T 
        + "\nI = \n  " + I 
        + "\nF = \n  " + F 
        + "\nR = \n " + R;
    }
}
