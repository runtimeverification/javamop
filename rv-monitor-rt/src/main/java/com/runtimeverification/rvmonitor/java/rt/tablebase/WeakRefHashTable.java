package com.runtimeverification.rvmonitor.java.rt.tablebase;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;

import com.runtimeverification.rvmonitor.java.rt.RuntimeOption;
import com.runtimeverification.rvmonitor.java.rt.ref.CachedWeakReference;
import com.runtimeverification.rvmonitor.java.rt.tablebase.annotation.ThreadSafety;
import com.runtimeverification.rvmonitor.java.rt.tablebase.annotation.ThreadSafety.Safety;

/**
 * This class is the base of one level of an indexing tree or global weak reference table (GWRT).
 * It implements all the necessary features; its subclasses simply qualify types to prevent the
 * generated code from wrong uses.
 * 
 * Each level in an indexing tree is a map, where the key being a weak reference and the value
 * being one of the followings:
 * 1. a nested level
 * 2. a set of monitors
 * 3. a monitor
 * 4. a Tuple2 instance for holding two of 1. 2. and 3.
 * 5. a Tuple3 instance for holding all of 1. 2. and 3.
 * 
 * Like a map, this implementation supports get and put operations. More specifically,
 * 1. putNode() or putNodeUnconditional() to add a new entry to this map
 * 2. putNodeAdditive() to update an existing entry in this map
 * 3. getNode() to retrieve an entry using a weak reference
 * 4. getNodeEquivalent() to retrieve an entry using a weak reference
 * 5. getNodeWithStrongRef() to retrieve an entry using a strong reference
 * 
 * The difference between 3. and 4. is that 3. assumes that weak reference interning is enabled and,
 * consequently, it is correct to check the equivalence of two weak references using reference equality.
 * Unlike 3., 4. does not assume that, and checks whether their referents are the same.
 * 
 * Additionally, this class implements GWRT, where one can retrieve weak references by invoking
 * findOrCreateWeakRefInternal().
 * 
 * The implementation is similar to HashMap, in the sense that it has multiple buckets and an entry
 * is distributed according to its hash value. A bucket is represented by an instance of Bucket.
 * The number of buckets is adjusted, at runtime, when it turns out that a bucket contains too many entries.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 * @see Bucket
 *
 * @param <TWeakRef> type of the key in this level
 * @param <TValue> type of the value in this level
 */
public abstract class WeakRefHashTable<TWeakRef extends CachedWeakReference, TValue extends IIndexingTreeValue> implements INodeOperation<TWeakRef, TValue> {
	protected final int treeid;
	private final TupleTrait<TValue> valueTrait;
	private final AtomicReferenceArray<Segment<TWeakRef, TValue>> segments;
	private final CacheEntry<TWeakRef, TValue> cacheWeakRef;
	
	/**
	 * This must be a power of 2.
	 */
	private static final int NUM_SEGMENTS = 16;
	/**
	 * This number should correspond to NUM_SEGMENTS.
	 */
	private static final int NUM_SEGMENTS_BITS = 4;
	
	static final class Segment<TWeakRef extends CachedWeakReference, TValue extends IIndexingTreeValue> extends ReentrantLock {
		private WeakRefHashTable<TWeakRef, TValue> enclosing;
		private ArrayList<Bucket<TWeakRef, TValue>> buckets;
		private int defaultBucketCapacity = 2;
		
		private static final long serialVersionUID = 1004720745644334860L;
		private static final int INITIAL_CAPACITY = 2;
		
		Segment(WeakRefHashTable<TWeakRef, TValue> enclosing) {
			this.enclosing = enclosing;
			this.resizeBucketSize(this.getNextBucketSize());
		}
		
		private final int getNextBucketSize() {
			if (this.buckets == null)
				return INITIAL_CAPACITY;
			else
				return this.buckets.size() * 2;
		}
		
		/**
		 * Resizes the bucket size.
		 * The caller should make sure that this method has exclusive access.
		 * @param newsize
		 */
		private final void resizeBucketSize(int newsize) {
			ArrayList<Bucket<TWeakRef, TValue>> oldbuckets = this.buckets; 

			ArrayList<Bucket<TWeakRef, TValue>> newbuckets = new ArrayList<Bucket<TWeakRef, TValue>>(newsize);
			for (int i = 0; i < newsize; ++i)
				newbuckets.add(null);

			if (oldbuckets != null) {
				for (Bucket<TWeakRef, TValue> bucket : oldbuckets) {
					if (bucket == null)
						continue;
					Bucket<TWeakRef, TValue>.PairIterator it = bucket.iterator();
					while (it.moveNext()) {
						if (bucket.terminateIfReclaimed(it.getKey(), it.getValue()))
							continue;
						Bucket<TWeakRef, TValue> target = this.getOrCreateBucket(newbuckets, it.getKey());
						target.add(it.getKey(), it.getValue());
					}
				}
			}
			
			this.buckets = newbuckets;
		}
		
