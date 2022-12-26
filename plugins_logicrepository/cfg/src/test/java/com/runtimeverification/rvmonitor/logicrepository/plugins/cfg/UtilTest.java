package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg;

import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util.Terminal;
import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util.Util;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test the utility methods used in the CFG plugin.
 */
public class UtilTest {
    
    /**
     * Test calculating power sets.
     */
    @Test
    public void testPowerSet() {
        HashSet<String> set = new HashSet<String>();
        set.add("a");
        set.add("b");
        
        HashSet<HashSet<String>> powerSet = Util.powerSet(set);
        assertEquals(4, powerSet.size());
        
        assertTrue(powerSet.contains(new HashSet<String>()));
        assertTrue(powerSet.contains(new HashSet<String>(Arrays.asList("a"))));
        assertTrue(powerSet.contains(new HashSet<String>(Arrays.asList("b"))));
        assertTrue(powerSet.contains(new HashSet<String>(Arrays.asList("a", "b"))));
        
        set.add("c");
        powerSet = Util.powerSet(set);
        assertEquals(8, powerSet.size());
        
        assertTrue(powerSet.contains(new HashSet<String>()));
        assertTrue(powerSet.contains(new HashSet<String>(Arrays.asList("a"))));
        assertTrue(powerSet.contains(new HashSet<String>(Arrays.asList("b"))));
        assertTrue(powerSet.contains(new HashSet<String>(Arrays.asList("c"))));
        assertTrue(powerSet.contains(new HashSet<String>(Arrays.asList("a", "b"))));
        assertTrue(powerSet.contains(new HashSet<String>(Arrays.asList("a", "c"))));
        assertTrue(powerSet.contains(new HashSet<String>(Arrays.asList("b", "c"))));
        assertTrue(powerSet.contains(new HashSet<String>(Arrays.asList("a", "b", "c"))));
    }
    
    /**
     * Test splitting ArrayLists into fragments of increasing size.
     */
    @Test
    public void testInits() {
        ArrayList<String> strings = new ArrayList<String>(Arrays.asList("a", "b", "c"));
        HashSet<ArrayList<String>> inits = Util.inits(strings);
        assertEquals(4, inits.size());
        
        assertTrue(inits.contains(new ArrayList<String>()));
        assertTrue(inits.contains(new ArrayList<String>(Arrays.asList("a"))));
        assertTrue(inits.contains(new ArrayList<String>(Arrays.asList("a", "b"))));
        assertTrue(inits.contains(new ArrayList<String>(Arrays.asList("a", "b", "c"))));
    }
    
    /**
     * Test splitting Arrays into fragments of increasing size, with the empty ArrayList missing.
     */
    @Test
    public void testNeinits() {
        ArrayList<String> strings = new ArrayList<String>(Arrays.asList("a", "b", "c"));
        HashSet<ArrayList<String>> neinits = Util.neinits(strings);
        assertEquals(3, neinits.size());
        
        assertTrue(neinits.contains(new ArrayList<String>(Arrays.asList("a"))));
        assertTrue(neinits.contains(new ArrayList<String>(Arrays.asList("a", "b"))));
        assertTrue(neinits.contains(new ArrayList<String>(Arrays.asList("a", "b", "c"))));
    }
    
    /**
     * Test creating singleton ArrayLists and HashSets.
     */
    @Test
    public void testSingletons() {
        ArrayList<String> singletonArrayList = Util.singletonAL("a");
        assertEquals(1, singletonArrayList.size());
        assertEquals("a", singletonArrayList.get(0));
        
        HashSet<String> singletonHashSet = Util.singletonHS("a");
        assertEquals(1, singletonHashSet.size());
        assertTrue(singletonHashSet.contains("a"));
    }
    
    /**
     * Test popping off members of an ArrayList.
     */
    @Test
    public void testPop() {
        ArrayList<String> strings = new ArrayList<String>();
        
        assertEquals(null, Util.popl(strings));
        
        strings.add("a");
        strings.add("b");
        
        assertEquals(Util.singletonAL("b"), Util.popl(strings));
        assertEquals(Util.singletonAL("a"), Util.popl(strings));
        assertEquals(null, Util.popl(strings));
    }
    
    /**
     * Test producing the union of two sets.
     */
    @Test
    public void testUnion() {
        HashSet<Terminal> setAB = new HashSet<Terminal>(Arrays.asList(new Terminal("a"), new Terminal("b")));
        HashSet<Terminal> setBC = new HashSet<Terminal>(Arrays.asList(new Terminal("b"), new Terminal("c")));
        
        HashSet<Terminal> setABC = Util.termUnion(setAB, setBC);
        assertEquals(3, setABC.size());
        assertTrue(setABC.contains(new Terminal("a")));
        assertTrue(setABC.contains(new Terminal("b")));
        assertTrue(setABC.contains(new Terminal("c")));
        
        setABC = Util.union(setAB, setBC);
        assertEquals(3, setABC.size());
        assertTrue(setABC.contains(new Terminal("a")));
        assertTrue(setABC.contains(new Terminal("b")));
        assertTrue(setABC.contains(new Terminal("c")));
    }
    
    /**
     * Test getting the members in an ArrayList before a specific element.
     */
    @Test
    public void testGetBefore() {
        ArrayList<String> strings = new ArrayList<String>(Arrays.asList("a", "b", "c", "d"));
        
        assertEquals(null, Util.getBefore(strings, "z"));
        
        ArrayList<String> beforeC = Util.getBefore(strings, "c");
        assertEquals(2, beforeC.size());
        assertEquals("a", beforeC.get(0));
        assertEquals("b", beforeC.get(1));
    }
    
    /**
     * Test getting the set of ArrayLists of members in an ArrayList before all instances of a specific element.
     */
    @Test
    public void testGetBeforeS() {
        ArrayList<String> strings = new ArrayList<String>(Arrays.asList("a", "c", "b", "c"));
        
        assertEquals(null, Util.getBeforeS(strings, "z"));
        
        HashSet<ArrayList<String>> beforeC_set = Util.getBeforeS(strings, "c");
        assertEquals(2, beforeC_set.size());
        assertTrue(beforeC_set.contains(new ArrayList<String>(Arrays.asList("a"))));
        assertTrue(beforeC_set.contains(new ArrayList<String>(Arrays.asList("a", "c", "b"))));
    }
}
