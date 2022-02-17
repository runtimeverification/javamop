package com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec;

import java.util.List;

import com.runtimeverification.rvmonitor.java.rvj.parser.ast.visitor.GenericVisitor;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.visitor.VoidVisitor;

public class CombinedProperty extends Property {

    private final List<Property> properties;

    public CombinedProperty(int line, int column, String type,
            List<Property> properties) {
        super(line, column, type);
        this.properties = properties;
    }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }

}
