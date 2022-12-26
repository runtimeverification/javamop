package SafeSyncCollection_1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class SafeSyncCollection_1 {
	public static void main(String[] args){
		ArrayList<String> list = new ArrayList<String>();
		Collection c = list;
		c = Collections.synchronizedCollection(c);
		rvm.SafeSyncCollectionRuntimeMonitor.syncEvent(c);

		list.add("Foo");
		list.add("Bar");
		Iterator i = c.iterator();
		rvm.SafeSyncCollectionRuntimeMonitor.asyncCreateIterEvent(c,i);
		rvm.SafeSyncCollectionRuntimeMonitor.syncCreateIterEvent(c,i);
		rvm.SafeSyncCollectionRuntimeMonitor.accessIterEvent(i);
		while(i.hasNext()){
			rvm.SafeSyncCollectionRuntimeMonitor.accessIterEvent(i);
			System.out.println(i.next());
			rvm.SafeSyncCollectionRuntimeMonitor.accessIterEvent(i);
		}
	}
}