		private final void increaseBuckets() {
			this.resizeBucketSize(this.getNextBucketSize());
		}
		
		private final Bucket<TWeakRef, TValue> getBucket(TWeakRef key) {
			return this.getBucket(this.buckets, key.hashCode());
		}
		
		private final Bucket<TWeakRef, TValue> getBucket(int hashval) {
			return this.getBucket(this.buckets, hashval);
		}
		
		private final Bucket<TWeakRef, TValue> getBucket(ArrayList<Bucket<TWeakRef, TValue>> buckets, int hashval) {
			int index = this.getBucketIndex(buckets, hashval);
			return buckets.get(index);
		}
		
		private final Bucket<TWeakRef, TValue> getOrCreateBucket(TWeakRef key) {
			return this.getOrCreateBucket(this.buckets, key.hashCode());
		}

		private final Bucket<TWeakRef, TValue> getOrCreateBucket(int hashval) {
			return this.getOrCreateBucket(this.buckets, hashval);
		}

		private final Bucket<TWeakRef, TValue> getOrCreateBucket(ArrayList<Bucket<TWeakRef, TValue>> buckets, TWeakRef key) {
			return this.getOrCreateBucket(buckets, key.hashCode());
		}
		
		private final Bucket<TWeakRef, TValue> getOrCreateBucket(ArrayList<Bucket<TWeakRef, TValue>> buckets, int hashval) {
			int index = this.getBucketIndex(buckets, hashval);
			Bucket<TWeakRef, TValue> bucket = buckets.get(index);
			if (bucket != null)
				return bucket;
			
			bucket = new Bucket<TWeakRef, TValue>(this.enclosing.treeid, this.defaultBucketCapacity);
			buckets.set(index, bucket);
			return bucket;
		}
		
		private final int getBucketIndex(ArrayList<Bucket<TWeakRef, TValue>> buckets, int hashval) {
			int remaining = hashval >> NUM_SEGMENTS_BITS;
			return remaining & (buckets.size() - 1);
		}
	
		@ThreadSafety(safety=Safety.SAFE)
		public final TValue findByWeakRef(TWeakRef key) {
			this.lock();
			try {
				Bucket<TWeakRef, TValue> bucket = this.getBucket(key);
				if (bucket == null)
					return null;
				return bucket.findByWeakRef(key);
			}
			finally {
				this.unlock();
			}
		}

		@ThreadSafety(safety=Safety.SAFE)
		public final TValue findByStrongRef(int hashval, Object key) {
			this.lock();
			try {
				Bucket<TWeakRef, TValue> bucket = this.getBucket(hashval);
				if (bucket == null)
					return null;
				return bucket.findByStrongRef(key);
			}
			finally {
				this.unlock();
			}
		}

		@ThreadSafety(safety=Safety.SAFE)
		public final void updateOrAdd(TupleTrait<TValue> trait, TWeakRef key, TValue value, boolean additive, int valueflag) {
			this.lock();
			try {
				Bucket<TWeakRef, TValue> bucket = this.getOrCreateBucket(key);
				if (bucket.updateOrAdd(trait, key, value, additive, valueflag)) {
					if (bucket.isSaturated())
						this.onSaturated();
				}
			}
			finally {
				this.unlock();
			}
		}
		
		@ThreadSafety(safety=Safety.SAFE)
		public TWeakRef findOrCreateWeakRef(int hashval, Object key, boolean create) {
			this.lock();
			try {
				Bucket<TWeakRef, TValue> bucket = this.getOrCreateBucket(hashval);
	
				TWeakRef weakref = bucket.findWeakRef(key);
				if (weakref != null)
					return weakref;
		
				if (create) {
					weakref = this.enclosing.createWeakRef(key, hashval);
					bucket.add(weakref, null);
					if (bucket.isSaturated())
						this.onSaturated();
				}
				return weakref;
			}
			finally {
				this.unlock();
			}
		}

