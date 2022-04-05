
package UnsafeIterator_2;
import java.util.*;
import rvm.UnsafeIteratorRuntimeMonitor;

public class UnsafeIterator_2 {
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
    }
    System.out.println(output);
  }
}
