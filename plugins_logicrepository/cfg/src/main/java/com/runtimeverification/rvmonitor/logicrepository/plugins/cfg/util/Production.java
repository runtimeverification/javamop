package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * An expansion/production from a nonterminal symbol to several other symbols in a context-free grammar.
 */
public class Production implements Serializable, Cloneable {
    private NonTerminal lhs;
    private ArrayList<Symbol> rhs;
    
    /**
     * Construct a production with the given nonterminal and its expansion.
     * @param nt The nonterminal left hand side.
     * @param l The list of symbols in the right hand side.
     */
    public Production(NonTerminal nt, List<Symbol> l){
        lhs = nt;
        rhs = new ArrayList<Symbol>();
        for (Symbol s : l) {
            rhs.add(s);
        }
    }
    
    /**
     * Construct a copy of the given production.
     * @param p The production to copy.
     */
    public Production(Production p) { 
        this(p.lhs,p.rhs);
    }
    
    /**
     * Produce a copy of this production.
     * @return A distinct deep copy of this Production.
     */
    public Production clone() { 
        return new Production(this);
    }
    
    /**
     * Test if this Production is an epsilon production.
     * @return If this is an epsilon production.
     */
    public boolean isEpsilon() {
        // Note we should never be able to have an empty lhs/rhs
        Symbol t = rhs.get(0); 
        return (t instanceof Epsilon); 
    }
    
    /**
     * Test if this Production only expands to itself and nothing else.
     */
    public boolean isSelfLoop() { 
        if (rhs.size() > 1) {
            return false; 
        } else {
            return lhs.equals(rhs.get(0));
        }
    }
    
    @Override
    public String toString() {
        // generate a string for debugging purposes
        String s = lhs.toString() + " ->";
        for (Symbol i : rhs) {
            s += " " + i.toString();
        }
        return s;
    }
    
    @Override
    public int hashCode() {
        return lhs.hashCode() ^ rhs.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Production)) {
            return false;
        }
        Production p = (Production) o;
        return (lhs.equals(p.lhs) && rhs.equals(p.rhs));
    }
    
    /**
     * Test if the given symbol is produced directly by this production.
     * @param s The symbol to look for.
     * @return If the symbol is in this production.
     */
    public boolean contains(Symbol s) {
        for (Symbol x : rhs) {
            if (x.equals(s)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * The set of nonterminals in the right hand side of this production.
     * @return A set of nonterminals in the right hand side.
     */
    public HashSet<NonTerminal> nonTerminals() {
        HashSet<NonTerminal> r = new HashSet<NonTerminal>();
        for (Symbol s : rhs) {
            if (s instanceof NonTerminal) {
                r.add((NonTerminal)s);
            }
        }
        return r;
    }
    
    /**
     * Replaces instances of nonterminals {@code o} with {@code n} in the right hand side.
     * @param n The nonterminal to insert.
     * @param o The nonterminal to replace.
     */
    public void replaceRHSNTs(NonTerminal n, NonTerminal o) {
        for (int i = 0; i < rhs.size(); i++) {
            if (rhs.get(i).equals(o)) {
                rhs.set(i,n);
            }
        }
    }
    
    /**
     * All the symbols before {@code s}.
     * @param s The symbol to stop at when retrieving symbols.
     * @return An ArrayList of all the Symbols up to {@code s}.
     */
    public ArrayList<Symbol> beforeSym(Symbol s) {
        return Util.getBefore(rhs,s);
    }
    
    /**
     * The set of all lists of symbols before instances of {@code s}.
     * @param s The symbol to look for.
     * @return A HashSet of ArrayLists of elements up to all instances of {@code s}.
     */
    public HashSet<ArrayList<Symbol>> beforeSymS(Symbol s) {
        return Util.getBeforeS(rhs,s);
    }
    
    /**
     * The left hand side of the production.
     * @return The left hand side.
     */
    public NonTerminal getLhs() {
        return lhs;
    }
    
    /**
     * The right hand side of the production.
     * @return The right hand side.
     */
    public ArrayList<Symbol> getRhs() {
        return rhs;
    }
}
