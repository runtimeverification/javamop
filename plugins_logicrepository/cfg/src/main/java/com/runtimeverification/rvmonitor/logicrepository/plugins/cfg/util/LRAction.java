package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util;

/**
 * An action that can be taken as part of a LR parser.
 */
public abstract class LRAction implements java.io.Serializable {
    
    /**
     * Retrieve the type of action in the {@link ActType} enum corresponding to this action.
     */
    public abstract ActType type();
}
