package com.runtimeverification.rvmonitor.logicrepository.plugins.fsm;

import com.runtimeverification.rvmonitor.logicrepository.PluginHelper;

import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.PropertyType;

import org.junit.Test;
import static org.junit.Assert.*;

public class PluginTest {
    
    /**
     * 
     */
    @Test
    public void testHasNext() throws Exception {
        LogicRepositoryType input = new LogicRepositoryType();
        input.setClient("testing");
        input.setEvents("hasnext next");
        input.setCategories("match");
        
        PropertyType property = new PropertyType();
        property.setLogic("fsm");
        property.setFormula("start [\n"+
            "    next -> unsafe\n"+
            "    hasnext -> safe\n"+
            "]\n"+
            "safe [\n"+
            "    next -> start\n"+
            "    hasnext -> safe\n"+
            "]\n"+
            "unsafe [\n"+
            "    next -> unsafe\n"+
            "    hasnext -> safe\n"+
            "]\n"+
            "\n"+
            "alias match = unsafe\n");
        input.setProperty(property);
        
        LogicRepositoryType output = PluginHelper.runLogicPlugin("fsm", input);
        
        assertEquals("fsm", output.getProperty().getLogic());
        assertEquals("done", output.getMessage().get(output.getMessage().size()-1));
        assertTrue(output.getCreationEvents().contains("hasnext"));
        assertTrue(output.getCreationEvents().contains("next"));
        
        String enableSets = output.getEnableSets();
        String matchEnables = enableSets.substring(0, enableSets.indexOf("// match Coenables"));
        String matchCoenables = enableSets.substring(enableSets.indexOf("// match Coenables"));
        
        assertTrue(matchEnables.contains("[]"));
        assertTrue(matchEnables.contains("[next]"));
        assertTrue(matchEnables.contains("[hasnext]"));
        assertTrue(matchEnables.contains("[next, hasnext]") || matchEnables.contains("[hasnext, next]"));
        
        assertFalse(matchCoenables.contains("[]"));
        assertTrue(matchCoenables.contains("[next]"));
        assertFalse(matchCoenables.contains("[hasnext]"));
        assertTrue(matchCoenables.contains("[next, hasnext]") || matchCoenables.contains("[hasnext, next]"));
        
        System.out.println(enableSets);
    }
}