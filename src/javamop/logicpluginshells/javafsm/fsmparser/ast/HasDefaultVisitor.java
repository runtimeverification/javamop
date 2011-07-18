package javamop.logicpluginshells.javafsm.fsmparser.ast;

import java.util.*;

public class HasDefaultVisitor implements GenericVisitor<boolean[], Object> {

    public boolean[] visit(Node n, Object arg){
    	boolean[] ret = new boolean[1];
    	ret[0] = false;
    	return ret;
    }
    
    public boolean[] visit(FSMInput f, Object arg){
    	boolean[] ret = new boolean[1];
    	ret[0] = false;

    	if(f.getItems() != null){
    		for(FSMItem i : f.getItems()){
    			boolean temp[] = i.accept(this, arg);
    			ret[0] = ret[0] || temp[0];
    		}
    	}
    	return ret;
    }
    
    public boolean[] visit(FSMItem i, Object arg){
    	boolean[] ret = new boolean[1];
    	ret[0] = false;
    	
    	if(i.getTransitions() != null){
    		for(FSMTransition t : i.getTransitions()){
    			boolean[] temp = t.accept(this, arg);
    			ret[0] = ret[0] || temp[0];
    		}
    	}

    	return ret;
    }
    
    public boolean[] visit(FSMAlias a, Object arg){
    	boolean[] ret = new boolean[1];
    	ret[0] = false;
    	return ret;
    }
    
    public boolean[] visit(FSMTransition t, Object arg){
    	boolean[] ret = new boolean[1];
    	ret[0] = false;

    	if(t.isDefaultFlag()){
    		ret[0] = true;
    	}
    	return ret;
    }

}
