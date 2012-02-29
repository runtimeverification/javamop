package javamoprt.ver24;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import javamoprt.MOPObject;

public class MOPWeakReference<V> extends WeakReference<V> implements MOPObject {
	protected int hash = 0;

	public MOPWeakReference(V r) {
		super(r);
		this.hash = System.identityHashCode(r);
	}

	public MOPWeakReference(V r, int hash) {
		super(r);
		this.hash = hash;
	}

	public MOPWeakReference(V r, int hash, ReferenceQueue q) {
		super(r, q);
		this.hash = hash;
	}

	public int hashCode() {
		return hash;
	}
}
