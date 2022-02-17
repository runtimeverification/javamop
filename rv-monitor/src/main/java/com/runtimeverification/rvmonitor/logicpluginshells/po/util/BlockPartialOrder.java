package com.runtimeverification.rvmonitor.logicpluginshells.po.util;

public class BlockPartialOrder {
    private String before;
    private String block;
    private String after;

    public BlockPartialOrder(String before, String block, String after) {
        this.before = before;
        this.block = block;
        this.after = after;
    }

    void setBefore(String before) {
        this.before = before;
    }

    String getBefore() {
        return before;
    }

    void setBlock(String block) {
        this.block = block;
    }

    String getBlock() {
        return block;
    }

    void setAfter(String after) {
        this.after = after;
    }

    String getAfter() {
        return after;
    }

}
