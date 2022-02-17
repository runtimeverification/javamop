package com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec;

import java.util.ArrayList;
import java.util.Iterator;

public class RVMParameterSet implements Iterable<RVMParameters> {

    private final ArrayList<RVMParameters> paramSet;

    public RVMParameterSet() {
        this.paramSet = new ArrayList<RVMParameters>();
    }

    public RVMParameterSet(ArrayList<RVMParameters> paramSet) {
        this.paramSet = new ArrayList<RVMParameters>();
        this.paramSet.addAll(paramSet);
    }

    /**
     * Find a parameter set with the same parameters in paramSet
     *
     * @param set
     *            a list of parameters
     */
    public RVMParameters getParameters(RVMParameters set) {
        for (RVMParameters s : this.paramSet) {
            if (set.equals((Object) s))
                return s;
        }
        return null;
    }

    public void add(RVMParameters param) {
        if (getParameters(param) == null) {
            paramSet.add(param);
        }
    }

    public int size() {
        return this.paramSet.size();
    }

    public void addAll(RVMParameterSet set) {
        if (set == null || set.paramSet == null)
            return;
        for (RVMParameters param : set.paramSet) {
            this.add(param);
        }
    }

    public void remove(RVMParameters param) {
        this.paramSet.remove(param);
    }

    public boolean contains(RVMParameters param) {
        for (RVMParameters param2 : paramSet) {
            if (param2.equals(param))
                return true;
        }

        return false;
    }

    public RVMParameters get(int i) {
        return this.paramSet.get(i);
    }

    public void sort() {
        for (int i = 0; i < paramSet.size(); i++) {
            for (int j = i + 1; j < paramSet.size(); j++) {
                if (paramSet.get(j).contains(paramSet.get(i))
                        && paramSet.get(j).size() > paramSet.get(i).size()) {
                    RVMParameters temp = paramSet.get(i);
                    paramSet.set(i, paramSet.get(j));
                    paramSet.set(j, temp);
                }
            }
        }
    }

    @Override
    public Iterator<RVMParameters> iterator() {
        return paramSet.iterator();
    }

    public int getIdnum(RVMParameters p) {
        if (!contains(p))
            return -1;

        return this.paramSet.indexOf(p);
    }

    @Override
    public String toString() {
        return paramSet.toString();
    }

}
