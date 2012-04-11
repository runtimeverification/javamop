package javamoprt.map.hashentry;

import javamoprt.ref.MOPWeakReference;

public class MOPHashRefEntry {
	public MOPHashRefEntry next;
	public MOPWeakReference key;

	public MOPHashRefEntry(MOPHashRefEntry next, MOPWeakReference keyref) {
		this.next = next;
		this.key = keyref;
	}

}