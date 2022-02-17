package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GBA {
    public LinkedHashSet<LinkedHashSet<LTLFormula>> Q;
    public LinkedHashSet<LinkedHashSet<Atom>> sigma;
    public LinkedHashSet<GBATransition> T;
    public LinkedHashSet<GBAState> I;
    public LinkedHashSet<GBAState> F;
    public ArrayList<LinkedHashSet<GBATransition>> TF;
    private LinkedHashSet<LTLFormula> ENDSet;
    
    private  LinkedHashSet<GBAState> fix;
    
    GBA(AAutomaton aa){
        sigma = aa.sigma;
        ENDSet = new LinkedHashSet<LTLFormula>(1);
        ENDSet.add(END.get());
        
        LinkedHashSet<LinkedHashSet<LTLFormula>> ENDSetSet 
        = new LinkedHashSet<LinkedHashSet<LTLFormula>>(1);
        ENDSetSet.add(ENDSet);
        
        // I = aa.F x aa.I
        I = SetOperations.cross(ENDSetSet, aa.I);
        
        // fix = aa.F x aa.I 
        //(but we don't want it to be the SAME set as I)
        fix = SetOperations.cross(ENDSetSet, aa.I);
        
        generateT(aa);
        Q = new LinkedHashSet<LinkedHashSet<LTLFormula>>();
        for(GBAState q : fix){
            Q.add(q.first);
            Q.add(q.second);
        }
        
        //cleanup transitions (not in paper)
        /* for(LinkedHashSet<LTLFormula> s : Q){
         *         T.add(new GBATransition(s,
         *                                 new LinkedHashSet<LTLFormula>(),
         *                                  new LinkedHashSet<LTLFormula>(),
         *                                 sigma));
    }*/
        
        
        LinkedHashSet<LinkedHashSet<LTLFormula>> powQ 
        = SetOperations.pow(SetOperations.toLTLArray(aa.Q));
        F = SetOperations.intersect(
            SetOperations.cross(powQ, ENDSetSet),
                                    SetOperations.cross(Q, Q)
        );
        TF = new ArrayList<LinkedHashSet<GBATransition>>();
        TF.add(new LinkedHashSet<GBATransition>());
        LinkedHashSet<LTLFormula> QminusR = SetOperations.difference(aa.Q, aa.R);
        for(LTLFormula q : QminusR){
            LinkedHashSet<GBATransition> inner = new LinkedHashSet<GBATransition>();
            for(GBATransition trans : T){
                if(!trans.now.contains(q)){
                    inner.add(trans);
                }
                for(ATuple at : aa.D.get(q).tuples){
                    if(SetOperations.subset(at.previous, trans.previous) 
                        &&  SetOperations.subset(at.next, trans.next)
                        &&  SetOperations.subset(trans.symbols, at.symbols)
                        &&  !at.next.contains(q)){
                        inner.add(trans);
                    break;
                        }
                }
            }
            TF.add(inner);
        }
    }
    
    private void generateT(AAutomaton aa){
        T = new LinkedHashSet<GBATransition>();
        boolean fixChanged;
        HashMap<LTLFormula, ATransition> D = aa.D;
        
        fixChanged = false;
        ATransition product;
        for(Iterator<GBAState> i = fix.iterator(); i.hasNext();){
            GBAState q = i.next();
            if(q.second.isEmpty()) continue;
            
            product = computeProduct(q, D);
            
            for(ATuple at : product.tuples){
                //System.out.println(i + "---" + q + "!!!" + at);
                //System.out.println(at.previous + "---" + q.first);
                //If at.previous is a subset of q.first
                //this is either a pure present or future state
                if(SetOperations.subset(at.previous,q.first)){
                    fixChanged = handleFuturePresent(at, q);
                    //if the fixed point changed, start over
                    //this is potentially less efficient than
                    //waiting for the end of the iteration to repeat,
                    //but java's stupid iterators require this anyway
                    if(fixChanged) i = fix.iterator();           
                }
                else{
                    fixChanged = handlePast(at, q);
                    //ditto
                    if(fixChanged) i = fix.iterator(); 
                }
            }
        } //end foreach (X,Y) in fix
    }
    
    private ATransition computeProduct(GBAState q, HashMap<LTLFormula, ATransition> D){
        ATransition product = null;
        for(LTLFormula l : q.second){
            if(product == null) {
                product = D.get(l);
                continue;
            }
            product = product.and(D.get(l));
        }
        return product;
    }
    
    private boolean handleFuturePresent(ATuple at, GBAState q){
        //System.out.println("future " + at + "***" + q);
        boolean fixChanged = false;
        GBAState testState = new GBAState(q.second, at.next);
        if(!fix.contains(testState)){
            fix.add(testState);   
            fixChanged = true;
        }
        GBATransition testTrans 
        = new GBATransition(q.first, q.second, at.next, at.symbols);
        if(!T.contains(testTrans)){
            T.add(testTrans);
            fixChanged = true;
        }
        
        return fixChanged;
    }
    
    ///checking way more transitions that I need to.  Clean this up!
    private boolean handlePast(ATuple at, GBAState q){
        boolean fixChanged = false;
        
        //System.out.println("past " + at + "***" + q);
        
        //Here we expand the initial state IF necessary
        //this can also expand the fixed point.  This happens
        //when we find a state with unfullfilled past operators
        for(GBATransition testTrans : T){
            if(!testTrans.previous.equals(ENDSet)) continue;
            if(!testTrans.now.equals(q.first)) continue;
            if(!testTrans.next.equals(q.second)) continue;
            if(!I.contains(new GBAState(ENDSet, testTrans.now))) {
                continue;
            }
            //System.out.println("test " + testTrans);
            LinkedHashSet<LTLFormula> toAddNext 
            = new LinkedHashSet<LTLFormula>(testTrans.now);
            toAddNext.addAll(at.previous);
            GBAState toAdd = new GBAState(ENDSet, toAddNext);
            //System.out.println("toAdd " + toAdd + "\n");
            if(!fix.contains(toAdd)) {
                fix.add(toAdd);
                fixChanged = true;
                I.add(toAdd);
            }
        }
        
        for(Iterator<GBATransition> i = T.iterator(); i.hasNext();){
            GBATransition a = i.next();
            for(Iterator<GBATransition> j = T.iterator(); j.hasNext();){
                GBATransition b = j.next();
                //This could get really bad.  Might want to compare
                //hashCodes first
                if(a == b) continue;
                if(!a.next.equals(q.first)) continue;
                if(!b.now.equals(q.first)) continue;
                //if(!b.next.equals(q.second)) continue;
                if(!a.now.equals(b.previous)) continue;
                LinkedHashSet<LTLFormula> toAddNext 
                = new LinkedHashSet<LTLFormula>(a.next);
                toAddNext.addAll(at.previous);
                GBAState toAdd = new GBAState(a.now, toAddNext);
                if(!fix.contains(toAdd)) {
                    fix.add(toAdd);
                    fixChanged = true;
                }
                GBATransition toAddTrans 
                = new GBATransition(a.previous, a.now, toAddNext, a.symbols);
                if(!T.contains(toAddTrans)){
                    T.add(toAddTrans);
                    fixChanged = true;
                    i = T.iterator(); //protect against concurrent modification
                    j = T.iterator(); //protect against concurrent modification
                }
            }
        }
        
        //
        return fixChanged;
    }
    
    public String toString(){
        return "Q = \n " + Q
        + "\nT = \n  " + T 
        + "\nI = \n  " + I 
        + "\nF = \n  " + F 
        + "\nTF = \n " + TF;
    }
    
}
