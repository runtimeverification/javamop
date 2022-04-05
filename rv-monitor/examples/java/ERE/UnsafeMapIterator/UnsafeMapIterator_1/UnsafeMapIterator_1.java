
package UnsafeMapIterator_1;

import java.util.*;

 public class UnsafeMapIterator_1 {
   public static void main(String[] args){
    try{
        Map<String, String> testMap = new HashMap<String,String>();
        testMap.put("Foo", "Bar");
        testMap.put("Foo", "Bar");
        Set<String> keys = testMap.keySet();
		rvm.UnsafeMapIteratorRuntimeMonitor.createCollEvent(testMap, keys);
        Iterator i = keys.iterator();
		rvm.UnsafeMapIteratorRuntimeMonitor.createIterEvent(keys,i);
        testMap.put("breaker", "borked");
		rvm.UnsafeMapIteratorRuntimeMonitor.updateMapEvent(testMap);
		rvm.UnsafeMapIteratorRuntimeMonitor.useIterEvent(i);
        System.out.println(i.next());
     }
     catch(Exception e){
        System.out.println("java found the problem too");
     }
   }
 }

