package com.runtimeverification.rvmonitor.logicpluginshells.cfg.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

class Util {

    static String readFile(String path) throws IOException {
        FileInputStream stream = new FileInputStream(new File(path));
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
                    fc.size());
            return Charset.defaultCharset().decode(bb).toString();
        } finally {
            stream.close();
        }
    }

    static <T> HashSet<T> asSet(Collection<T> c) {
        HashSet<T> ret = new HashSet<T>();
        ret.addAll(c);
        return ret;
    }

    static <T> HashSet<ArrayList<T>> inits(ArrayList<T> l) {
        HashSet<ArrayList<T>> ret = new HashSet<ArrayList<T>>();
        ret.add(l);
        for (int i = l.size() - 1; i >= 0; i--) {
            l = (ArrayList<T>) l.clone();
            l.remove(i);
            ret.add(l);
        }
        return ret;
    }

    static <T> HashSet<ArrayList<T>> neinits(ArrayList<T> l) {
        HashSet<ArrayList<T>> ret = inits(l);
        ret.remove(new ArrayList<T>());
        return ret;
    }

    static <T> ArrayList<T> singletonAL(T e) {
        ArrayList<T> ret = new ArrayList<T>();
        ret.add(e);
        return ret;
    }

    static <T> HashSet<T> singletonHS(T e) {
        HashSet<T> ret = new HashSet<T>();
        ret.add(e);
        return ret;
    }

    // Why isn't stack an interface?
    // Also why does ArrayList not have push/pop
    static <T> ArrayList<T> popl(ArrayList<T> l) {
        if (l.isEmpty())
            return null;
        ArrayList<T> ret = singletonAL(l.get(l.size() - 1));
        l.remove(l.size() - 1);
        return ret;
    }

    static HashSet<Terminal> termUnion(HashSet<Terminal> a, HashSet<Terminal> b) {
        HashSet<Terminal> ret = DeepCopy.copy(a);
        ret.addAll(b);
        return ret;
    }

    static <T, A extends T, B extends T> HashSet<T> union(HashSet<A> a,
            HashSet<B> b) {
        HashSet<T> ret = (HashSet<T>) DeepCopy.copy(a);
        ret.addAll(b);
        return ret;
    }

    static <T> ArrayList<T> getBefore(ArrayList<T> l, T e) {
        if (!l.contains(e))
            return null;
        ArrayList<T> ret = new ArrayList<T>();
        for (int i = 0; i < l.indexOf(e); i++)
            ret.add(l.get(i));
        return ret;
    }

    static <T> HashSet<ArrayList<T>> getBeforeS(ArrayList<T> l, T e) {
        if (!l.contains(e))
            return null;
        HashSet<ArrayList<T>> ret = new HashSet<ArrayList<T>>();
        while (l.contains(e)) {
            int i = l.lastIndexOf(e);
            l = subAL(l, 0, i);
            ret.add(l);
        }
        return ret;
    }

    static <T> ArrayList<T> subAL(ArrayList<T> l, int low, int high) {
        if (l == null || low < 0 || high > l.size() || low > high)
            return null;
        ArrayList<T> ret = new ArrayList<T>();
        for (int i = low; i < high; i++)
            ret.add(DeepCopy.copy(l.get(i)));
        return ret;
    }
}
