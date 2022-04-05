
package SafeSyncMap_2;

import java.util.*;

public class SafeSyncMap_2 {
	public static void main(String[] args){
	  Map<String,String> testMap = new HashMap<String,String>();
      testMap = Collections.synchronizedMap(testMap);
		rvm.SafeSyncMapRuntimeMonitor.syncEvent(testMap);
      testMap.put("Foo", "Bar");
      testMap.put("Bar", "Bar");
	  Set<String> keys = testMap.keySet();
		rvm.SafeSyncMapRuntimeMonitor.createSetEvent(testMap, keys);
		Iterator i = keys.iterator();
		rvm.SafeSyncMapRuntimeMonitor.asyncCreateIterEvent(keys, i);
		rvm.SafeSyncMapRuntimeMonitor.syncCreateIterEvent(keys, i);
		rvm.SafeSyncMapRuntimeMonitor.accessIterEvent(i);
		while(i.hasNext()){
			rvm.SafeSyncMapRuntimeMonitor.accessIterEvent(i);
			System.out.println(testMap.get(i.next()));
			rvm.SafeSyncMapRuntimeMonitor.accessIterEvent(i);
		}
	}
}
