package SafeIterator_2;

import java.util.*;

public class SafeIterator_2 {
  public static void main(String[] args){
    Set<Integer> testSet = new HashSet<Integer>(); 
    for(int i = 0; i < 10; ++i){
      testSet.add(new Integer(i));
		rvm.SafeIteratorRuntimeMonitor.updatesourceEvent(testSet);
    }
    Iterator i = testSet.iterator();
	  rvm.SafeIteratorRuntimeMonitor.createEvent(testSet, i);
  
    int output = 0;	
    for(int j = 0; j < 10 && i.hasNext(); ++j){
		rvm.SafeIteratorRuntimeMonitor.nextEvent(i);
	  output += (Integer)i.next();
    }
	System.out.println(output);
  }
}
