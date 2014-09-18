// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
import java.util.*;

public class SafeSyncCollection_1 {
    public static void main(String[] args){
        ArrayList<String> list = new ArrayList<String>();
        Collection c = list;
        c = Collections.synchronizedCollection(c);
        
        list.add("Foo");
        list.add("Bar");
        Iterator i = c.iterator();
        while(i.hasNext()){
            System.out.println(i.next());
        }
    }
}
