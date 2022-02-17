package com.runtimeverification.rvmonitor.logicrepository.plugins.srs;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test the Splice List internal data structure used in SRS.
 */
public class SpliceListTest {
    
    private SpliceList<String> empty;
    private SpliceList<String> sl;
    
    private SLIterator<String> H;
    private SLIterator<String> T;
    
    /**
     * Initialize a SpliceList and iterators to be shared between the tests.
     */
    @Before
    public void setUp() {
        empty = new SpliceList<String>();
        sl = new SpliceList<String>(new String[] {"a", "b", "c"});
        
        H = sl.head();
        T = sl.tail();
    }
    
    /**
     * Test that the SpliceList is initialized correctly.
     */
    @Test
    public void testInit() {
        assertEquals("a", sl.head().get());
        assertEquals("c", sl.tail().get());
        assertEquals("[a b c]", sl.toString());
        H = sl.head();
        assertEquals("a", H.get());
        assertTrue(H.next());
        assertEquals("b", H.get());
        assertTrue(H.next());
        assertEquals("c", H.get());
        assertFalse(H.next());
        assertEquals("c", H.get());
    }
    
    /**
     * Test that empty lists behave as expected.
     */
    @Test
    public void testEmptyToEmpty() {
        // ========splicing empty to empty========
        H = empty.head();
        T = empty.tail();
        assertNull(H.get());
        assertNull(T.get());
        assertEquals("<>", H.toString());
        assertEquals("<>", T.toString());
        assertEquals("#epsilon", empty.toString());
        assertTrue(empty.isEmpty());
        H.splice(T, new SpliceList<String>());
        assertNull(H.get());
        assertNull(T.get());
        assertEquals("<>", H.toString());
        assertEquals("<>", T.toString());
        assertEquals("#epsilon", empty.toString());
        assertTrue(empty.isEmpty());
    }
    
    /**
     * Test splicing a list into an empty list.
     */
    @Test
    public void testListToEmpty() {
        // ========splicing [1 2 3] to empty========
        H = empty.head();
        T = empty.tail();
        assertNull(H.get());
        assertNull(T.get());
        assertEquals("<>", H.toString());
        assertEquals("<>", T.toString());
        assertEquals("#epsilon", empty.toString());
        assertTrue(empty.isEmpty());
        H.splice(T, new SpliceList<String>(new String[] {"1", "2", "3"}));    
        assertEquals("1", H.get());
        assertEquals("<<> [1] 2 3 <>>", H.toString());
        assertEquals("1", T.get());
        assertEquals("<<> [1] 2 3 <>>", T.toString());
        assertEquals("[1 2 3]", empty.toString());
        assertEquals("1", empty.head().get());
        assertEquals("<<> [1] 2 3 <>>", empty.head().toString());
        assertEquals("3", empty.tail().get());
        assertEquals("<<> 1 2 [3] <>>", empty.tail().toString());
    }
    
    /**
     * Test splicing an empty list into the beginning of a list.
     */
    @Test
    public void testEmptyToFront() {
        // ========splicing empty to front========
        T.previous(5);
        assertEquals("a", H.get());
        assertEquals("<<> [a] b c <>>", H.toString());
        assertEquals("a", T.get());
        assertEquals("<<> [a] b c <>>", T.toString());
        assertEquals("[a b c]", sl.toString());
        H.splice(T, new SpliceList<String>());
        assertEquals("b", H.get());
        assertEquals("b", T.get());
        assertEquals("[b c]", sl.toString());
        assertEquals("b", sl.head().get());
        assertEquals("c", sl.tail().get());
    }
    
    /**
     * Test splicing an empty list into the first element of a list.
     */
    @Test
    public void testEmptyToSingleFront() {
        // ========splicing empty to single front element========
        assertEquals("a", H.get());
        assertEquals("c", T.get());
        assertEquals("[a b c]", sl.toString());
        H.splice(T, new SpliceList<String>());
        assertNull(H.get());
        assertNull(T.get());
        assertEquals("#epsilon", sl.toString());
        assertNull(sl.head().get());
        assertNull(sl.tail().get());
    }
    
    /**
     * Test splicing an empty list into the middle of a list.
     */
    @Test
    public void testEmptyToMiddle() {
        // ========splicing empty to middle========
        H.next(1);
        T.previous(1);
        assertEquals("b", H.get());
        assertEquals("b", T.get());
        assertEquals("[a b c]", sl.toString());
        H.splice(T, new SpliceList<String>());
        assertEquals("c", H.get());
        assertEquals("c", T.get());;
        assertEquals("[a c]", sl.toString());
        assertEquals("a", sl.head().get());
        assertEquals("c", sl.tail().get());
    }
    
    /**
     * Test splicing an empty list into the back of a list.
     */
    @Test
    public void testEmptyToBack() {
        // ========splicing empty to back========
        H.next(5);
        assertEquals("c", H.get());
        assertEquals("c", T.get());
        assertEquals("[a b c]", sl.toString());
        H.splice(T, new SpliceList<String>());
        assertNull(H.get());
        assertNull(T.get());
        assertEquals("[a b]", sl.toString());
        assertEquals("a", sl.head().get());
        assertEquals("b", sl.tail().get());
    }
    
