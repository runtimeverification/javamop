package com.runtimeverification.rvmonitor.logicpluginshells.fsm.visitor;

import com.runtimeverification.rvmonitor.logicpluginshells.fsm.ast.FSMAlias;
import com.runtimeverification.rvmonitor.logicpluginshells.fsm.ast.FSMInput;
import com.runtimeverification.rvmonitor.logicpluginshells.fsm.ast.FSMItem;
import com.runtimeverification.rvmonitor.logicpluginshells.fsm.ast.FSMTransition;
import com.runtimeverification.rvmonitor.logicpluginshells.fsm.ast.Node;

public class HasDefaultVisitor implements GenericVisitor<boolean[], Object> {

    @Override
    public boolean[] visit(Node n, Object arg) {
        boolean[] ret = new boolean[1];
        ret[0] = false;
        return ret;
    }

    @Override
    public boolean[] visit(FSMInput f, Object arg) {
        boolean[] ret = new boolean[1];
        ret[0] = false;

        if (f.getItems() != null) {
            for (FSMItem i : f.getItems()) {
                boolean temp[] = i.accept(this, arg);
                ret[0] = ret[0] || temp[0];
            }
        }
        return ret;
    }

    @Override
    public boolean[] visit(FSMItem i, Object arg) {
        boolean[] ret = new boolean[1];
        ret[0] = false;

        if (i.getTransitions() != null) {
            for (FSMTransition t : i.getTransitions()) {
                boolean[] temp = t.accept(this, arg);
                ret[0] = ret[0] || temp[0];
            }
        }

        return ret;
    }

    @Override
    public boolean[] visit(FSMAlias a, Object arg) {
        boolean[] ret = new boolean[1];
        ret[0] = false;
        return ret;
    }

    @Override
    public boolean[] visit(FSMTransition t, Object arg) {
        boolean[] ret = new boolean[1];
        ret[0] = false;

        if (t.isDefaultFlag()) {
            ret[0] = true;
        }
        return ret;
    }

}
