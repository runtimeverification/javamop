package com.runtimeverification.rvmonitor.logicpluginshells.srs.pma;

import java.util.Map;

public class Fail implements AbstractSequence {

    private Fail() {
    }

    @Override
    public String toString() {
        return "#fail";
    }

    @Override
    public String toDotString() {
        return "\\#fail";
    }

    public static Fail theFail = new Fail();

    public static Fail get() {
        return theFail;
    }

    @Override
    public int dotLength() {
        return 5;
    }

    @Override
    public void getImpl(StringBuilder sb, Map<Symbol, Integer> symToNum) {
        sb.append(", ");
        sb.append(0);
    }
}
