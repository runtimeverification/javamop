// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
import java.util.*;

public class SafeSyncCollection_2 {
    public static void main(String[] args){
        ArrayList<String> list = new ArrayList<String>();
        list.add("Foo");
        list.add("Bar");
        
        Collection c = list;
        c = Collections.synchronizedCollection(list);
        
        Iterator i = null;
        synchronized(c){
            i = c.iterator();
        }
        
        System.out.println("lists---");
        while(i.hasNext()){
            System.out.println(i.next());
        }
    }
}
