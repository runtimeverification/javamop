package com.runtimeverification.rvmonitor.java.rvj.output;

public class CodeGenerationOption {
    /**
     * When this field is true, a cache key of an indexing tree becomes of weak
     * reference type. Otherwise, the type would be a strong reference. That is,
     * the resulting code is, when this field is true: <code>
     * public class RuntimeMonitor {
     *   WeakRef cacheKey_p;
     *   WeakRef cacheKey_q;
     *   public static void event1(P p, Q q) {
     *     WeakRef wr_p;
     *     if (cacheKey_p.get() == p && cacheKey_q.get() == q) {
     *       wr_p = cacheKey_p;
     *       wp_q = cacheKey_q;
     *       ...
     *     }
     *     else {
     *       wr_p = findOrCreateWeakRef(p);
     *       wr_q = findOrCreateWeakRef(q);
     *       ...
     *     }
     *     ...
     *   }
     *   ...
     * }
     * </code> When this field is false, the resulting code is: <code>
     * public class RuntimeMonitor {
     *   P cacheKey_p = null;
     *   Q cacheKey_q = null;
     *   public static void event1(P p, Q q) {
     *     WeakRef wr_p = null;
     *     if (cacheKey_p == p && cacheKey_q == q) {
     *       ...
     *     }
     *     else {
     *       wr_p = findOrCreateWeakRef(p);
     *       wr_q = findOrCreateWeakRef(q);
     *       ...
     *     }
     *     ...
     *     if something should be created {
     *       if (wr_p == null)
     *         wr_p = findOrCreateWeakRef(p);
     *       if (wr_q == null)
     *         wr_q = findOrCreateWeakRef(q);
     *       ...
     *     }
     *   }
     *   ...
     * }
     * </code> Advantages of using weak references include: - Cache keys do not
     * prevent the JVM from garbage-collecting keys. - Code can be simpler. -
     * Local variables for weak references do not need to be initialized to be
     * null. - When something needs to be created, no additional operation is
     * needed for weak references, unlike the other case, where checking and
     * calling 'findOrCreateWeakRef' is needed to make sure weak references are
     * assigned to local variables. In contrast, advantages of using strong
     * references include: - Checking the cache hit requires less operations;
     * one == operation per parameter would suffice, unlike the other case,
     * where extra get() is required. - When cache hits and nothing needs to be
     * created, there is no need to assign weak references to local variables,
     * which can save a few operations per parameter.
     */
    private static boolean cacheKeyWeakReference;

    public static boolean isCacheKeyWeakReference() {
        return cacheKeyWeakReference;
    }

    public static void setCacheKeyWeakReference(boolean on) {
        cacheKeyWeakReference = on;
    }

    public static void initialize() {
        cacheKeyWeakReference = false;
    }
}
