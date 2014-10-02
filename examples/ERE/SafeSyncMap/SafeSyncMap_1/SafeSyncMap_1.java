// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
import java.util.*;

public class SafeSyncMap_1 {
    public static void main(String[] args){
        Map<String,String> testMap = new LinkedHashMap<String,String>();
        testMap = Collections.synchronizedMap(testMap);
        synchronized (testMap) {
            testMap.put("Foo", "Bar");
            testMap.put("Bar", "Foo");
            Set<String> keys = testMap.keySet();
            Iterator i = keys.iterator();
            while(i.hasNext()){
                System.out.println(testMap.get(i.next()));
            }
        }
    }
}
