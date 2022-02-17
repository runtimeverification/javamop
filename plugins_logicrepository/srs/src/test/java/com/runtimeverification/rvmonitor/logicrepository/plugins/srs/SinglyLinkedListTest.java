package com.runtimeverification.rvmonitor.logicrepository.plugins.srs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test the singly linked list data structure used in the SRS internals. Based on some manual
 * tests originally in the SinglyLinkedList class.
 */
public class SinglyLinkedListTest {
    
    private SinglyLinkedList<String> l;
    private ArrayList<String> arr;
    private ArrayList<String> replacement;
    
    /**
     * Create a sample SimplyLinkedList to use in tests.
     */
    @Before
    public void setUp() {
        String[] a = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"};
        arr = new ArrayList<String>(Arrays.asList(a));
        replacement = new ArrayList<String>(Arrays.asList("0", "0", "0", "0"));
        l = new SinglyLinkedList<String>(arr);
    }
    
    /**
     * Remove all the odd-index members from an collection given an iterator.
     * @param I The iterator to use to remove elements.
     */
    private static void removeOdd(Iterator I) {
        int i = 0;
        while(I.hasNext()) {
            I.next();
            if((i & 1) == 0) {
                I.remove();
            }
            ++i;
        }
    }
    
    /**
     * Test that the SinglyLinkedList is initialized correctly.
     */
    @Test
    public void testInit() {
        assertEquals("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13]", l.toString());
        int i = 1;
        for(String s : l) {
            assertEquals(Integer.toString(i), s);
            i++;
        }
    }
    
    /**
     * Test that elements can be removed non-contiguously and it will work properly.
     */
    @Test
    public void testRemove() {
        Iterator<String> I = arr.iterator();
        removeOdd(I);
        I = l.iterator();
        removeOdd(I);
        
        assertEquals(arr.toString(), l.toString());
        assertEquals(arr.size(), l.size());
        assertEquals("<12, <>>", "" + l.getTail());
    }
    
    /**
     * Test that chunks can be removed and inserted.
     */
    @Test
    public void testRemoveAddition() {
        // replacing 3 -- 10 with 0, 0, 0, 0
        Iterator<String> I3 = l.iterator();
        while(I3.hasNext()) {
            if(I3.next().equals("3")) {
                break;
            }
        }
        
        Iterator<String> I10 = l.iterator(I3);
        while(I10.hasNext()) {
            if(I10.next().equals("10")) {
                break;
            }
        }
        
        l.nonDestructiveReplace(I3,I10,replacement);
        assertEquals("[1, 2, 0, 0, 0, 0, 11, 12, 13]", l.toString());
        
        // Now make sure I3 is not broken using printRange
        l.printRange(I3, I10);
        
         // Now make sure I10 is not broken
        while(I10.hasNext()) {
            I10.next();
        }
        
        // Now make sure we can iterate over the whole new list
        for(String s : l) {}
        assertEquals("<13, <>>", "" + l.getTail());
        
        for(int i = 0; i < 12; ++i) {
            l.add("foo");
        }
        
        assertEquals("<foo, <>>", "" + l.getTail());
        assertEquals("<1, <2, <0, <0, <0, <0, <11, <12, <13, <foo, <foo, <foo, <foo, <foo, <foo, "
            + "<foo, <foo, <foo, <foo, <foo, <foo, <>>>>>>>>>>>>>>>>>>>>>>", "" + l.getHead());
        
        l.addAll(arr);
        assertEquals("[1, 2, 0, 0, 0, 0, 11, 12, 13, foo, foo, foo, foo, foo, foo, foo, foo, " +
            "foo, foo, foo, foo, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13]", l.toString());
        assertEquals("<13, <>>", "" + l.getTail());
        
        l.remove("foo");
        assertEquals("[1, 2, 0, 0, 0, 0, 11, 12, 13, foo, foo, foo, foo, foo, foo, foo, foo, " +
            "foo, foo, foo, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13]", l.toString());
        l.remove("foo");
        assertEquals("[1, 2, 0, 0, 0, 0, 11, 12, 13, foo, foo, foo, foo, foo, foo, foo, foo, " +
        "foo, foo, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13]", l.toString());
        
        l.remove("11");
        assertEquals("[1, 2, 0, 0, 0, 0, 12, 13, foo, foo, foo, foo, foo, foo, foo, foo, foo, " +
            "foo, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13]", l.toString());
        l.remove("11");
        assertEquals("[1, 2, 0, 0, 0, 0, 12, 13, foo, foo, foo, foo, foo, foo, foo, foo, foo, " +
            "foo, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13]", l.toString());
        assertEquals("<13, <>>", "" + l.getTail());
    }
    
    /**
     * Test that the beginning can be replaced.
     */
    @Test
    public void testReplaceBeginning() {
        // replacing 1 -- 10 with 0, 0, 0, 0
        
        Iterator<String> I1 = l.iterator();
        I1.next();
        Iterator<String> I10 = l.iterator(I1);
        while(I10.hasNext()) {
            if(I10.next().equals("10")) {
                break;
            }
        }
        
        l.nonDestructiveReplace(I1,I10,replacement);
        assertEquals("[0, 0, 0, 0, 11, 12, 13]", l.toString());
    }
    
    /**
     * Test that the end can be replaced.
     */
    @Test
    public void testReplaceEnd() {
        // replacing 11 -- 13 with 0, 0, 0, 0")
        
        Iterator<String> I11 = l.iterator();
        
        while(I11.hasNext()) {
            if(I11.next().equals("11")) {
                break;
            }
        }
        Iterator<String> I13 = l.iterator(I11);
        while(I13.hasNext()) {
            if(I13.next().equals("13")) {
                break;
            }
        }
        
        l.nonDestructiveReplace(I11,I13,new ArrayList<String>());
        assertEquals("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]", l.toString());
        assertEquals("<10, <>>", "" + l.getTail());
    }
}