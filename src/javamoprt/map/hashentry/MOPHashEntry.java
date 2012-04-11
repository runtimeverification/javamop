package javamoprt.map.hashentry;

import javamoprt.ref.MOPWeakReference;

public class MOPHashEntry {
	public MOPHashEntry next;
	public MOPWeakReference key;
	public Object value;

	public MOPHashEntry(MOPHashEntry next, MOPWeakReference keyref) {
		this.next = next;
		this.key = keyref;
		this.value = null;
	}

	public MOPHashEntry(MOPHashEntry next, MOPWeakReference keyref, Object value) {
		this.next = next;
		this.key = keyref;
		this.value = value;
	}

}
