package com.runtimeverification.rvmonitor.logicpluginshells.po.util;

import java.util.ArrayList;

public class BlockCondition extends Condition {
    String beforeEvent;
    String blockEvent = null; // optional

    public BlockCondition(String beforeEvent) {
        this.beforeEvent = beforeEvent;
    }

    public BlockCondition(String beforeEvent, String blockEvent) {
        this.beforeEvent = beforeEvent;
        this.blockEvent = blockEvent;
    }

    public String getBeforeEvent() {
        return beforeEvent;
    }

    public void setBeforeEvent(String beforeEvent) {
        this.beforeEvent = beforeEvent;
    }

    public String getBlockEvent() {
        return blockEvent;
    }

    public void setBlockEvent(String blockEvent) {
        this.blockEvent = blockEvent;
    }

    @Override
    public ArrayList<String> getAllNodes() {
        ArrayList<String> ret = new ArrayList<String>();

        ret.add(beforeEvent);
        if (blockEvent != null)
            ret.add(blockEvent);

        return ret;
    }

    @Override
    public ArrayList<SimpleCondition> getSimpleConditions() {
        ArrayList<SimpleCondition> ret = new ArrayList<SimpleCondition>();

        return ret;
    }

    @Override
    public ArrayList<BlockCondition> getBlockConditions() {
        ArrayList<BlockCondition> ret = new ArrayList<BlockCondition>();

        ret.add(this);

        return ret;
    }

}
