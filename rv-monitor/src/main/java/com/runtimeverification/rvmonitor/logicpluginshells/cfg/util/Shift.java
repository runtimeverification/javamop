package com.runtimeverification.rvmonitor.logicpluginshells.cfg.util;

class Shift extends LRAction {
    int target;

    Shift(int t) {
        target = t;
    }

    @Override
    public int hashCode() {
        return target;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof Shift))
            return false;
        return target == (((Shift) o).target);
    }

    @Override
    public String toString() {
        return "Shift " + target;
    }

    @Override
    ActType type() {
        return ActType.SHIFT;
    }
}
