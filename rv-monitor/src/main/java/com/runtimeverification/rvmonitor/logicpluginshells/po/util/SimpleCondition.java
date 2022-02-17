package com.runtimeverification.rvmonitor.logicpluginshells.po.util;

import java.util.ArrayList;

public class SimpleCondition extends Condition {
    private String beforeEvent;

    public SimpleCondition(String beforeEvent) {
        this.beforeEvent = beforeEvent;
    }

    public String getBeforeEvent() {
        return beforeEvent;
    }

    public void setBeforeEvent(String beforeEvent) {
        this.beforeEvent = beforeEvent;
    }

    @Override
    public ArrayList<String> getAllNodes() {
        ArrayList<String> ret = new ArrayList<String>();

        ret.add(beforeEvent);

        return ret;
    }

    @Override
    public ArrayList<SimpleCondition> getSimpleConditions() {
        ArrayList<SimpleCondition> ret = new ArrayList<SimpleCondition>();

        ret.add(this);

        return ret;
    }

    @Override
    public ArrayList<BlockCondition> getBlockConditions() {
        ArrayList<BlockCondition> ret = new ArrayList<BlockCondition>();

        return ret;
    }

}
