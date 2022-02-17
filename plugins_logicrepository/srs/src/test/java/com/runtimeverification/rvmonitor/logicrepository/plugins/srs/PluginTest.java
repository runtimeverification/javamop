package com.runtimeverification.rvmonitor.logicrepository.plugins.srs;

import com.runtimeverification.rvmonitor.logicrepository.PluginHelper;

import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.PropertyType;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests invocation of the complete SRS Logic Repository plugin.
 * Verifies that the complete SRS plugin can be invoked through the normal plugin mechanism
 * and that it produces reasonable output.
 * @author A. Cody Schuffelen
 */
public class PluginTest {
    
    /**
     * Tests code generation for SRS code containing a HasNext property.
     */
    @Test
    public void testCompletePluginHasNext() throws Exception {
        LogicRepositoryType input = new LogicRepositoryType();
        input.setClient("testing");
        input.setCategories("fail");
        input.setEvents("hasnexttrue hasnextfalse next");
        input.setProperty(new PropertyType());
        input.getProperty().setLogic("srs");
        input.getProperty().setFormula(
            "hasnexttrue hasnexttrue -> hasnexttrue ." +
            "hasnexttrue next        -> #epsilon . " +
            "next                    -> #fail ."
        );
        
        LogicRepositoryType output = PluginHelper.runLogicPlugin("srs", input);
        
        assertEquals("testing", output.getClient());
        assertEquals("done", output.getMessage().get(output.getMessage().size() - 1));
        
        String formula = output.getProperty().getFormula();
        System.out.println(formula);
        
        /**
         * These get reordered and the order isn't important, so i just test for the lines separately.
         */
        
        String zeroAtZero = formula.substring(formula.indexOf("\n<0 @ 0"), formula.indexOf("\n<1 @ 1"));
        
        assertTrue(zeroAtZero.contains("hasnexttrue -> [0] <1 @ 1>"));
        assertTrue(zeroAtZero.contains("hasnextfalse -> [0] <0 @ 0>"));
        assertTrue(zeroAtZero.contains("next -> [0] <4 @ 1 matches next  -> #fail>"));
        
        String oneAtOne = formula.substring(formula.indexOf("\n<1 @ 1"), formula.indexOf("\n<2 @ 2"));
        
        assertTrue(oneAtOne.contains("hasnexttrue -> [0] <2 @ 2 matches hasnexttrue hasnexttrue  -> hasnexttrue"));
        assertTrue(oneAtOne.contains("hasnextfalse -> [1] <0 @ 0>"));
        assertTrue(oneAtOne.contains("next -> [0] <3 @ 2 matches hasnexttrue next  -> #epsilon"));
        
        String twoAtTwo = formula.substring(formula.indexOf("\n<2 @ 2"), formula.indexOf("\n<3 @ 2"));
        
        assertTrue(twoAtTwo.contains("matches hasnexttrue hasnexttrue  -> hasnexttrue"));
        assertTrue(twoAtTwo.contains("hasnexttrue -> [1] <1 @ 1>"));
        assertTrue(twoAtTwo.contains("hasnextfalse -> [1] <1 @ 1>"));
        assertTrue(twoAtTwo.contains("next -> [1] <1 @ 1>"));
        
        String threeAtTwo = formula.substring(formula.indexOf("\n<3 @ 2"), formula.indexOf("\n<4 @ 1"));
        
        assertTrue(threeAtTwo.contains("matches hasnexttrue next  -> #epsilon"));
        assertTrue(threeAtTwo.contains("hasnexttrue -> [1] <4 @ 1 matches next  -> #fail>"));
        assertTrue(threeAtTwo.contains("hasnextfalse -> [1] <4 @ 1 matches next  -> #fail>"));
        assertTrue(threeAtTwo.contains("next -> [1] <4 @ 1 matches next  -> #fail>"));
        
        String fourAtOne = formula.substring(formula.indexOf("\n<4 @ 1"));
        
        assertTrue(fourAtOne.contains("matches next  -> #fail"));
        assertTrue(fourAtOne.contains("hasnexttrue -> [1] <0 @ 0>"));
        assertTrue(fourAtOne.contains("hasnextfalse -> [1] <0 @ 0>"));
        assertTrue(fourAtOne.contains("next -> [1] <0 @ 0>"));
    }
}