package com.runtimeverification.rvmonitor.logicpluginshells.srs.pma;

import java.util.Map;

public class Succeed implements AbstractSequence {

    private Succeed() {
    }

    @Override
    public String toString() {
        return "#succeed";
    }

    @Override
    public String toDotString() {
        return "\\#succeed";
    }

    public static Succeed theSucceed = new Succeed();

    public static Succeed get() {
        return theSucceed;
    }

    @Override
    public int dotLength() {
        return 8;
    }

    @Override
    public void getImpl(StringBuilder sb, Map<Symbol, Integer> symToNum) {
        sb.append(", ");
        sb.append(1);
    }
}
