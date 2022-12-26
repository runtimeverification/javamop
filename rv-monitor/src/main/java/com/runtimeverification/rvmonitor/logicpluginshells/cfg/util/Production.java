package com.runtimeverification.rvmonitor.logicpluginshells.cfg.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class Production implements Serializable, Cloneable {
    public NonTerminal lhs;
    public ArrayList<Symbol> rhs;

    public Production(NonTerminal nt, ArrayList<Symbol> l) {
        lhs = nt;
        rhs = new ArrayList<Symbol>();
        for (Symbol s : l)
            rhs.add(s);
    }

    Production(Production p) {
        this(p.lhs, p.rhs);
    }

    @Override
    public Production clone() {
        return new Production(this);
    }

    // Is this an epsilon production?
    // Note we should never be able to have an empty lhs/rhs
    public boolean isEpsilon() {
        Symbol t = rhs.get(0);
        return (t instanceof Epsilon);
    }

    public boolean isSelfLoop() {
        if (rhs.size() > 1)
            return false;
        else {
            return lhs.equals(rhs.get(0));
        }
    }

    // generate a string for debugging purposes
    @Override
    public String toString() {
        String s = lhs.toString() + " ->";
        for (Symbol i : rhs)
            s += " " + i.toString();
        return s;
    }

    @Override
    public int hashCode() {
        return lhs.hashCode() ^ rhs.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof Production))
            return false;
        Production p = (Production) o;
        return (lhs.equals(p.lhs) && rhs.equals(p.rhs));
    }

    boolean contains(Symbol s) {
        for (Symbol x : rhs)
            if (x.equals(s))
                return true;
        return false;
    }

    HashSet<NonTerminal> nonTerminals() {
        HashSet<NonTerminal> r = new HashSet<NonTerminal>();
        for (Symbol s : rhs)
            if (s instanceof NonTerminal)
                r.add((NonTerminal) s);
        return r;
    }

    void replaceRHSNTs(NonTerminal n, NonTerminal o) {
        for (int i = 0; i < rhs.size(); i++)
            if (rhs.get(i).equals(o))
                rhs.set(i, n);
    }

    ArrayList<Symbol> beforeSym(Symbol s) {
        return Util.getBefore(rhs, s);
    }

    HashSet<ArrayList<Symbol>> beforeSymS(Symbol s) {
        return Util.getBeforeS(rhs, s);
    }
}