    /**
     * Test splicing an empty list into a single element in the back of the list.
     */
    @Test
    public void testEmptyToSingleBack() {
        // ========splicing empty to single back element========
        assertEquals("a", H.get());
        assertEquals("c", T.get());
        assertEquals("[a b c]", sl.toString());
        H.splice(T, new SpliceList<String>());
        assertNull(H.get());
        assertNull(T.get());
        assertEquals("#epsilon", sl.toString());
        assertNull(sl.head().get());
        assertNull(sl.tail().get());
    }
    
    /**
     * Test splicing an empty list over a list.
     */
    @Test
    public void testEmptyToWholeList() {
        // ========splicing empty to whole list========
        assertEquals("a", H.get());
        assertEquals("c", T.get());
        assertEquals("[a b c]", sl.toString());
        H.splice(T, new SpliceList<String>());
        assertNull(H.get());
        assertNull(T.get());
        assertEquals("#epsilon", sl.toString());
        assertNull(sl.head().get());
        assertNull(sl.tail().get());
    }
    
    /**
     * Test splicing a list into the beginning of another list.
     */
    @Test
    public void testListToFront() {
        // ========splicing [0 0 0] to front========
        T.previous(5);
        assertEquals("a", H.get());
        assertEquals("a", T.get());
        assertEquals("[a b c]", sl.toString());
        H.splice(T, new SpliceList<String>(new String[] {"0", "0", "0"}));
        assertEquals("0", H.get());
        assertEquals("b", T.get());
        assertEquals("[0 0 0 b c]", sl.toString());
        assertEquals("0", sl.head().get());
        assertEquals("c", sl.tail().get());
    }
    
    /**
     * Test splicing a list into the back of another list.
     */
    @Test
    public void testListToBack() {
        // ========splicing [0 0 0] to back========
        H.next(5);
        assertEquals("c", H.get());
        assertEquals("c", T.get());
        assertEquals("[a b c]", sl.toString());
        H.splice(T, new SpliceList<String>(new String[] {"0", "0", "0"}));
        assertEquals("0", H.get());
        assertNull(T.get());
        assertEquals("[a b 0 0 0]", sl.toString());
        assertEquals("a", sl.head().get());
        assertEquals("0", sl.tail().get());
    }
    
    /**
     * Test splicing a list into the last element of another list.
     */
    @Test
    public void testListToSingleBack() {
        // ========splicing [0 0 0] to single back element========
        assertEquals("a", H.get());
        assertEquals("c", T.get());
        assertEquals("[a b c]", sl.toString());
        H.splice(T, new SpliceList<String>(new String[] {"0", "0", "0"}));
        assertEquals("0", H.get());
        assertNull(T.get());
        assertEquals("[0 0 0]", sl.toString());
        assertEquals("0", sl.head().get());
        assertEquals("0", sl.tail().get());
    }
    
    /**
     * Test splicing a list into the middle of another list.
     */
    @Test
    public void testListToMiddle() {
        // ========splicing [0 0 0] to middile========
        H.next(1);
        T.previous(1);
        assertEquals("b", H.get());
        assertEquals("b", T.get());
        assertEquals("[a b c]", sl.toString());
        H.splice(T, new SpliceList<String>(new String[] {"0", "0", "0"}));
        assertEquals("0", H.get());
        assertEquals("c", T.get());
        assertEquals("[a 0 0 0 c]", sl.toString());
        assertEquals("a", sl.head().get());
        assertEquals("c", sl.tail().get());
    }
    
    /**
     * Test splicing a list on top of another list.
     */
    @Test
    public void testListToWholeList() {
        // ========splicing [0 0 0] to entirety========
        assertEquals("a", H.get());
        assertEquals("c", T.get());
        assertEquals("[a b c]", sl.toString());
        H.splice(T, new SpliceList<String>(new String[] {"0", "0", "0"}));
        assertEquals("0", H.get());
        assertNull(T.get());
        assertEquals("[0 0 0]", sl.toString());
        assertEquals("0", sl.head().get());
        assertEquals("0", sl.tail().get());
    }
    
    /**
     * Test reusing the iterators in multiple splices.
     */
    @Test
    public void testReuseIteratorSplicez() {
        // ========splicing [0 0 0] to middle then [9 9 9] with the same Iterators========
        H.next(1);
        T.previous(1);
        assertEquals("b", H.get());
        assertEquals("b", T.get());
        assertEquals("[a b c]", sl.toString());
        H.splice(T, new SpliceList<String>(new String[] {"0", "0", "0"}));
        assertEquals("0", H.get());
        assertEquals("c", T.get());
        assertEquals("[a 0 0 0 c]", sl.toString());
        assertEquals("a", sl.head().get());
        assertEquals("c", sl.tail().get());
        H.splice(T, new SpliceList<String>(new String[] {"9", "9", "9"}));
        assertEquals("9", H.get());
        assertNull(T.get());
        assertEquals("[a 9 9 9]", sl.toString());
        assertEquals("a", sl.head().get());
        assertEquals("9", sl.tail().get());
    }
}