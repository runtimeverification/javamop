package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util;

/**
 * Categories of interest in matching an expression against a context-free grammar.
 * Sometimes users want to take an action when it matches the grammar, and sometimes
 * users want to take an action when it fails it.
 */
public enum Category { 
    ACCEPT, 
    UNKNOWN, 
    FAIL
}
