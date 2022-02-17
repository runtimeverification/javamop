package com.runtimeverification.rvmonitor.logicrepository.plugins.ptltl;

import com.runtimeverification.rvmonitor.logicrepository.PluginHelper;

import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.PropertyType;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests invocation of the complete PTLTL Logic Repository plugin.
 * Verifies that the complete PTLTL plugin can be invoked through the normal plugin mechanism
 * and that it produces reasonable output.
 * @author A. Cody Schuffelen
 */
public class PluginTest {
    
    /**
     * Tests code generation for PTLTL code containing a HasNext property.
     */
    @Test
    public void testCompletePluginHasNext() throws Exception {
        LogicRepositoryType input = new LogicRepositoryType();
        input.setClient("testing");
        input.setProperty(new PropertyType());
        input.getProperty().setLogic("ptltl");
        input.getProperty().setFormula("next => (*) hasnext");
        
        LogicRepositoryType output = PluginHelper.runLogicPlugin("ptltl", input);
        
        assertEquals("testing", output.getClient());
        assertEquals("fsm", output.getProperty().getLogic());
        
        String formula = output.getProperty().getFormula();
        assertTrue(formula.contains("n0"));
        assertTrue(formula.contains("n1"));
        assertTrue(formula.contains("n2"));
        assertFalse(formula.contains("n3"));
        
        String n0 = formula.substring(formula.indexOf("n0["), formula.indexOf("]\nn1"));
        assertTrue(n0.contains("next -> n1,"));
        assertTrue(n0.contains("hasnext -> n2,"));
        assertTrue(n0.contains("default n0"));
        
        String n1 = formula.substring(formula.indexOf("n1["), formula.indexOf("]\nn2"));
        assertTrue(n1.contains("next -> n1,"));
        assertTrue(n1.contains("hasnext -> n2,"));
        assertTrue(n1.contains("default n0"));
        
        String n2 = formula.substring(formula.indexOf("n2["));
        assertTrue(n2.contains("next -> n0,"));
        assertTrue(n2.contains("hasnext -> n2,"));
        assertTrue(n2.contains("default n0"));
        
        assertTrue(formula.contains("alias violation = n1"));
        assertTrue(formula.contains("alias validation = n0 n2"));
    }
    
    /**
     * Use the PTLTL plugin to test a SafeCollection property.
     */
    @Test
    public void testSafeCollection() throws Exception {
        LogicRepositoryType input = new LogicRepositoryType();
        input.setClient("testing");
        input.setProperty(new PropertyType());
        input.getProperty().setLogic("ptltl");
        input.getProperty().setFormula("next and (<*> (updatesource and (<*> (next and (<*> create)))))");
        
        LogicRepositoryType output = PluginHelper.runLogicPlugin("ptltl", input);
        
        assertEquals("testing", output.getClient());
        assertEquals("fsm", output.getProperty().getLogic());
        
        String formula = output.getProperty().getFormula();
        assertTrue(formula.contains("n0"));
        assertTrue(formula.contains("n1"));
        assertTrue(formula.contains("n2"));
        assertTrue(formula.contains("n3"));
        assertTrue(formula.contains("n4"));
        assertFalse(formula.contains("n5"));
        
        String n0 = formula.substring(formula.indexOf("n0["), formula.indexOf("]\nn1"));
        assertTrue(n0.contains("next -> n0,"));
        assertTrue(n0.contains("updatesource -> n0,"));
        assertTrue(n0.contains("create -> n1,"));
        assertTrue(n0.contains("default n0"));
        
        String n1 = formula.substring(formula.indexOf("n1["), formula.indexOf("]\nn2"));
        assertTrue(n1.contains("next -> n2,"));
        assertTrue(n1.contains("updatesource -> n1,"));
        assertTrue(n1.contains("create -> n1,"));
        assertTrue(n1.contains("default n1"));
        
        String n2 = formula.substring(formula.indexOf("n2["), formula.indexOf("]\nn3"));
        assertTrue(n2.contains("next -> n2,"));
        assertTrue(n2.contains("updatesource -> n3,"));
        assertTrue(n2.contains("create -> n2,"));
        assertTrue(n2.contains("default n2"));
        
        String n3 = formula.substring(formula.indexOf("n3["), formula.indexOf("]\nn4"));
        assertTrue(n3.contains("next -> n4,"));
        assertTrue(n3.contains("updatesource -> n3,"));
        assertTrue(n3.contains("create -> n3,"));
        assertTrue(n3.contains("default n3"));
        
        String n4 = formula.substring(formula.indexOf("n4["));
        assertTrue(n4.contains("next -> n4,"));
        assertTrue(n4.contains("updatesource -> n3,"));
        assertTrue(n4.contains("create -> n3,"));
        assertTrue(n4.contains("default n3"));
        
        assertTrue(formula.contains("alias violation = n0 n1 n2 n3"));
        assertTrue(formula.contains("alias validation = n4"));
    }
    
