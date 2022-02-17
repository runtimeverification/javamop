package com.runtimeverification.rvmonitor.java.rt.observable;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.runtimeverification.rvmonitor.java.rt.ref.CachedWeakReference;
import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractIndexingTree;
import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractMonitor;
import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractMonitorSet;
import com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractPartitionedMonitorSet;
import com.runtimeverification.rvmonitor.java.rt.tablebase.DisableHolder;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IDisableHolder;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IIndexingTreeValue;
import com.runtimeverification.rvmonitor.java.rt.tablebase.IMonitor;
import com.runtimeverification.rvmonitor.java.rt.tablebase.TableAdopter.Tuple2;
import com.runtimeverification.rvmonitor.java.rt.tablebase.TableAdopter.Tuple3;

public class InternalBehaviorDumper implements IInternalBehaviorObserver {
	private final PrintWriter writer;
	private final Map<Class<?>, List<Object>> keyobjectmap;
	
	public InternalBehaviorDumper(PrintWriter writer) {
		this.writer = writer;
		this.keyobjectmap = new HashMap<Class<?>, List<Object>>();
	}
	
	private void printSpace() {
		this.writer.print(' ');
	}
	
	private void printIndent(int num) {
		for (int i = 0; i < num; ++i)
			this.writer.print("   ");
	}
	
	private void printTitle(String title) {
		this.printIndent(1);
		this.writer.print('[');
		this.writer.print(title);
		this.writer.print("] ");
	}
	
	private void printOneKey(Object key) {
		if (key instanceof String) {
			this.writer.print(key);
			return;
		}

		Class<?> cls = key.getClass();
		List<Object> objects = this.keyobjectmap.get(cls);
		if (objects == null) {
			objects = new ArrayList<Object>();
			this.keyobjectmap.put(cls, objects);
		}
		
		int id = objects.indexOf(key);
		if (id == -1) {
			id = objects.size();
			objects.add(key);
		}

		int prettyid = id + 1;
		String s = cls.getSimpleName() + "#" + prettyid;
		this.writer.print(s);
	}
	
	private void printTreeKeys(Object[] keys) {
		this.writer.print('(');
		for (int i = 0; i < keys.length; ++i) {
			if (i > 0)
				this.writer.print(',');
			Object key = keys[i];
			if (key instanceof CachedWeakReference) {
				CachedWeakReference wr = (CachedWeakReference)key;
				this.printOneKey(wr.get());
			}
			else
				this.printOneKey(key);
		}
		this.writer.print(')');
	}
	
	private <TWeakRef extends CachedWeakReference, TValue extends IIndexingTreeValue> void printIndexingTree(AbstractIndexingTree<TWeakRef, TValue> tree) {
		String desc = tree.getObservableObjectDescription();
		if (desc == null)
			this.writer.print(tree.getClass().getSimpleName());
		else
			this.writer.print(desc);
	}
	
	private void printPotentialMonitor(Object o) {
		StringBuilder s = new StringBuilder();
		if (o == null)
			s.append('_');
		else {
			if (o instanceof DisableHolder || o instanceof AbstractMonitor)
				s.append(o.getClass().getSimpleName());
			if (o instanceof IObservableObject) {
				IObservableObject obs = (IObservableObject)o;
				s.append(obs.getObservableObjectDescription());
			}
		}
		this.writer.print(s);
	}
	
	private void printMonitorSetElement(Object o) {
		if (o instanceof IObservableObject) {
			IObservableObject obs = (IObservableObject)o;
			// 'desc' will look like the following:
			// <type>#<id>{<time>}[<trace>]
			// The {<time>} part can be omitted.
			String desc = obs.getObservableObjectDescription();
			int j = desc.indexOf('#');
			int k = desc.indexOf('{', j);
			if (k == -1)
				k = desc.indexOf('[', j);
			String brief = desc.substring(j, k);
			this.writer.print(brief);
		}
		else
			this.writer.print('?');
	}
	
	private void printMonitorSet(AbstractMonitorSet<?> set) {
		this.writer.print(set.getClass().getSimpleName());
		this.writer.print('{');
		for (int i = 0; i < set.getSize(); ++i) {
			if (i > 0)
				this.writer.print(',');
			Object o = set.get(i);
			this.printMonitorSetElement(o);
		}
		this.writer.print('}');
	}

	private void printMonitorSet(AbstractPartitionedMonitorSet<?> set) {
		this.writer.print(set.getClass().getSimpleName());
		this.writer.print('{');
		int n = 0;
		for (AbstractPartitionedMonitorSet<?>.MonitorIterator i = set.monitorIterator(true); i.moveNext(); ++n) {
			IMonitor monitor = i.getMonitor();
			if (n > 0)
				this.writer.print(',');
			this.printMonitorSetElement(monitor);
		}
		this.writer.print('}');
	}
	
	private void printOneValue(Object value) {
		if (value instanceof AbstractIndexingTree<?, ?>)
			this.printIndexingTree((AbstractIndexingTree<?, ?>)value);
		else if (value instanceof AbstractMonitorSet<?>)
			this.printMonitorSet((AbstractMonitorSet<?>)value);
		else if (value instanceof AbstractPartitionedMonitorSet<?>)
			this.printMonitorSet((AbstractPartitionedMonitorSet<?>)value);
		else
			this.printPotentialMonitor(value);
	}

