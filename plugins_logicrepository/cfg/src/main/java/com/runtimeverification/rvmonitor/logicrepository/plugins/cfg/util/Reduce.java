package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util;

/**
 * A reduce action, targeting another state.
 */
public class Reduce extends LRAction {
    private int nt;
    private int size;
    
    /**
     * Construct a Reduce action with a given nonterminal and size.
     * @param oldnt The index of the nonterminal symbol representing the reduction.
     * @param oldsize The number of symbols to reduce from the stack.
     */
    public Reduce(int oldnt, int oldsize) {
        nt = oldnt; 
        size = oldsize;
    }
    
    @Override
    public int hashCode() { 
        return nt+size;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Reduce)) {
            return false;
        }
        return nt == (((Reduce)o).nt) && size == (((Reduce)o).size);
    }
    
    @Override
    public String toString() { 
        return "Reduce "+nt +" "+size;
    }
    
    @Override
    public ActType type() {
        return ActType.REDUCE;
    }
    
    /**
     * The nonterminal symbol for this reduction action.
     * @return The index of the nonterminal symbol.
     */
    public int getNt() {
        return nt;
    }
    
    /**
     * The number of symbols to pop off the stack when reducing.
     * @return The number to remove from the stack.
     */
    public int getSize() {
        return size;
    }
}
