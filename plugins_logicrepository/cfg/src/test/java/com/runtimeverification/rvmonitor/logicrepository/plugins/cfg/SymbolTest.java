package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg;

import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util.Cursor;
import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util.EOF;
import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util.Epsilon;
import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util.NonTerminal;
import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util.Terminal;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test the Symbol class and its simple subclasses.
 */
public class SymbolTest {
    
    /**
     * Test the Terminal subclass.
     */
    @Test
    public void testTerminal() {
        Terminal a = new Terminal("a");
        Terminal b = new Terminal("b");
        
        Terminal a_again = new Terminal("a");
        Terminal a_copy = new Terminal(a);
        
        assertEquals(a, a_again);
        assertEquals(a, a_copy);
        assertFalse(a.equals(b));
        assertFalse(a_again.equals(b));
        assertFalse(a_copy.equals(b));
        
        assertEquals(a.hashCode(), a_again.hashCode());
        assertEquals(a.hashCode(), a_copy.hashCode());
        
        assertEquals(a.toString(), "a");
        assertEquals(a_copy.toString(), "a");
    }
    
    /**
     * Test the Epsilon subclass.
     */
    @Test
    public void testEpsilon() {
        Epsilon epsilon = new Epsilon();
        
        assertEquals(epsilon.toString(), "epsilon");
        
        Epsilon another = new Epsilon();
        assertEquals(epsilon, another);
        assertEquals(epsilon.hashCode(), another.hashCode());
        
        Terminal a = new Terminal("a");
        assertFalse(a.equals(epsilon));
    }
    
    /**
     * Test the EOF subclass.
     */
    @Test
    public void testEOF() {
        EOF eof = new EOF();
        EOF eof_again = new EOF();
        
        assertEquals(eof, eof_again);
        assertEquals(eof.hashCode(), eof_again.hashCode());
        
        Epsilon epsilon = new Epsilon();
        
        assertFalse(eof.equals(epsilon));
    }
    
    /**
     * Test the cursor subclass.
     */
    @Test
    public void testCursor() {
        Cursor cursor = new Cursor();
        Cursor cursor_again = new Cursor();
        
        assertEquals(cursor, cursor_again);
        assertEquals(cursor.hashCode(), cursor_again.hashCode());
        
        Epsilon epsilon = new Epsilon();
        
        assertFalse(cursor.equals(epsilon));
    }
    
    /**
     * Test the NonTerminal subclass.
     */
    @Test
    public void testNonTerminal() {
        NonTerminal a = new NonTerminal("a");
        NonTerminal b = new NonTerminal("b");
        
        NonTerminal a_again = new NonTerminal("a");
        NonTerminal a_copy = new NonTerminal(a);
        
        assertEquals(a, a_again);
        assertEquals(a, a_copy);
        assertFalse(a.equals(b));
        assertFalse(a_again.equals(b));
        assertFalse(a_copy.equals(b));
        
        assertEquals(a.hashCode(), a_again.hashCode());
        assertEquals(a.hashCode(), a_copy.hashCode());
        
        assertEquals(a.toString(), "nt(a)");
        assertEquals(a_copy.toString(), "nt(a)");
    }
}
