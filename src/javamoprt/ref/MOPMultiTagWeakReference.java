package javamoprt.ref;

import java.lang.ref.ReferenceQueue;



public class MOPMultiTagWeakReference extends MOPWeakReference{
	
	public long[] disable;
	public long[] tau;

	public MOPMultiTagWeakReference(int taglen, Object r) {
		super(r);
		disable = new long[taglen];
		tau = new long[taglen];
	}

	public MOPMultiTagWeakReference(int taglen, Object r, int hash) {
		super(r, hash);
		disable = new long[taglen];
		tau = new long[taglen];
	}

	public MOPMultiTagWeakReference(int taglen, Object r, int hash, ReferenceQueue<Object> q) {
		super(r, hash, q);
		disable = new long[taglen];
		tau = new long[taglen];
	}

}
