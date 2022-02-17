package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.HashMap;

/**
 * class representing an And node in an LTL formula
 */
public class And extends LTLFormula {
    
    /**
     * Construct an And element.
     * @param children The children of the And element.
     */
    public And(ArrayList<LTLFormula> children){
        assert children != null && children.size() >= 2 
        : "And requires at least two children!";
        this.children = children;
    }
    
    @Override
    public LTLType getLTLType(){ 
        return LTLType.AND;
    }
    
    /**
     * This method inlines nested AND nodes,
     * creating a flat list of ANDS, making
     * reduction MUCH easier
     */
    private void flatten(){
        ArrayList<LTLFormula> flattened;
        ArrayList<LTLFormula> previous = children;
        boolean changed;
        do {
            changed = false;
            flattened = new ArrayList<LTLFormula>(children.size() >> 1);
            for(LTLFormula child : previous){
                if(child.getLTLType() == LTLType.AND){
                    flattened.addAll(child.getChildren());
                    changed = true;
                } else {
                    flattened.add(child); 
                }
            }
            previous = flattened;
        } while(changed);
        children = flattened;
    }
    
    @Override
    protected LTLFormula reduce(){
        //first reduce all children
        for(int i = 0; i < children.size(); ++i){
            children.set(i, children.get(i).reduce());
        }
        //first pass, perform any inlining
        flatten();
        //now sort
        Collections.sort(children);
        //the rest of this method must preserve sorting
        for(int i = 0; ; ++i){
            //because we are removing elements the
            //size may change during computation
            if(i >= children.size()) return this;
            //if a child is false the whole thing
            //reduces to false
            if(children.get(i) == False.get()){
                return False.get();
            }
            //Trues can be removed
            if(children.get(i) == True.get()){
                children.remove(i);
                --i; continue;
            }
            //If we have a and not a we can reduce
            //the whole formula to false
            if(children.get(i).getLTLType() == LTLType.A){
                for(int j = i; j < children.size(); ++j){
                    LTLFormula child = children.get(j);
                    if(child.getLTLType().compareTo(LTLType.NEG) > 0){
                        break;
                    }
                    if(child.getLTLType() == LTLType.NEG
                        && child.getChildren().get(0) == children.get(i)){
                        return False.get();
                        }
                }
            }
            //duplicate children can be removed
            //keep in mind that the children are sorted
            //so duplicates must be adjacent
            if(i + 1 < children.size() 
                && children.get(i).equals(children.get(i + 1))){
                children.remove(i);
            --i;
                }
                if(children.size() == 1) return children.get(0);
        }
    }
    
    /**
     * simple helper to remove clutter
     */
    private LTLFormula getNegation(int i, boolean b){
        return children.get(i).normalize(b);
    }
    
    @Override
    protected LTLFormula normalize(boolean b) {
        if(b) {
            flatten();
            //because b is true we are below a negation
            //negate all the children and create a 
            //new Or containing the negated children
            //(Or is the dual of And)
            for(int i = 0; i < children.size(); ++i){
                children.set(i, getNegation(i, true));
            }
            return new Or(children);
        }
        else {
            for(int i = 0; i < children.size(); ++i){
                children.set(i, getNegation(i, false));
            }
            return this;
        }
    }
    
    @Override
    public LTLFormula copy() {
        ArrayList<LTLFormula> copiedChildren = new ArrayList<LTLFormula>(children.size());
        for(LTLFormula child : children){
            copiedChildren.add(child.copy());
        }
        return new And(copiedChildren);
    }
    
    /**
     * 
     */
    private static LinkedHashSet<LTLFormula> copySet(LinkedHashSet<LTLFormula> set){
        LinkedHashSet<LTLFormula> ret = new LinkedHashSet<LTLFormula>(set.size());
        //this is a shallow copy, we are no longer manipulating formulae,
        //so there is no reason to copy them, sharing is fine
        //eventually we may want to use an integer encoding for formulae,
        //though I think this will all be more than efficient enough
        for(LTLFormula f : set){
            ret.add(f);
        }
        return ret;
    }
    
    /**
     * 
     */
    private static LinkedHashSet<LinkedHashSet<LTLFormula>> 
    innerUnion(LinkedHashSet<LinkedHashSet<LTLFormula>> first, 
               LinkedHashSet<LinkedHashSet<LTLFormula>> second){
        //unfortuntely Java's exceptions mean that we must create
        //a completely new set even though we could easily reuse
        //one of them
        LinkedHashSet<LinkedHashSet<LTLFormula>> ret 
        =  new LinkedHashSet<LinkedHashSet<LTLFormula>>(first.size() 
        * second.size());
        for(LinkedHashSet<LTLFormula> firstInner : first){
            LinkedHashSet<LTLFormula> copy; 
            for(LinkedHashSet<LTLFormula> secondInner : second){
                copy = copySet(firstInner);
                copy.addAll(secondInner);
                ret.add(copy);
            }
        }
        return ret;
    }
    
    @Override
    public LinkedHashSet<LinkedHashSet<LTLFormula>> toSetForm(){
        LinkedHashSet<LinkedHashSet<LTLFormula>> ret
        = new LinkedHashSet<LinkedHashSet<LTLFormula>>(1); 
        LinkedHashSet<LTLFormula> retInner 
        = new LinkedHashSet<LTLFormula>(0);
        ret.add(retInner);
        for(LTLFormula child : children){
            ret = innerUnion(ret, child.toSetForm());
        }
        return ret;
    }
    
    @Override
    public ATransition d(HashMap<LTLFormula, ATransition> D){
        ATransition ret = D.get(children.get(0));
        for(int i = 1; i < children.size(); ++i){
            ret = ret.and(D.get(children.get(i)));
        }
        return ret;
    }
    
    
    public static void main(String args[]){
        LinkedHashSet<LinkedHashSet<LTLFormula>> one 
        = new LinkedHashSet<LinkedHashSet<LTLFormula>> ();
        LinkedHashSet<LinkedHashSet<LTLFormula>> two 
        = new LinkedHashSet<LinkedHashSet<LTLFormula>> ();
        
        LinkedHashSet<LTLFormula> in1 = new LinkedHashSet<LTLFormula>();
        LinkedHashSet<LTLFormula> in2 = new LinkedHashSet<LTLFormula>();
        LinkedHashSet<LTLFormula> in3 = new LinkedHashSet<LTLFormula>();
        LinkedHashSet<LTLFormula> in4 = new LinkedHashSet<LTLFormula>();
        
        in1.add(Atom.get("1"));
        in1.add(Atom.get("1a"));
        in2.add(Atom.get("2"));
        in2.add(Atom.get("2a"));
        in3.add(Atom.get("3"));
        in4.add(Atom.get("4"));
        
        one.add(in1);
        one.add(in2);
        two.add(in3);
        two.add(in4);
        
        System.out.println(one);
        System.out.println(two);
        System.out.println(innerUnion(one,two));
    }
}
