package com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec;

public class RVMonitorParameterPair implements
Comparable<RVMonitorParameterPair> {
    private final RVMParameters param1;
    private final RVMParameters param2;

    public RVMonitorParameterPair(RVMParameters param1, RVMParameters param2) {
        this.param1 = param1;
        this.param2 = param2;
    }

    public RVMParameters getParam1() {
        return param1;
    }

    public RVMParameters getParam2() {
        return param2;
    }

    public boolean equals(RVMonitorParameterPair mopPair) {
        return this.getParam1().equals(mopPair.getParam1())
                && this.getParam2().equals(mopPair.getParam2());
    }

    @Override
    public String toString() {
        return "(" + param1.toString() + ", " + param2.toString() + ")";
    }

    @Override
    public int hashCode() {
        return param1.hashCode() ^ param2.hashCode();
    }

    @Override
    public int compareTo(RVMonitorParameterPair that) {
        int r = this.param1.compareTo(that.param1);
        if (r != 0)
            return r;
        return this.param2.compareTo(that.param2);
    }
}
