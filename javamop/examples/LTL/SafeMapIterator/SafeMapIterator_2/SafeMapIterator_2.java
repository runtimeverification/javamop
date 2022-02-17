// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
import java.util.*;

public class SafeMapIterator_2 {
    public static void main(String[] args){
        try{
            Map<String, String> testMap = new LinkedHashMap<String,String>();
            testMap.put("Foo", "Bar");
            testMap.put("Bar", "Foo");
            Set<String> keys = testMap.keySet();
            Iterator i = keys.iterator();
            System.out.println(i.next());
        }
        catch(Exception e){
            System.out.println("java found the problem too");
        }
    }
}
