package com.runtimeverification.rvmonitor.logicpluginshells.po.util;

import java.util.ArrayList;

public class ORCondition extends Condition {
    Condition con1, con2;

    public ORCondition(Condition con1, Condition con2) {
        this.con1 = con1;
        this.con2 = con2;
    }

    public Condition getCon1() {
        return con1;
    }

    public void setCon1(Condition con1) {
        this.con1 = con1;
    }

    public Condition getCon2() {
        return con2;
    }

    public void setCon2(Condition con2) {
        this.con2 = con2;
    }

    @Override
    public ArrayList<String> getAllNodes() {
        ArrayList<String> ret = new ArrayList<String>();

        ret.addAll(con1.getAllNodes());
        ret.addAll(con2.getAllNodes());

        return ret;
    }

    @Override
    public ArrayList<SimpleCondition> getSimpleConditions() {
        ArrayList<SimpleCondition> ret = new ArrayList<SimpleCondition>();

        ret.addAll(con1.getSimpleConditions());
        ret.addAll(con2.getSimpleConditions());

        return ret;
    }

    @Override
    public ArrayList<BlockCondition> getBlockConditions() {
        ArrayList<BlockCondition> ret = new ArrayList<BlockCondition>();

        ret.addAll(con1.getBlockConditions());
        ret.addAll(con2.getBlockConditions());

        return ret;
    }
}
