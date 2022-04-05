package SafeIterator_1;

import java.util.*;

public class SafeIterator_1 {
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
      testSet.add(new Integer(j));
		rvm.SafeIteratorRuntimeMonitor.updatesourceEvent(testSet);
    }
	System.out.println(output);
  }
}
