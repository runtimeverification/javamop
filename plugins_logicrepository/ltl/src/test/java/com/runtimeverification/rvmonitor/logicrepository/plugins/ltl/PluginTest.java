package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import com.runtimeverification.rvmonitor.logicrepository.PluginHelper;

import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.PropertyType;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests invocation of LTL Logic Repository plugin.
 */
public class PluginTest {
    
    /**
     * Use the LTL plugin to produce a Finite State Machine testing a HasNext property.
     */
    @Test
    public void testCompletePluginHasNext() throws Exception {
        LogicRepositoryType input = new LogicRepositoryType();
        input.setClient("testing");
        input.setEvents("next hasnexttrue hasnextfalse");
        input.setProperty(new PropertyType());
        input.getProperty().setLogic("ltl");
        input.getProperty().setFormula("[](next => (*) hasnexttrue)");
        
        LogicRepositoryType output = PluginHelper.runLogicPlugin("ltl", input);
        
        assertEquals("testing", output.getClient());
        assertEquals("fsm", output.getProperty().getLogic());
        String formula = output.getProperty().getFormula();
        
        /*
         * The states can have any names, so we try to test some things in a way unaffected
         * by the shuffling.
         */
        if(formula.contains("hasnexttrue -> s0")) {
            assertFalse(formula.contains("hasnextfalse -> s0"));
            assertFalse(formula.contains("hasnexttrue -> s1"));
            assertFalse(formula.contains("hasnexttrue -> s2"));
        }
        if(formula.contains("hasnexttrue -> s1")) {
            assertFalse(formula.contains("hasnexttrue -> s0"));
            assertFalse(formula.contains("hasnextfalse -> s1"));
            assertFalse(formula.contains("hasnexttrue -> s2"));
        }
        
        if(formula.contains("hasnexttrue -> s2")) {
            assertFalse(formula.contains("hasnexttrue -> s0"));
            assertFalse(formula.contains("hasnexttrue -> s1"));
            assertFalse(formula.contains("hasnextfalse -> s2"));
        }
        if(formula.contains("hasnextfalse -> s0")) {
            assertFalse(formula.contains("hasnexttrue -> s0"));
            assertFalse(formula.contains("hasnextfalse -> s1"));
            assertFalse(formula.contains("hasnextfalse -> s2"));
        }
        if(formula.contains("hasnextfalse -> s1")) {
            assertFalse(formula.contains("hasnextfalse -> s0"));
            assertFalse(formula.contains("hasnexttrue -> s1"));
            assertFalse(formula.contains("hasnextfalse -> s2"));
        }
        if(formula.contains("hasnextfalse -> s2")) {
            assertFalse(formula.contains("hasnextfalse -> s0"));
            assertFalse(formula.contains("hasnextfalse -> s1"));
            assertFalse(formula.contains("hasnexttrue -> s2"));
        }
        if(formula.contains("default s0")) {
            assertFalse(formula.contains("default s1"));
            assertFalse(formula.contains("default s2"));
        }
        if(formula.contains("default s1")) {
            assertFalse(formula.contains("default s0"));
            assertFalse(formula.contains("default s2"));
        }
        if(formula.contains("default s2")) {
            assertFalse(formula.contains("default s0"));
            assertFalse(formula.contains("default s1"));
        }
        assertTrue(formula.contains("next -> violation"));
    }
}
