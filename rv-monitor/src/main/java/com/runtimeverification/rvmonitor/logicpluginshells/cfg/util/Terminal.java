package com.runtimeverification.rvmonitor.logicpluginshells.cfg.util;

public class Terminal extends Symbol {
    public Terminal(String s) {
        super(s);
    }

    public Terminal(Symbol s) {
        super(s.name);
    }

    @Override
    public String toString() {
        return "t(" + name + ")";
    }
}
