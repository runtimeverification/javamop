package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.LinkedHashSet;
import java.util.HashMap;

public class AAutomaton{
    public LinkedHashSet<LTLFormula> Q;
    public LinkedHashSet<LinkedHashSet<Atom>> sigma;
    public HashMap<LTLFormula, ATransition> D;
    public LinkedHashSet<LinkedHashSet<LTLFormula>> I;
    //F is {END}, no need to store
    public LinkedHashSet<LTLFormula> R;
    
    
    AAutomaton(LTLFormula l){
        sigma = l.sigma;
        //This is fudged, it should be a set of sets containing a singleton
        //set, but this makes the GBA construction easier for this
        //specialized case
        I = l.toSetForm();
        
        Q = l.subFormulae();
        R = new LinkedHashSet<LTLFormula>();
        D = new HashMap<LTLFormula, ATransition>();
        for(LTLFormula sub : Q){
            if(sub.getLTLType() != LTLType.U){
                R.add(sub);
            }
            D.put(sub, sub.d(D));
        }
        D.put(END.get(), new ATransition());
        Q.add(END.get());
    }
    
    public String toString(){
        return "Q =\n" + Q + "\nI = \n  " + I + "\nR = \n  " + R 
        + "\nD = \n  " + D + "\n";
    }
}
