package com.runtimeverification.rvmonitor.logicrepository.plugins.ere;

import com.runtimeverification.rvmonitor.logicrepository.PluginHelper;

import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.PropertyType;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests invocation of the complete ERE Logic Repository plugin.
 * Verifies that the complete ERE plugin can be invoked through the normal plugin mechanism
 * and that it produces reasonable output.
 * @author A. Cody Schuffelen
 */
public class PluginTest {
    
    /**
     * Tests formula generation for ERE code on a simple property.
     */
    @Test
    public void testCompletePluginHasNext() throws Exception {
        //based on HasNext at http://fsl.cs.illinois.edu/index.php/Special:EREPlugin3
        LogicRepositoryType input = new LogicRepositoryType();
        input.setClient("testing");
        input.setEvents("hasnext next");
        input.setCategories("fail");
        
        PropertyType property = new PropertyType();
        property.setLogic("ere");
        property.setFormula("(hasnext hasnext* next)*");
        input.setProperty(property);
        
        LogicRepositoryType output = PluginHelper.runLogicPlugin("ere", input);
        
        assertEquals("testing", output.getClient());
        assertEquals("hasnext next", output.getEvents());
        String expectedFormula =
        "s0 [" + FSMTest.NEWLINE +
        "   hasnext -> s1" + FSMTest.NEWLINE +
        "]" + FSMTest.NEWLINE +
        "s1 [" + FSMTest.NEWLINE +
        "   hasnext -> s1" + FSMTest.NEWLINE +
        "   next -> s0" + FSMTest.NEWLINE +
        "]" + FSMTest.NEWLINE +
        "alias match = s0 " + FSMTest.NEWLINE;
        assertEquals(expectedFormula, output.getProperty().getFormula());
        assertEquals("fail", output.getCategories());
        
    }
    
}