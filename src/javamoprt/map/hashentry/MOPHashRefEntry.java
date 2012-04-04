package javamoprt.map.hashentry;

import javamoprt.ref.MOPWeakReference;

public class MOPHashRefEntry {
	public MOPHashRefEntry next;
	public int hashCode;
	public MOPWeakReference key;

	public MOPHashRefEntry(MOPHashRefEntry next, int hashCode, MOPWeakReference keyref) {
		this.next = next;
		this.hashCode = hashCode;
		this.key = keyref;
	}

}