package HasNext_2;

import java.util.*;

import rvm.*;

public class HasNext_2 {
	public static void main(String[] args){
		Vector<Integer> v = new Vector<Integer>();

		v.add(1);
		v.add(2);
		v.add(4);
		v.add(8);

		Iterator i = v.iterator();
		int sum = 0;

		while(i.hasNext()){
			HasNextRuntimeMonitor.hasnextEvent(i);
			HasNextRuntimeMonitor.nextEvent(i);
			sum += (Integer)i.next();
		}

		System.out.println("sum: " + sum);
	}
}



