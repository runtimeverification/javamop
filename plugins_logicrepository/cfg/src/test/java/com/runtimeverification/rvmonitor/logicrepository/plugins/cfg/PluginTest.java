package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg;

import com.runtimeverification.rvmonitor.logicrepository.PluginHelper;

import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.PropertyType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests invocation of the complete CFG Logic Repository plugin.
 * Verifies that the complete CFG plugin can be invoked through the normal plugin mechanism
 * and that it produces reasonable output.
 * @author A. Cody Schuffelen
 */
public class PluginTest {
    
    /**
     * A list of comma-separated strings containing the permutations of the input strings.
     * @param strings The strings to permute.
     * @return The permutations as comma-separated strings.
     */
    private List<String> generatePermutations(List<String> strings) {
        if(strings.size() == 0) {
            return new ArrayList<String>();
        } else if(strings.size() == 1) {
            return strings;
        }
        ArrayList<String> permutations = new ArrayList<String>();
        for(String str : strings) {
            List<String> withoutStr = new ArrayList<String>(strings);
            withoutStr.remove(str);
            List<String> innerPermutations = generatePermutations(withoutStr);
            for(String innerPermutation : innerPermutations) {
                permutations.add(str + ", " + innerPermutation);
            }
        }
        return permutations;
    }
    
    /**
     * Whether a string contains a permutation of some number of strings when separated by
     * commas.
     * @param search The string to search.
     * @param strings The strings to permute.
     * @return If {@code search} contains a permutation of {@code strings}.
     */
    private boolean containsPermutationOf(String search, String... strings) {
        List<String> permutations = generatePermutations(Arrays.asList(strings));
        for(String permutation : permutations) {
            if(search.contains("[" + permutation + "]")) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Tests code generation for CFG code containing a Safe File property.
     */
    @Test
    public void testCompletePluginSafeFile() throws Exception {
        LogicRepositoryType input = new LogicRepositoryType();
        input.setClient("testing");
        input.setEvents("beginCall endCall open close");
        input.setCategories("fail");
        input.setProperty(new PropertyType());
        input.getProperty().setLogic("cfg");
        input.getProperty().setFormula(
            "S -> A S | epsilon,\n" +
            "A -> A beginCall A endCall | A open A close | epsilon\n"
        );
        
        LogicRepositoryType output = PluginHelper.runLogicPlugin("cfg", input);
        
        assertEquals("testing", output.getClient());
        assertEquals("done", output.getMessage().get(output.getMessage().size() - 1));
        assertEquals("cfg", output.getProperty().getLogic());
        
        String powerSet = "[[open, close, beginCall], [], [endCall, close], [close, beginCall], " +
        "[endCall], [endCall, beginCall, close], [close], [endCall, open], [open], " +
        "[open, endCall, close, beginCall], [close, open], [beginCall, open], " +
        "[open, endCall, close], [open, endCall, beginCall], [beginCall], [endCall, beginCall]]";
        
        String enableSets = output.getEnableSets();
        
        assertTrue(enableSets.contains("[]"));
        assertTrue(containsPermutationOf(enableSets, "open"));
        assertTrue(containsPermutationOf(enableSets, "open", "close"));
        assertTrue(containsPermutationOf(enableSets, "open", "beginCall"));
        assertTrue(containsPermutationOf(enableSets, "open", "endCall"));
        assertTrue(containsPermutationOf(enableSets, "open", "beginCall", "endCall"));
        assertTrue(containsPermutationOf(enableSets, "open", "close", "beginCall"));
        assertTrue(containsPermutationOf(enableSets, "open", "close", "endCall"));
        assertTrue(containsPermutationOf(enableSets, "open", "close", "beginCall", "endCall"));
        assertTrue(containsPermutationOf(enableSets, "close"));
        assertTrue(containsPermutationOf(enableSets, "close", "beginCall"));
        assertTrue(containsPermutationOf(enableSets, "close", "endCall"));
        assertTrue(containsPermutationOf(enableSets, "close", "beginCall", "endCall"));
        assertTrue(containsPermutationOf(enableSets, "beginCall"));
        assertTrue(containsPermutationOf(enableSets, "beginCall", "endCall"));
        assertTrue(containsPermutationOf(enableSets, "endCall"));
        
        assertEquals(input.getEvents(), output.getCreationEvents());
    }
    
    /**
     * Test code generation for CFG code containing a HasNext property.
     */
    @Test
    public void testCompletePluginHasNext() throws Exception {
        LogicRepositoryType input = new LogicRepositoryType();
        input.setClient("testing");
        input.setEvents("hasnext next");
        input.setCategories("fail");
        input.setProperty(new PropertyType());
        input.getProperty().setLogic("cfg");
        input.getProperty().setFormula("S -> next next");
        
        LogicRepositoryType output = PluginHelper.runLogicPlugin("cfg", input);
        
        assertEquals("testing", output.getClient());
        assertEquals("done", output.getMessage().get(output.getMessage().size() - 1));
        assertEquals("cfg", output.getProperty().getLogic());
        
        assertTrue(output.getEnableSets().contains("{next=[[], [next]]}"));
        assertEquals(input.getEvents(), output.getCreationEvents());
    }
}