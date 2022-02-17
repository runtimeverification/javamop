package com.runtimeverification.rvmonitor.logicpluginshells.srs.pma;

import java.util.Map;

public interface AbstractSequence {
    public String toDotString();

    public int dotLength();

    public void getImpl(StringBuilder sb, Map<Symbol, Integer> symToNum);
}
