package SafeSyncCollection_2;

import java.util.*;

public class SafeSyncCollection_2 {
	public static void main(String[] args){
		ArrayList<String> list = new ArrayList<String>();
		list.add("Foo");
		list.add("Bar");

		Collection c = list;
		c = Collections.synchronizedCollection(list);
		rvm.SafeSyncCollectionRuntimeMonitor.syncEvent(c);

		Iterator i = null;
		synchronized(c){
			i = c.iterator();
			rvm.SafeSyncCollectionRuntimeMonitor.asyncCreateIterEvent(c,i);
			rvm.SafeSyncCollectionRuntimeMonitor.syncCreateIterEvent(c,i);
		}

		System.out.println("lists---");
		rvm.SafeSyncCollectionRuntimeMonitor.accessIterEvent(i);
		while(i.hasNext()){
			rvm.SafeSyncCollectionRuntimeMonitor.accessIterEvent(i);
			System.out.println(i.next());
			rvm.SafeSyncCollectionRuntimeMonitor.accessIterEvent(i);
		}
	}
}
