package javamoprt.map.hashentry;

import javamoprt.ref.MOPWeakReference;

public class MOPHashDualEntry {
	public MOPHashDualEntry next;
	
	public MOPWeakReference key;
	public int hashCode;
	
	public Object value1 = null;
	public Object value2 = null;

	public MOPHashDualEntry(MOPHashDualEntry next, int hashCode, MOPWeakReference keyref) {
		this.next = next;
		this.hashCode = hashCode;
		this.key = keyref;
	}

}
