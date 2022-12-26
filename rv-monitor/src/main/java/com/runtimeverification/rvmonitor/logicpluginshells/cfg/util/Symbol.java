package com.runtimeverification.rvmonitor.logicpluginshells.cfg.util;

public class Symbol implements java.io.Serializable {
    public final String name;

    public Symbol(String s) {
        name = s;
    }

    @Override
    public String toString() {
        return "sym(" + name + ")";
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof Symbol))
            return false;
        Symbol s = (Symbol) o;
        return (name.equals(s.name));
    }
}
