package com.runtimeverification.rvmonitor.logicpluginshells.srs.pma;

import java.util.HashMap;

public class Symbol {
    protected static HashMap<String, Symbol> symbolTable;
    protected String name;

    protected Symbol() {
        name = "";
    }

    protected Symbol(String name) {
        this.name = name;
    }

    static {
        symbolTable = new HashMap<String, Symbol>();
    }

    public static Symbol get(String name) {
        Symbol ret = symbolTable.get(name);
        if (ret == null) {
            ret = new Symbol(name);
            symbolTable.put(name, ret);
        }
        return ret;
    }

    @Override
    public String toString() {
        return name;
    }

    public String toDotString() {
        if (name.equals("^"))
            return "\\mathbin{\\char`\\^}";
        if (name.equals("$"))
            return "\\$";
        return name;
    }

    public int length() {
        return name.length();
    }

    public static void main(String[] args) {
        Symbol foo = Symbol.get("foo");
        Symbol foo2 = Symbol.get("foo");
        Symbol bar = Symbol.get("bar");
        Symbol bar2 = Symbol.get("bar");
        System.out.println(foo == foo2); // true
        System.out.println(foo == bar); // false
        System.out.println(bar == bar2); // true
    }
}
