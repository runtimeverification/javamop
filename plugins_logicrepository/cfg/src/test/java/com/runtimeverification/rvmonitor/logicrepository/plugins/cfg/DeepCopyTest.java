package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg;

import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util.DeepCopy;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test code that produces deep copies of arbitrary data structures.
 */
public class DeepCopyTest {
    
    /**
     * Test that deep independent copies can be made from ArrayLists.
     */
    @Test
    public void testNestedArrayLists() {
        ArrayList<ArrayList<String>> arrs = new ArrayList<ArrayList<String>>();
        arrs.add(new ArrayList<String>());
        arrs.add(new ArrayList<String>(Arrays.asList("a")));
        arrs.add(new ArrayList<String>(Arrays.asList("a", "b")));
        
        ArrayList<ArrayList<String>> copy = DeepCopy.copy(arrs);
        assertEquals(arrs, copy);
        for(int i = 0; i < 2; i++) {
            assertEquals(arrs.get(i), copy.get(i));
        }
        
        arrs.get(0).add("c");
        assertFalse(arrs.equals(copy));
        assertFalse(arrs.get(0).equals(copy.get(0)));
    }
    
    /**
     * Test that deep independent copies can be made from HashSets.
     */
    @Test
    public void testNestedHashSets() {
        HashSet<HashSet<String>> sets = new HashSet<HashSet<String>>();
        sets.add(new HashSet<String>());
        sets.add(new HashSet<String>(Arrays.asList("a")));
        sets.add(new HashSet<String>(Arrays.asList("a", "b")));
        
        HashSet<HashSet<String>> copy = DeepCopy.copy(sets);
        assertEquals(sets, copy);
        assertTrue(copy.contains(new HashSet<String>()));
        assertTrue(copy.contains(new HashSet<String>(Arrays.asList("a"))));
        assertTrue(copy.contains(new HashSet<String>(Arrays.asList("a", "b"))));
        
        sets.remove(new HashSet<String>());
        assertFalse(sets.equals(copy));
        assertFalse(sets.contains(new HashSet<String>()));
        assertTrue(copy.contains(new HashSet<String>()));
    }
}