package com.runtimeverification.rvmonitor.logicrepository.plugins.ere;

//class representing a repeated symbol in an ERE
/**
 * An ERE representing another ERE repeated a fixed number of times.
 */
public class Repeat {
    
    /**
     * Acquire an ERE that matches another ERE a fixed number of times.
     * @param child The ERE to match multiple times.
     * @param num The number of times to match the child ERE.
     * @return An ERE matching {@code child @endcode} {@code num @endcode} times.
     */
    public static ERE get(ERE child, int num) {
        /* 
         * We won't even use derive here or even
         * subclass ERE, we just immediately 
         * return a new concatenation list of the child repeated
         * num times.
         */
        if(num < 1) {
            return Empty.get();
        } else if(num == 1) {
            return child;
        } else {
            ERE ret = Concat.get(child, child);
            for(int i = 2; i < num; ++i) {
                ret = Concat.get(child, ret); 
            } 
            return ret;
        }
    }
}
