package com.runtimeverification.rvmonitor.java.rvj.parser.ast.typepattern;

import com.runtimeverification.rvmonitor.java.rvj.parser.ast.Node;

public abstract class TypePattern extends Node {

    String op;

    public TypePattern(int line, int column, String op) {
        super(line, column);
        this.op = op;
    }

    public String getOp() {
        return op;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TypePattern)) {
            return false;
        }

        TypePattern t2 = (TypePattern) o;

        return op.equals(t2.getOp());
    }

}