		/**
		 * Terminates all the values contained in this map. This will
		 * eventually terminate all the monitors reached by this map.
		 * @param treeid tree id
		 */
		@ThreadSafety(safety=Safety.SAFE)
		public void terminateValues(int treeid) {
			this.lock();
			try {
				for (Bucket<TWeakRef, TValue> bucket : this.buckets) {
					if (bucket == null)
						continue;
					bucket.terminateValues();
				}
			}
			finally {
				this.unlock();
			}
		}
		
		private final void onSaturated() {
			this.increaseBuckets();
		}
		
		@ThreadSafety(safety=Safety.SAFE)
		public final int cleanUpUnnecessaryMappings() {
			this.lock();
			try {
				int removed = 0;
				for (Bucket<TWeakRef, TValue> bucket : this.buckets) {
					if (bucket == null)
						continue;
					int n = bucket.cleanUpUnnecessaryMappings();
					removed += n;
				}
				return removed;
			}
			finally {
				this.unlock();
			}
		}
	
		@Override
		public String toString() {
			StringBuilder r = new StringBuilder();
			int i = 0;
			for (Bucket<TWeakRef, TValue> bucket : this.buckets) {
				r.append("[");
				r.append(i);
				r.append("] ");
				if (bucket != null)
					r.append(bucket.toString());
				r.append("\n");
				
				++i;
			}
			return r.toString();
		}
	}
	
	protected WeakRefHashTable(int treeid, TupleTrait<TValue> valuetrait) {
		this.treeid = treeid;
		this.valueTrait = valuetrait;

		@SuppressWarnings("unchecked")
		Segment<TWeakRef, TValue>[] segs = new Segment[NUM_SEGMENTS];
		this.segments = new AtomicReferenceArray<Segment<TWeakRef, TValue>>(segs);

		if (RuntimeOption.isFineGrainedLockEnabled())
			this.cacheWeakRef = new ThreadLocalCacheEntry<TWeakRef, TValue>();
		else
			this.cacheWeakRef = new OrdinaryCacheEntry<TWeakRef, TValue>();
	}
	
	private final int getSegmentIndex(int hashval) {
		return hashval & (NUM_SEGMENTS - 1);
	}
	
	private final Segment<TWeakRef, TValue> getSegment(int hashval) {
		int index = this.getSegmentIndex(hashval);
		return this.segments.get(index);
	}
	
	private final Segment<TWeakRef, TValue> getOrCreateSegment(TWeakRef key) {
		int hashval = key.hashCode();
		return this.getOrCreateSegment(hashval);
	}

	private final Segment<TWeakRef, TValue> getOrCreateSegment(int hashval) {
		int index = this.getSegmentIndex(hashval);
		Segment<TWeakRef, TValue> oldseg = this.segments.get(index);
		
		if (oldseg != null)
			return oldseg;
		
		Segment<TWeakRef, TValue> newseg = new Segment<TWeakRef, TValue>(this);
		oldseg = this.segments.getAndSet(index, newseg);
		return oldseg == null ? newseg : oldseg;
	}
	
	@Override
	public final TValue getNode(TWeakRef key) {
		int hashval = key.hashCode();
		Segment<TWeakRef, TValue> segment = this.getSegment(hashval);
		if (segment == null)
			return null;
		return segment.findByWeakRef(key);
	}
	
	private final TValue getNodeEquivalentInternal(int hashval, Object key) {
		Segment<TWeakRef, TValue> segment = this.getSegment(hashval);
		if (segment == null)
			return null;
		return segment.findByStrongRef(hashval, key);
	}
	
	@Override
	public final TValue getNodeEquivalent(TWeakRef key) {
		int hashval = key.hashCode();
		return this.getNodeEquivalentInternal(hashval, key.get());
	}
	
	@Override
	public final TValue getNodeWithStrongRef(Object key) {
		int hashval = System.identityHashCode(key);
		return this.getNodeEquivalentInternal(hashval, key);
	}

	private final void putNodeInternal(TWeakRef key, TValue value, boolean additive, int valueflag) {
		Segment<TWeakRef, TValue> segment = this.getOrCreateSegment(key);
		segment.updateOrAdd(this.valueTrait, key, value, additive, valueflag);
	}
	
	protected final void putNodeUnconditional(TWeakRef key, TValue value) {
		this.putNodeInternal(key, value, false, 0);
	}
	
	protected final void putNodeAdditive(TWeakRef key, TValue value, int valueflag) {
		this.putNodeInternal(key, value, true, valueflag);
	}

