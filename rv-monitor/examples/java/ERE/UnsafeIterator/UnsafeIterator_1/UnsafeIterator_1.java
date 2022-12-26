
package UnsafeIterator_1;

import java.util.*;
import rvm.UnsafeIteratorRuntimeMonitor;


public class UnsafeIterator_1 {
  public static void main(String[] args){
    Set<Integer> testSet = new HashSet<Integer>(); 
    for(int i = 0; i < 10; ++i){
      testSet.add(new Integer(i));
    }
    Iterator i = testSet.iterator();
    UnsafeIteratorRuntimeMonitor.createEvent(testSet, i);
  
    int output = 0;	
    for(int j = 0; j < 10 && i.hasNext(); ++j){
      UnsafeIteratorRuntimeMonitor.nextEvent(i);
      output += (Integer)i.next();

      testSet.add(new Integer(j));
      UnsafeIteratorRuntimeMonitor.updatesourceEvent(testSet);
    }
	System.out.println(output);
  }
}
