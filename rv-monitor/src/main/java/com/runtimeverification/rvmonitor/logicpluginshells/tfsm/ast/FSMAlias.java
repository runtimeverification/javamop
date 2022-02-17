package com.runtimeverification.rvmonitor.logicpluginshells.tfsm.ast;

import java.util.ArrayList;
import java.util.List;

import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.visitor.GenericVisitor;
import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.visitor.VoidVisitor;

public class FSMAlias extends Node {

    String groupName;
    List<String> States = new ArrayList<String>();

    public FSMAlias(int line, int column, String groupName, List<String> States) {
        super(line, column);
        this.groupName = groupName;
        this.States = States;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<String> getStates() {
        return States;
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
