// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.util;

/**
 * A generic immutable Pair class for holding two values of any type.
 */
public class Pair<A, B>{
    private final A left;
    private final B right;
    
    /**
     * Construct the pair.
     * @param a The left side of the pair.
     * @param b The right side of the pair.
     */
    public Pair(final A a, final B b){
        this.left = a;
        this.right = b;
    }
    
    /**
     * Retrieve the left side of the pair.
     * @return The left side.
     */
    public A getLeft() {
        return left;
    }
    
    /**
     * Retrieve the right side of the pair.
     * @return The right side
     */
    public B getRight() {
        return right;
    }
}
