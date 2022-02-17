package com.runtimeverification.rvmonitor.logicpluginshells.srs.pma;

import java.util.ArrayList;
import java.util.Map;

public class Sequence extends ArrayList<Symbol> implements AbstractSequence {

    public Sequence(int size) {
        super(size);
    }

    public Sequence() {
        super();
    }

    public Sequence(ArrayList<Symbol> symbols) {
        for (Symbol s : symbols) {
            if (s == null)
                continue;
            add(s);
        }
    }

    @Override
    public String toString() {
        if (size() == 0)
            return "#epsilon";
        StringBuilder sb = new StringBuilder();
        for (Symbol s : this) {
            sb.append(s.toString());
            sb.append(" ");
        }
        return sb.toString();
    }

    @Override
    public String toDotString() {
        if (size() == 0)
            return "\\#epsilon";
        StringBuilder sb = new StringBuilder();
        for (Symbol s : this) {
            sb.append(s.toDotString());
            sb.append("\\ ");
        }
        String ret = sb.toString();
        return ret.substring(0, ret.length() - 2);
    }

    public Sequence copy() {
        Sequence ret = new Sequence(size());
        for (Symbol s : this) {
            ret.add(s);
        }
        return ret;
    }

    public static void printSequenceArray(Sequence[] arr) {
        for (Sequence s : arr) {
            System.out.print(s);
            System.out.print("; ");
        }
        System.out.println();
    }

    @Override
    public int dotLength() {
        if (isEmpty())
            return 8;
        int len = 0;
        for (Symbol s : this) {
            len += s.length();
        }
        return len;
    }

    @Override
    public void getImpl(StringBuilder sb, Map<Symbol, Integer> symToNum) {
        sb.append(", new int[] {");
        for (Symbol s : this) {
            sb.append(symToNum.get(s));
            sb.append(",");
        }
        sb.append("}");
    }
}
