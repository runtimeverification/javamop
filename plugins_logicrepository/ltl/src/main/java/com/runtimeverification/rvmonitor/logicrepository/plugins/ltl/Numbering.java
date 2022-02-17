package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * A class that manages a unique enumeration for a set of objects. Each object is assigned a
 * distinct index once and keeps that index.
 */
public class Numbering<Key> {
    private HashMap<Key, Integer> map = new HashMap<Key, Integer>();
    private int count;
    
    /**
     * Retrieve the index used with a particular object. Creates a new index if there is none
     * associated with the object.
     * @param k The object to find the index of.
     * @return The index of the object.
     */
    public Integer get(Object k){
        if(map.containsKey(k)) {
            return map.get(k);
        }
        map.put((Key)k, count);
        return count++;
    }
    
    /**
     * Map a set of objects to their matching indexes. Assigns indexes to any objects that
     * do not yet have assigned ones.
     * @param set The set of elements to procude the indexes of.
     * @return The indexes of the elements in {@code set}.
     */
    public LinkedHashSet<Integer> map(LinkedHashSet<Key> set){
        LinkedHashSet<Integer> ret = new LinkedHashSet();
        for(Key item : set){
            ret.add(get(item)); 
        } 
        return ret;
    }
} 
