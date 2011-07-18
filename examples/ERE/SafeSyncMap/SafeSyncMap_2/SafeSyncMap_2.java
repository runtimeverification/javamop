import java.util.*;

public class SafeSyncMap_2 {
	public static void main(String[] args){
	  Map<String,String> testMap = new HashMap<String,String>();
      testMap = Collections.synchronizedMap(testMap);
      testMap.put("Foo", "Bar");
      testMap.put("Bar", "Foo");
	  Set<String> keys = testMap.keySet();
	  Iterator i = keys.iterator();
      while(i.hasNext()){
        System.out.println(testMap.get(i.next()));
	  }
	}
}
