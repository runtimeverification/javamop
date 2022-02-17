// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
import java.util.*;

public class HashSet_1 {
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
        //      System.out.println("initial hashcode " + item.hashCode());
        
        s.add(item);
        System.out.println("contains 1 : " + s.contains(item));
        
        // The hashcode of "item" object will be modified.
        item.update("world");
        //      System.out.println("updated hashcode " + item.hashCode());
        
        // "contains" method should trigger "unsafe_contains" event.
        System.out.println("contains 2 : " + s.contains(item));
    }
}