	private void printTreeValue(Object value) {
		if (value instanceof Tuple2) {
			Tuple2<?, ?> tuple = (Tuple2<?, ?>)value;
			this.writer.print('<');
			this.printOneValue(tuple.getValue1());
			this.writer.print(", ");
			this.printOneValue(tuple.getValue2());
			this.writer.print('>');
		}
		else if (value instanceof Tuple3) {
			Tuple3<?, ?, ?> tuple = (Tuple3<?, ?, ?>)value;
			this.writer.print('<');
			this.printOneValue(tuple.getValue1());
			this.writer.print(", ");
			this.printOneValue(tuple.getValue2());
			this.writer.print(", ");
			this.printOneValue(tuple.getValue3());
			this.writer.print('>');
		}
		else
			this.printPotentialMonitor(value);
	}

	@Override
	public void onEventMethodEnter(String evtname, Object... args) {
		this.writer.print(evtname);
		this.writer.print('<');
		this.printTreeKeys(args);
		this.writer.print('>');
		this.writer.print(" {{{");
		this.writer.println();
	}

	@Override
	public void onIndexingTreeCacheHit(String cachename, Object cachevalue) {
		this.printTitle("CacheHit");
		this.writer.print(cachename);
		this.printSpace();
		this.printTreeValue(cachevalue);
		this.writer.println();
	}

	@Override
	public void onIndexingTreeCacheMissed(String cachename) {
		this.printTitle("CacheMiss");
		this.writer.print(cachename);
		this.writer.println();
	}

	@Override
	public void onIndexingTreeCacheUpdated(String cachename, Object cachevalue) {
		this.printTitle("CacheUpdate");
		this.writer.print(cachename);
		this.printSpace();
		this.printTreeValue(cachevalue);
		this.writer.println();
	}

	@Override
	public <TWeakRef extends CachedWeakReference, TValue extends IIndexingTreeValue> void onIndexingTreeLookup(AbstractIndexingTree<TWeakRef, TValue> tree, LookupPurpose purpose, Object retrieved, Object ... keys) {
		this.printTitle("TreeLookup");
		this.printIndexingTree(tree);
		this.printSpace();
		this.writer.print(purpose.toString());
		this.printSpace();
		this.printTreeKeys(keys);
		this.printSpace();
		this.printTreeValue(retrieved);
		this.writer.println();
	}

	@Override
	public <TWeakRef extends CachedWeakReference, TValue extends IIndexingTreeValue> void onTimeCheck(AbstractIndexingTree<TWeakRef, TValue> tree, IDisableHolder source, IDisableHolder candidate, boolean definable, Object ... keys) {
		this.printTitle("TimeCheck");
		this.printIndexingTree(tree);
		this.printSpace();
		this.printTreeKeys(keys);
		this.printSpace();
		this.printPotentialMonitor(source);
		this.printSpace();
		this.printPotentialMonitor(candidate);
		this.printSpace();
		this.writer.print(definable);
		this.writer.println();
	}

	@Override
	public <TWeakRef extends CachedWeakReference, TValue extends IIndexingTreeValue> void onIndexingTreeNodeInserted(AbstractIndexingTree<TWeakRef, TValue> tree, Object inserted, Object ... keys) {
		this.printTitle("TreeInsert");
		this.printIndexingTree(tree);
		this.printSpace();
		this.printTreeKeys(keys);
		this.printSpace();
		this.printPotentialMonitor(inserted);
		this.writer.println();
	}

	@Override
	public void onNewMonitorCreated(AbstractMonitor created) {
		this.printTitle("MonitorCreate");
		this.printPotentialMonitor(created);
		this.writer.println();
	}

	@Override
	public void onMonitorCloned(AbstractMonitor existing, AbstractMonitor created) {
		this.printTitle("MonitorClone");
		this.printPotentialMonitor(existing);
		this.writer.print(" -> ");
		this.printPotentialMonitor(created);
		this.writer.println();
		
	}

	@Override
	public void onDisableFieldUpdated(IDisableHolder affected) {
		this.printTitle("DisableUpdate");
		this.printPotentialMonitor(affected);
		this.writer.println();
	}

	@Override
	public void onMonitorTransitioned(AbstractMonitor monitor) {
		this.printTitle("MonitorTransition");
		this.printPotentialMonitor(monitor);
		this.writer.println();
	}

	@Override
	public <TMonitor extends IMonitor> void onMonitorTransitioned(AbstractMonitorSet<TMonitor> set) {
		this.printTitle("MonitorSetTransition");
		this.printMonitorSet(set);
		this.writer.println();
		for (int i = 0; i < set.getSize(); ++i) {
			TMonitor monitor = set.get(i);
			this.printIndent(2);
			this.printPotentialMonitor(monitor);
			this.writer.println();
		}
	}

	@Override
	public <TMonitor extends IMonitor> void onMonitorTransitioned(AbstractPartitionedMonitorSet<TMonitor> set) {
		this.printTitle("MonitorSetTransition");
		this.printMonitorSet(set);
		this.writer.println();
		
		for (AbstractPartitionedMonitorSet<TMonitor>.MonitorIterator i = set.monitorIterator(true); i.moveNext(); ) {
			TMonitor monitor = i.getMonitor();
			this.printIndent(2);
			this.printPotentialMonitor(monitor);
			this.writer.println();
		}
	}

	@Override
	public void onEventMethodLeave() {
		this.writer.print("}}}");
		this.writer.println();
		this.writer.flush();
	}

	@Override
	public void onCompleted() {
		this.writer.print("=== END OF TRACE ===");
		this.writer.println();
		this.writer.flush();
		this.writer.close();
	}
}
