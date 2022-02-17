package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util;

public class LRPair implements java.io.Serializable {
    private Production prod;
    private Terminal look;
    
    public LRPair(Production p, Terminal t) { 
        prod = new Production(p); look = t;
    }
    
    public LRPair(LRPair l) {
        this(new Production(l.prod),l.look);
    }
    
    @Override
    public int hashCode() { 
        return prod.hashCode() + look.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof LRPair)) {
            return false;
        }
        return look.equals(((LRPair)o).look) && prod.equals(((LRPair)o).prod);
    }
    
    @Override
    public String toString() { 
        return "[ " + prod.toString() + " , " + look.toString() + " ]";
    }
    
    public boolean isAfterCursor(Symbol s) {
        int cursor = prod.getRhs().indexOf(new Cursor());
        return cursor + 1 < prod.getRhs().size() && prod.getRhs().get(cursor + 1).equals(s);
    }
    
    public int indexOfCursor() { 
        return prod.getRhs().indexOf(new Cursor());
    }
    
    public boolean isAnyAfterCursor() {
        return indexOfCursor() + 1 < prod.getRhs().size();
    }
    
    public boolean isTwoAfterCursor() {
        return indexOfCursor() + 2 < prod.getRhs().size();
    }
    
    public boolean isTAfterCursor() {
        int cursor = prod.getRhs().indexOf(new Cursor());
        return cursor + 1 < prod.getRhs().size() && prod.getRhs().get(cursor + 1) instanceof Terminal;
    }
    
    public boolean isNTAfterCursor() {
        return isAnyAfterCursor() && prod.getRhs().get(indexOfCursor() + 1) instanceof NonTerminal;
    }
    
    public boolean isNTBeforeCursor() {
        int cursor = prod.getRhs().indexOf(new Cursor());
        return cursor > 0 && prod.getRhs().get(cursor - 1) instanceof NonTerminal;
    }
    
    // This is unsafe if the cursor is at the end
    public Symbol getAfterCursor() {
        return prod.getRhs().get(indexOfCursor() + 1);
    }
    
    public Symbol getBeforeCursor() {
        return prod.getRhs().get(indexOfCursor() - 1);
    }
    
    public Production prodWithoutCursor() {
        Production ret = new Production(prod);
        ret.getRhs().remove(new Cursor());
        return ret;
    }
    
    public Production getProd() {
        return prod;
    }
    
    public Terminal getLook() {
        return look;
    }
}
