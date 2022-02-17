package com.runtimeverification.rvmonitor.logicpluginshells.pda.ast;

import com.runtimeverification.rvmonitor.logicpluginshells.pda.visitor.DumpVisitor;
import com.runtimeverification.rvmonitor.logicpluginshells.pda.visitor.GenericVisitor;
import com.runtimeverification.rvmonitor.logicpluginshells.pda.visitor.VoidVisitor;

public class Event {
    String name;
    boolean isDefault = false;

    public Event() {
        this.name = null;
    }

    public Event(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }

    @Override
    public String toString() {
        DumpVisitor visitor = new DumpVisitor();
        String formula = accept(visitor, null);
        return formula;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Event))
            return false;
        return name.equals(((Event) o).getName());
    }
}
