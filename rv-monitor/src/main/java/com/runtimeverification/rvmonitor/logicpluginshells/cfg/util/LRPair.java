package com.runtimeverification.rvmonitor.logicpluginshells.cfg.util;

class LRPair implements java.io.Serializable {
    Production prod;
    Terminal look;

    LRPair(Production p, Terminal t) {
        prod = new Production(p);
        look = t;
    }

    LRPair(LRPair l) {
        this(new Production(l.prod), l.look);
    }

    @Override
    public int hashCode() {
        return prod.hashCode() + look.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof LRPair))
            return false;
        return look.equals(((LRPair) o).look) && prod.equals(((LRPair) o).prod);
    }

    @Override
    public String toString() {
        return "[ " + prod.toString() + " , " + look.toString() + " ]";
    }

    boolean isAfterCursor(Symbol s) {
        int cursor = prod.rhs.indexOf(new Cursor());
        return cursor + 1 < prod.rhs.size()
                && prod.rhs.get(cursor + 1).equals(s);
    }

    int indexOfCursor() {
        return prod.rhs.indexOf(new Cursor());
    }

    boolean isAnyAfterCursor() {
        return indexOfCursor() + 1 < prod.rhs.size();
    }

    boolean isTwoAfterCursor() {
        return indexOfCursor() + 2 < prod.rhs.size();
    }

    boolean isTAfterCursor() {
        int cursor = prod.rhs.indexOf(new Cursor());
        return cursor + 1 < prod.rhs.size()
                && prod.rhs.get(cursor + 1) instanceof Terminal;
    }

    boolean isNTAfterCursor() {
        return isAnyAfterCursor()
                && prod.rhs.get(indexOfCursor() + 1) instanceof NonTerminal;
    }

    boolean isNTBeforeCursor() {
        int cursor = prod.rhs.indexOf(new Cursor());
        return cursor > 0 && prod.rhs.get(cursor - 1) instanceof NonTerminal;
    }

    // This is unsafe if the cursor is at the end
    Symbol getAfterCursor() {
        return prod.rhs.get(indexOfCursor() + 1);
    }

    Symbol getBeforeCursor() {
        return prod.rhs.get(indexOfCursor() - 1);
    }

    Production prodWithoutCursor() {
        Production ret = new Production(prod);
        ret.rhs.remove(new Cursor());
        return ret;
    }
}
