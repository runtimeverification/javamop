package com.runtimeverification.rvmonitor.logicpluginshells.srs.pma;

import java.util.HashSet;
import java.util.Set;

public class Rule {
    private static int counter = 0;
    private int number;
    private Set<Symbol> terminals = new HashSet<Symbol>();

    private Sequence lhs;
    private AbstractSequence rhs;

    public Sequence getLhs() {
        return lhs;
    }

    public AbstractSequence getRhs() {
        return rhs;
    }

    protected Rule() {
    }

    public Rule(Sequence lhs, AbstractSequence rhs) {
        number = counter++;
        this.lhs = lhs;
        this.rhs = rhs;
        computeTerminals();
    }

    private void computeTerminals() {
        for (Symbol s : lhs) {
            terminals.add(s);
        }
        if (rhs instanceof Sequence) {
            for (Symbol s : (Sequence) rhs) {
                terminals.add(s);
            }
        }
    }

    public int getNumber() {
        return number;
    }

    public Set<Symbol> getTerminals() {
        return terminals;
    }

    @Override
    public String toString() {
        return lhs.toString() + " -> " + rhs.toString();
    }

    public String toDotString() {
        return lhs.toDotString() + " \\rightarrow " + rhs.toDotString();
    }

    public int dotLength() {
        return lhs.dotLength() + rhs.dotLength() + 3;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Rule))
            return false;
        Rule r = (Rule) o;
        return (lhs.equals(r.lhs) && rhs.equals(r.rhs));
    }

    @Override
    public int hashCode() {
        return lhs.hashCode() ^ rhs.hashCode();
    }
}
