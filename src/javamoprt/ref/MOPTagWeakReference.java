package javamoprt.ref;

import java.lang.ref.ReferenceQueue;


public class MOPTagWeakReference extends MOPWeakReference{
	
  	public long disable = -1;
	public long tau = -1;
	
	public MOPTagWeakReference(Object r) {
		super(r);
	}

	public MOPTagWeakReference(Object r, int hash) {
		super(r, hash);
	}

	public MOPTagWeakReference(Object r, int hash, ReferenceQueue<Object> q) {
		super(r, hash, q);
	}

}
