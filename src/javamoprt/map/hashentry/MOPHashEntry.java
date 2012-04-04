package javamoprt.map.hashentry;

import javamoprt.ref.MOPWeakReference;

public class MOPHashEntry {
	public MOPHashEntry next;
	public int hashCode;
	public MOPWeakReference key;
	public Object value;

	public MOPHashEntry(MOPHashEntry next, int hashCode, MOPWeakReference keyref) {
		this.next = next;
		this.hashCode = hashCode;
		this.key = keyref;
		this.value = null;
	}

	public MOPHashEntry(MOPHashEntry next, int hashCode, MOPWeakReference keyref, Object value) {
		this.next = next;
		this.hashCode = hashCode;
		this.key = keyref;
		this.value = value;
	}

}
