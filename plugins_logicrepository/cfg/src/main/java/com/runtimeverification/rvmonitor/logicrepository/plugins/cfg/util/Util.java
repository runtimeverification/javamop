package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * Utility methods operating on ArrayLists and HashSets.
 */
public class Util {
    
    /**
     * The contents of the file at the given path.
     * @param path The path to read the file from.
     * @return The contents of the file in a string.
     */
    private static String readFile(String path) throws IOException {
        FileInputStream stream = new FileInputStream(new File(path));
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            return Charset.defaultCharset().decode(bb).toString();
        }
        finally {
            stream.close();
        }
    }
    
    /**
     * Construct the power set of a set.
     * That is, the set of all subsets of a given set. Note that for a set of size n,
     * there are 2^n subsets.
     * @param in The set to compute the power set of.
     * @return The power set of the given parameter.
     */
    public static <T> HashSet<HashSet<T>> powerSet(Set<T> in) {
        HashSet<HashSet<T>> ret = new HashSet<HashSet<T>>();
        ret.add(new HashSet<T>());
        HashSet<HashSet<T>> old = null;
        while (!ret.equals(old)) {
            old = DeepCopy.copy(ret);
            for (HashSet<T> s : old)
                for (T e : in) {
                    HashSet<T> t = DeepCopy.copy(s);
                    t.add(e);
                    ret.add(t);
                }
        }
        return ret;
    }
    
    /**
     * Convert a Collection into a HashSet.
     * @param c A collection of objects.
     * @return A set containing the members of the parameter collection.
     */
    private static <T> HashSet<T> asSet(Collection<T> c) {
        HashSet<T> ret = new HashSet<T>();
        ret.addAll(c);
        return ret;
    }
    
    /**
     * Construct a HashSet of fragments of an ArrayList in increasing size.
     * For example, for an ArrayList containing [0, 1, 2, 3], this contains
     * {[], [0], [0, 1], [0, 1, 2], [0, 1, 2, 3]}.
     * @param l The ArrayList to take the fragments from.
     * @return A HashSet of fragments.
     */
    public static <T> HashSet<ArrayList<T>> inits(ArrayList<T> l) {
        HashSet<ArrayList<T>> ret = new HashSet<ArrayList<T>>();
        ret.add(l);
        for (int i = l.size()-1; i >= 0; i--) {
            l = (ArrayList<T>)l.clone();
            l.remove(i);
            ret.add(l);
        }
        return ret;
    }
    
    /**
     * Construct a HashSet of fragments of an ArrayList in increasing size, without the empty ArrayList.
     * For example, for an ArrayList containing [0, 1, 2, 3], this contains
     * {[0], [0, 1], [0, 1, 2], [0, 1, 2, 3]}.
     * @param l The ArrayList to take the fragments from.
     * @return A HashSet of fragments.
     */
    public static <T> HashSet<ArrayList<T>> neinits(ArrayList<T> l) {
        HashSet<ArrayList<T>> ret = inits(l);
        ret.remove(new ArrayList<T>());
        return ret;
    }
    
    /**
     * Construct an ArrayList containing only one member.
     * @param e The one member of the ArrayList.
     * @return An ArrayList containing only {@code e}.
     */
    public static <T> ArrayList<T> singletonAL(T e) {
        ArrayList<T> ret = new ArrayList<T>();
        ret.add(e);
        return ret;
    }
    
    /**
     * Construct a HashSet containing only one member.
     * @param e The one member of the HashSet.
     * @return a HashSet containing only {@code e}.
     */
    public static <T> HashSet<T> singletonHS (T e) {
        HashSet<T> ret = new HashSet<T>();
        ret.add(e);
        return ret;
    }
    
    /**
     * Pop the highest member off an ArrayList, and return it as an ArrayList.
     * @param l The ArrayList to pop a member off of.
     * @return An ArrayList containing the popped off member.
     */
    public static <T> ArrayList<T> popl(ArrayList<T> l) {
        // Why isn't stack an interface?
        // Also why does ArrayList not have push/pop
        if (l.isEmpty()) {
            return null;
        }
        ArrayList<T> ret = singletonAL(l.get(l.size()-1));
        l.remove(l.size()-1);
        return ret;
    }
    
    /**
     * Construct the union of two sets of terminals.
     * @param a The first set to union.
     * @param b The second set to union.
     * @return The union of the two HashSets.
     */
    public static HashSet<Terminal> termUnion(HashSet<Terminal> a, HashSet<Terminal> b){
        HashSet<Terminal> ret = DeepCopy.copy(a);
        ret.addAll(b);
        return ret;
    }
    
    /**
     * Construct the union of two sets.
     * @param a The first set to union.
     * @param b The second set to union.
     * @return The union of the two HashSets.
     */
    public static <T,A extends T, B extends T> HashSet<T> union(HashSet<A> a, HashSet<B> b) {
        HashSet<T> ret = (HashSet<T>)DeepCopy.copy(a);
        ret.addAll(b);
        return ret;
    }
    
    /**
     * Construct an ArrayList containing the members of another ArrayList up to but not including a specific member.
     * @param l The ArrayList to select elements from.
     * @param e The element to stop at when selecting members.
     * @return An array list with members from {@code l} up to {@code e}.
     */
    public static <T> ArrayList<T> getBefore(ArrayList<T> l, T e) {
        if (!l.contains(e)) {
            return null;
        }
        ArrayList<T> ret = new ArrayList<T>();
        for (int i = 0; i < l.indexOf(e); i++) {
            ret.add(l.get(i));
        }
        return ret;
    }
    
    /**
     * Construct a HashSet of all ArrayLists containing all members from 0 to every index of {@code e}.
     * @param l The ArrayList to select members from.
     * @param e The member to stop at when selecting members.
     * @return A HashSet of fragment ArrayLists.
     */
    public static <T> HashSet<ArrayList<T>> getBeforeS(ArrayList<T> l, T e) {
        if (!l.contains(e)) {
            return null;
        }
        HashSet<ArrayList<T>> ret = new HashSet<ArrayList<T>>();
        while (l.contains(e)) {
            int i = l.lastIndexOf(e);
            l = subAL(l,0,i);
            ret.add(l);
        }
        return ret;
    }
    
    /**
     * Construct a fragment ArrayList by copying elements from an existing ArrayList.
     * @param l The ArrayList to select members from.
     * @param low The place to start selecting members from.
     * @param high The index to stop inserting members at. Not included.
     */
    private static <T> ArrayList<T> subAL(ArrayList<T> l, int low, int high) {
        if (l == null || low < 0 || high > l.size() || low > high) {
            return null;
        }
        ArrayList<T> ret = new ArrayList<T>();
        for (int i = low; i < high; i++) {
            ret.add(DeepCopy.copy(l.get(i)));
        }
        return ret;
    }
}
