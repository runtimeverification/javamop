
package HasNext_3;
import java.util.*;

public class HasNext_3 {
       public static void main(String[] args) {
               Vector<Integer> v = new Vector<Integer>();
               v.add(1); v.add(2);
               Iterator it = v.iterator();
		   boolean  b;
               while(b = it.hasNext()) {
				   rvm.HasNextRuntimeMonitor.hasnexttrueEvent(it, b);
				   rvm.HasNextRuntimeMonitor.hasnextfalseEvent(it, b);
				   rvm.HasNextRuntimeMonitor.nextEvent(it);
				   final Integer next1 = (Integer) it.next();
				   rvm.HasNextRuntimeMonitor.nextEvent(it);
				   int sum = next1 + (Integer)it.next();
                       System.out.println("sum = " + sum);
               }
       }
}

