package HashSet_2;

import java.util.*;

public class HashSet_2 {
	static class Item {
		String value;

		public Item(String v) {
			this.value = v;
		}

		public void update(String v) {
			this.value = v;
		}

		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null || obj.getClass() != this.getClass()) return false;

			Item that = (Item)obj;
			return this.value.equals(that.value);
		}

		public int hashCode() {
			return this.value.hashCode();
		}
	}

	public static void main(String[] args) {
		HashSet<Item> s = new HashSet<Item>();

		Item item = new Item("hello");
//		System.out.println("initial hashcode " + item.hashCode());

		s.add(item);
		rvm.HashSetRuntimeMonitor.addEvent(s, item);
		rvm.HashSetRuntimeMonitor.unsafe_containsEvent(s, item);
		System.out.println("contains 1 : " + s.contains(item));

		s.remove(item);
		rvm.HashSetRuntimeMonitor.removeEvent(s, item);

		// The hashcode of "item" object will be modified.
		item.update("world");
//		System.out.println("updated hashcode " + item.hashCode());

		// "contains" method should not trigger "unsafe_contains" event
		// because "item" was removed from this set.
		rvm.HashSetRuntimeMonitor.unsafe_containsEvent(s, item);
		System.out.println("contains 2 : " + s.contains(item));
	}
}

