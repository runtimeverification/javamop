package SafeMapIterator_1;

import java.util.*;

 public class SafeMapIterator_1 {
   public static void main(String[] args){
    try{
        Map<String, String> testMap = new HashMap<String,String>();
        testMap.put("Foo", "Bar");
		rvm.SafeMapIteratorRuntimeMonitor.updateMapEvent(testMap);
        testMap.put("Foo", "Bar");
		rvm.SafeMapIteratorRuntimeMonitor.updateMapEvent(testMap);
        Set<String> keys = testMap.keySet();
		rvm.SafeMapIteratorRuntimeMonitor.createCollEvent(testMap, keys);
        Iterator i = keys.iterator();
		rvm.SafeMapIteratorRuntimeMonitor.createIterEvent(keys, i);
        testMap.put("breaker", "borked");
		rvm.SafeMapIteratorRuntimeMonitor.updateMapEvent(testMap);
		rvm.SafeMapIteratorRuntimeMonitor.useIterEvent(i);
        System.out.println(i.next());
     }
     catch(Exception e){
        System.out.println("java found the problem too");
     }
   }
 }

