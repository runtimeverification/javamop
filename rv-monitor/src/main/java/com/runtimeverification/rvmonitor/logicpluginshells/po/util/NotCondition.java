package com.runtimeverification.rvmonitor.logicpluginshells.po.util;

import java.util.ArrayList;

public class NotCondition extends Condition {
    private Condition condition;

    public NotCondition(Condition condition) {
        this.condition = condition;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setBeforeEvent(Condition condition) {
        this.condition = condition;
    }

    @Override
    public ArrayList<String> getAllNodes() {
        return condition.getAllNodes();
    }

}
