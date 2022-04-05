package HasNext2_2;

import java.util.*;

public class HasNext2_2 {
	public static void main(String[] args) {
		Vector<Integer> v = new Vector<Integer>();
		v.add(1);
		v.add(2);

		Iterator i = v.iterator();
		int sum = 0;

		// should not match "next next"
		rvm.HasNext2RuntimeMonitor.hasnextEvent(i);
		if (i.hasNext()) {
			rvm.HasNext2RuntimeMonitor.nextEvent(i);
			sum += (Integer)i.next();
		}

		System.out.println("sum: " + sum);
	}
}

