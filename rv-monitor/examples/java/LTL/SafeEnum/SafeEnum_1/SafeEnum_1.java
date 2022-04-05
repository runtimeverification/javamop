package SafeEnum_1;

import java.util.*;

public class SafeEnum_1 {
	public static void main(String[] args){
		Vector<Integer> v = new Vector<Integer>();

		v.add(1);
		rvm.SafeEnumRuntimeMonitor.updatesourceEvent(v);
		v.add(2);
		rvm.SafeEnumRuntimeMonitor.updatesourceEvent(v);
		v.add(4);
		rvm.SafeEnumRuntimeMonitor.updatesourceEvent(v);
		v.add(8);
		rvm.SafeEnumRuntimeMonitor.updatesourceEvent(v);

		Enumeration e = v.elements();
		rvm.SafeEnumRuntimeMonitor.createEvent(v,e);

		int sum = 0;

		if(e.hasMoreElements()){
			rvm.SafeEnumRuntimeMonitor.nextEvent(e);
			sum += (Integer)e.nextElement();
			v.add(11);
			rvm.SafeEnumRuntimeMonitor.updatesourceEvent(v);
		}

		while(e.hasMoreElements()){
			sum += (Integer)e.nextElement();
			rvm.SafeEnumRuntimeMonitor.nextEvent(e);
		}

		v.clear();
		rvm.SafeEnumRuntimeMonitor.updatesourceEvent(v);

		System.out.println("sum: " + sum);
	}
}



