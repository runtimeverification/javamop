package javamoprt.map;

import javamoprt.ref.MOPWeakReference;

public abstract class MOPMap extends MOPCleanable{

	abstract public long size();

//	abstract public Object get(MOPWeakReference key);
//	abstract public boolean put(MOPWeakReference keyref, Object value);

	abstract public Object getMap(MOPWeakReference key);
	abstract public boolean putMap(MOPWeakReference key, Object value);

	abstract public Object getSet(MOPWeakReference key);
	abstract public boolean putSet(MOPWeakReference key, Object value);

	abstract public Object getNode(MOPWeakReference key);
	abstract public boolean putNode(MOPWeakReference key, Object value);

}