	@Override
	public final void putNode(TWeakRef key, TValue value) {
		this.putNodeInternal(key, value, false, 0);
	}
	
	/**
	 * Finds or creates (only if 'create' is true) the weak reference that corresponds to
	 * the given key.
	 * @param key the strong reference
	 * @param create true if the weak reference should be created when there is none
	 * @return found or created weak reference
	 */
	protected final TWeakRef findOrCreateWeakRefInternal(Object key, boolean create) {
		{
			TWeakRef weakref = this.cacheWeakRef.getWeakRef(key);
			if (weakref != null)
				return weakref;
		}

		int hashval = System.identityHashCode(key);

		Segment<TWeakRef, TValue> segment = this.getSegment(hashval);
		if (segment == null) {
			if (create)
				segment = this.getOrCreateSegment(hashval);
			else {
				this.cacheWeakRef.invalidate();
				return null;
			}
		}

		TWeakRef weakref = segment.findOrCreateWeakRef(hashval, key, create);
		if (weakref == null)
			this.cacheWeakRef.invalidate();
		else
			this.cacheWeakRef.set(key, weakref);
		return weakref;
	}

	protected abstract TWeakRef createWeakRef(Object key, int hashval);
	
	protected final void terminateValues(int treeid) {
		for (int i = 0; i < this.segments.length(); ++i) {
			Segment<TWeakRef, TValue> segment = this.segments.get(i);
			if (segment != null)
				segment.terminateValues(treeid);
		}
	}
	
	public final int cleanUpUnnecessaryMappings() {
		int removed = 0;
		for (int i = 0; i < this.segments.length(); ++i) {
			Segment<TWeakRef, TValue> segment = this.segments.get(i);
			if (segment == null)
				continue;
			int n = segment.cleanUpUnnecessaryMappings();
			removed += n;
		}
		return removed;
	}
	
	@Override
	public String toString() {
		StringBuilder r = new StringBuilder();
		for (int i = 0; i < this.segments.length(); ++i) {
			Segment<TWeakRef, TValue> segment = this.segments.get(i);
			r.append("Segment [");
			r.append(i);
			r.append("] ");
			if (segment != null)
				r.append(segment.toString());
			r.append("\n");
		}
		return r.toString();
	}
}

/**
 * This class represents the GWRT cache.
 * Two different implementations are available: one for ordinary cache, the other for
 * thread-specific cache. The latter was introduced not to reduce concurrency.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 *
 * @param <TWeakRef> type of the key
 * @param <TValue> type of the value
 */
abstract class CacheEntry<TWeakRef extends CachedWeakReference, TValue> {
	public abstract Object getRef();
	public abstract TWeakRef getWeakRef(Object key);
	
	public abstract void set(Object ref, TWeakRef weakref);
	public abstract void invalidate();
	
	protected CacheEntry() {
	}
}

final class OrdinaryCacheEntry<TWeakRef extends CachedWeakReference, TValue> extends CacheEntry<TWeakRef, TValue> {
	private Object ref;
	private TWeakRef weakref;
	
	@Override
	public final Object getRef() {
		return this.ref;
	}
	
	@Override
	public final TWeakRef getWeakRef(Object key) {
		if (key == this.ref)
			return this.weakref;
		return null;
	}
	
	@Override
	public final void set(Object ref, TWeakRef weakref) {
		this.ref = ref;
		this.weakref = weakref;
	}
	
	@Override
	public final void invalidate() {
		this.ref = null;
		this.weakref = null;
	}
}

final class ThreadLocalCacheEntry<TWeakRef extends CachedWeakReference, TValue> extends CacheEntry<TWeakRef, TValue> {
	protected final ThreadLocal<OrdinaryCacheEntry<TWeakRef, TValue>> tls = new ThreadLocal<OrdinaryCacheEntry<TWeakRef, TValue>>() {
		@Override protected OrdinaryCacheEntry<TWeakRef, TValue> initialValue() {
			return new OrdinaryCacheEntry<TWeakRef, TValue>();
		}
	};
	
	@Override
	public final Object getRef() {
		return this.tls.get().getRef();
	}
	
	@Override
	public final TWeakRef getWeakRef(Object key) {
		return this.tls.get().getWeakRef(key);
	}
	
	@Override
	public final void set(Object ref, TWeakRef weakref) {
		this.tls.get().set(ref, weakref);
	}
	
	@Override
	public final void invalidate() {
		this.tls.get().invalidate();
	}
}
