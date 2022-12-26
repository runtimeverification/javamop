package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.HashMap;

//class representing an And node in an LTL formula
public class Or extends LTLFormula {
    
    public Or(ArrayList<LTLFormula> children){
        assert children != null && children.size() >= 2 
        : "Or requires at least two children!";
        this.children = children;
    }
    
    public LTLType getLTLType(){ 
        return LTLType.OR;
    }
    
    /**
     * This method inlines nested OR nodes,
     * creating a flat list of ORS, making
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
                if(child.getLTLType() == LTLType.OR){
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
            //if a child is true the whole thing
            //reduces to true
            if(children.get(i) == True.get()){
                return True.get();
            }
            //Falses can be removed
            if(children.get(i) == False.get()){
                children.remove(i);
                --i; continue;
            }
            //If we have a and not a we can reduce
            //the whole formula to true
            if(children.get(i).getLTLType() == LTLType.A){
                for(int j = i; j < children.size(); ++j){
                    LTLFormula child = children.get(j);
                    if(child.getLTLType().compareTo(LTLType.NEG) > 0){
                        break;
                    }
                    if(child.getLTLType() == LTLType.NEG
                        && child.getChildren().get(0) == children.get(i)){
                        return True.get();
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
    
    //simple helper to remove clutter
    private LTLFormula getNegation(int i, boolean b){
        return children.get(i).normalize(b);
    }
    
    protected LTLFormula normalize(boolean b) {
        if(b) {
            flatten();
            //because b is true we are below a negation
            //negate all the children and create a 
            //new And containing the negated children
            //(And is the dual of Or)
            for(int i = 0; i < children.size(); ++i){
                children.set(i, getNegation(i, true));
            }
            return new And(children);
        }
        else {
            for(int i = 0; i < children.size(); ++i){
                children.set(i, getNegation(i, false));
            }
            return this;
        }
    }
    
    public LTLFormula copy() {
        ArrayList<LTLFormula> copiedChildren = new ArrayList<LTLFormula>(children.size());
        for(LTLFormula child : children){
            copiedChildren.add(child.copy());
        }
        return new Or(copiedChildren);
    }
    
    public LinkedHashSet<LinkedHashSet<LTLFormula>> toSetForm(){
        LinkedHashSet<LinkedHashSet<LTLFormula>> ret 
        = new LinkedHashSet<LinkedHashSet<LTLFormula>>();
        for(LTLFormula child : children){
            ret.addAll(child.toSetForm());
        }
        return ret;
    }
    
    public ATransition d(HashMap<LTLFormula, ATransition> D){
        ATransition ret = D.get(children.get(0));
        for(int i = 1; i < children.size(); ++i){
            ret = ret.or(D.get(children.get(i)));
        }
        return ret;
    }
    
    
    public static void main(String args[]){
        ArrayList<LTLFormula> c1 = new ArrayList<LTLFormula>(10);
        ArrayList<LTLFormula> c2 = new ArrayList<LTLFormula>(10);
        ArrayList<LTLFormula> c3 = new ArrayList<LTLFormula>(10);
        ArrayList<LTLFormula> c4 = new ArrayList<LTLFormula>(10);
        c1.add(Atom.get("a"));
        c1.add(Atom.get("b"));
        LTLFormula a = new Or(c1);
        c2.addAll(c1);
        c2.add(a);
        LTLFormula b = new Or(c2);
        c3.addAll(c2);
        c3.add(b);
        c3.add(b);
        c4.add(new Negation(new Or(c3)));
        Or f = new Or(c4);
        System.out.println(f);
        f.flatten();
        System.out.println(f);
        System.out.println(f.reduce());
        
        c1 = new ArrayList<LTLFormula>(10);
        c2 = new ArrayList<LTLFormula>(10);
        c1.add(Atom.get("a"));
        c1.add(Atom.get("b"));
        c2.add(Atom.get("a"));
        c2.add(Atom.get("b"));
        
        System.out.println((new Or(c1)).hashCode());
        System.out.println((new Or(c2)).hashCode());
    }
}
