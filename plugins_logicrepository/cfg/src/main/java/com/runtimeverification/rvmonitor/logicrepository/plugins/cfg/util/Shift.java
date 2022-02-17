package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util;

/**
 * A shift action in a LR parser, targeting another state.
 */
public class Shift extends LRAction {
    private int target;
    
    /**
     * Construct a shifting action targeting another state.
     * @param t The index of the target state after shifting the symbol.
     */
    public Shift(int t) { 
        target = t;
    }
    
    @Override
    public int hashCode() { 
        return target;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Shift)) {
            return false;
        }
        return target == (((Shift)o).target);
    }
    
    @Override
    public String toString() { 
        return "Shift "+target;
    }
    
    @Override
    public ActType type() { 
        return ActType.SHIFT;
    }
    
    /**
     * The target state after shifting a symbol.
     * @return The index of the target state.
     */
    public int getTarget() {
        return target;
    }
}
