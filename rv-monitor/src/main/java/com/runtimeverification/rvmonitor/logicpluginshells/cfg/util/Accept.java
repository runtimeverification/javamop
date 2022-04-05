package com.runtimeverification.rvmonitor.logicpluginshells.cfg.util;

class Accept extends LRAction {
    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof Accept))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Accept";
    }

    @Override
    ActType type() {
        return ActType.ACCEPT;
    }
}
