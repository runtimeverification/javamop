package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util;

/**
 * Action taken when the LR parser accepts a complete expression.
 */
public class Accept extends LRAction {
    
    @Override
    public int hashCode() { 
        return 1;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Accept)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "Accept";
    }
    
    @Override
    public ActType type() { 
        return ActType.ACCEPT;
    }
}
