package com.runtimeverification.rvmonitor.logicrepository.plugins.srs;

import java.util.Collection;

public interface SLIterator<E> {
    public SLIterator<E> copy();
    public boolean next();
    public boolean next(int amount);
    public boolean previous();
    public boolean previous(int amount);
    public E get();
    public void splice(SLIterator<E> end, SpliceList<E> replacement);
    public void nonDestructiveSplice(SLIterator<E> end, SpliceList<E> replacement);
    public void nonDestructiveSplice(SLIterator<E> end, Collection<E> replacement);
}

