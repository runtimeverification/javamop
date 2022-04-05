
package HasNext2_1;
import java.util.*;

public class HasNext2_1 {
	public static void main(String[] args) {
		Vector<Integer> v = new Vector<Integer>();
		v.add(1);
		v.add(2);

		Iterator i = v.iterator();
		int sum = 0;

		if (i.hasNext()) {
			rvm.HasNext2RuntimeMonitor.hasnextEvent(i);
			rvm.HasNext2RuntimeMonitor.nextEvent(i);
			sum += (Integer)i.next();
			// should match "next next" on the following event
			rvm.HasNext2RuntimeMonitor.nextEvent(i);
			sum += (Integer)i.next();
		} else rvm.HasNext2RuntimeMonitor.hasnextEvent(i);

		System.out.println("sum: " + sum);
	}
}

