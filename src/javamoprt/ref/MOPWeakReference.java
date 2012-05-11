package javamoprt.ref;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import javamoprt.MOPObject;

public class MOPWeakReference extends WeakReference<Object> implements MOPObject {
	public int hash = 0;
	
	public MOPWeakReference(Object r) {
		super(r);
		this.hash = System.identityHashCode(r);
	}

	public MOPWeakReference(Object r, int hash) {
		super(r);
		this.hash = hash;
	}

	public MOPWeakReference(Object r, int hash, ReferenceQueue<Object> q) {
		super(r, q);
		this.hash = hash;
	}

	public int hashCode() {
		return hash;
	}
}