    /**
     * Uses the PTLTL plugin to test a car property.
     */
    @Test
    public void testComplexCar() throws Exception {
        LogicRepositoryType input = new LogicRepositoryType();
        input.setClient("testing");
        input.setProperty(new PropertyType());
        input.getProperty().setLogic("ptltl");
        input.getProperty().setFormula("(not on S off) and (not manual S auto) and (not stop S start)");
        
        LogicRepositoryType output = PluginHelper.runLogicPlugin("ptltl", input);
        assertEquals("testing", output.getClient());
        assertEquals("fsm", output.getProperty().getLogic());
        
        String formula = output.getProperty().getFormula();
        System.out.println(formula);
        assertTrue(formula.contains("n0"));
        assertTrue(formula.contains("n1"));
        assertTrue(formula.contains("n2"));
        assertTrue(formula.contains("n3"));
        assertTrue(formula.contains("n4"));
        assertTrue(formula.contains("n5"));
        assertTrue(formula.contains("n6"));
        assertTrue(formula.contains("n7"));
        assertFalse(formula.contains("n8"));
        
        String n0 = formula.substring(formula.indexOf("n0["), formula.indexOf("]\nn1"));
        assertTrue(n0.contains("on -> n0,"));
        assertTrue(n0.contains("off -> n1,"));
        assertTrue(n0.contains("manual -> n0,"));
        assertTrue(n0.contains("auto -> n2,"));
        assertTrue(n0.contains("stop -> n0,"));
        assertTrue(n0.contains("start -> n3,"));
        assertTrue(n0.contains("default n0"));
        
        String n1 = formula.substring(formula.indexOf("n1["), formula.indexOf("]\nn2"));
        assertTrue(n1.contains("on -> n0,"));
        assertTrue(n1.contains("on -> n0,"));
        assertTrue(n1.contains("manual -> n1,"));
        assertTrue(n1.contains("auto -> n4,"));
        assertTrue(n1.contains("stop -> n1,"));
        assertTrue(n1.contains("start -> n5,"));
        assertTrue(n1.contains("default n1"));
        
        String n2 = formula.substring(formula.indexOf("n2["), formula.indexOf("]\nn3"));
        assertTrue(n2.contains("on -> n2,"));
        assertTrue(n2.contains("off -> n4,"));
        assertTrue(n2.contains("manual -> n0,"));
        assertTrue(n2.contains("auto -> n2,"));
        assertTrue(n2.contains("stop -> n2,"));
        assertTrue(n2.contains("start -> n6,"));
        assertTrue(n2.contains("default n2"));
        
        String n3 = formula.substring(formula.indexOf("n3["), formula.indexOf("]\nn4"));
        assertTrue(n3.contains("on -> n3,"));
        assertTrue(n3.contains("off -> n5,"));
        assertTrue(n3.contains("manual -> n3,"));
        assertTrue(n3.contains("auto -> n6,"));
        assertTrue(n3.contains("stop -> n0,"));
        assertTrue(n3.contains("start -> n3,"));
        assertTrue(n3.contains("default n3"));
        
        String n4 = formula.substring(formula.indexOf("n4["), formula.indexOf("]\nn5"));
        assertTrue(n4.contains("on -> n2,"));
        assertTrue(n4.contains("off -> n4,"));
        assertTrue(n4.contains("manual -> n1,"));
        assertTrue(n4.contains("auto -> n4,"));
        assertTrue(n4.contains("stop -> n4,"));
        assertTrue(n4.contains("start -> n7,"));
        assertTrue(n4.contains("default n4"));
        
        String n5 = formula.substring(formula.indexOf("n5["), formula.indexOf("]\nn6"));
        assertTrue(n5.contains("on -> n3,"));
        assertTrue(n5.contains("off -> n5,"));
        assertTrue(n5.contains("manual -> n5,"));
        assertTrue(n5.contains("auto -> n7,"));
        assertTrue(n5.contains("stop -> n1,"));
        assertTrue(n5.contains("start -> n5,"));
        assertTrue(n5.contains("default n5"));
        
        String n6 = formula.substring(formula.indexOf("n6["), formula.indexOf("]\nn7"));
        assertTrue(n6.contains("on -> n6,"));
        assertTrue(n6.contains("off -> n7,"));
        assertTrue(n6.contains("manual -> n3,"));
        assertTrue(n6.contains("auto -> n6,"));
        assertTrue(n6.contains("stop -> n2,"));
        assertTrue(n6.contains("start -> n6,"));
        assertTrue(n6.contains("default n6"));
        
        String n7 = formula.substring(formula.indexOf("n7["));
        assertTrue(n7.contains("on -> n6,"));
        assertTrue(n7.contains("off -> n7,"));
        assertTrue(n7.contains("manual -> n5,"));
        assertTrue(n7.contains("auto -> n7,"));
        assertTrue(n7.contains("stop -> n4,"));
        assertTrue(n7.contains("start -> n7,"));
        assertTrue(n7.contains("default n7"));
        
        assertTrue(formula.contains("alias violation = n0 n1 n2 n3 n4 n5 n6"));
        assertTrue(formula.contains("alias validation = n7"));
    }
    
}
