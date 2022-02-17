package com.runtimeverification.rvmonitor.logicpluginshells.tfsm.visitor;

import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.ast.FSMAlias;
import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.ast.FSMInput;
import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.ast.FSMItem;
import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.ast.FSMTransition;
import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.ast.Node;

public final class DumpVisitor implements VoidVisitor<Object> {

    private final SourcePrinter printer = new SourcePrinter();

    public String getSource() {
        return printer.getSource();
    }

    @Override
    public void visit(Node n, Object arg) {
        throw new IllegalStateException(n.getClass().getName());
    }

    @Override
    public void visit(FSMInput f, Object arg) {
        if (f.getItems() != null) {
            for (FSMItem i : f.getItems()) {
                i.accept(this, arg);
                printer.printLn();
            }
        }
        if (f.getAliases() != null) {
            for (FSMAlias a : f.getAliases()) {
                a.accept(this, arg);
                printer.printLn();
            }
        }
    }

    @Override
    public void visit(FSMItem i, Object arg) {
        boolean firstFlag = true;
        printer.print(i.getState() + "[");
        if (i.getTransitions() != null) {
            for (FSMTransition t : i.getTransitions()) {
                if (!firstFlag) {
                    printer.print(",");
                    printer.printLn();
                }
                t.accept(this, arg);
                firstFlag = false;
            }
        }
        printer.print("]");
        printer.printLn();
    }

    @Override
    public void visit(FSMAlias a, Object arg) {
        boolean firstFlag = true;
        printer.print(a.getGroupName() + " : ");
        if (a.getStates() != null) {
            for (String state : a.getStates()) {
                if (!firstFlag) {
                    printer.print(", ");
                }
                printer.print(state);
                firstFlag = false;
            }
        }
        printer.printLn();

    }

    @Override
    public void visit(FSMTransition t, Object arg) {
        if (t.isDefaultFlag()) {
            printer.print("default ");
            printer.print(t.getStateName());
            printer.printLn();
        } else {
            printer.print(t.getEventName());
            printer.print(" -> ");
            printer.print(t.getStateName());
            printer.printLn();
        }
    }
}
