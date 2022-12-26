package com.runtimeverification.rvmonitor.logicpluginshells.cfg.util;

class Reduce extends LRAction {
    int nt;
    int size;

    Reduce(int oldnt, int oldsize) {
        nt = oldnt;
        size = oldsize;
    }

    @Override
    public int hashCode() {
        return nt + size;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof Reduce))
            return false;
        return nt == (((Reduce) o).nt) && size == (((Reduce) o).size);
    }

    @Override
    public String toString() {
        return "Reduce " + nt + " " + size;
    }

    @Override
    ActType type() {
        return ActType.REDUCE;
    }
}
