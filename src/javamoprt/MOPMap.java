package javamoprt;

public abstract class MOPMap<V> extends MOPCleanable implements MOPObject{

	public MOPWeakReference cachedKey;

	/*
	 * To avoid a race condition, it keeps two numbers separately.
	 * Thus, the result might be incorrect sometimes.
	 * This method is only for statistics.
	 */
	abstract public long size();

	abstract public Object get(Object key);
	
	abstract public Object get(Object key, int pos);
	abstract public Object[] getAll(Object key);
	
	abstract public boolean put(MOPWeakReference keyref, V value);

	abstract public boolean put(MOPWeakReference keyref, V value, int pos);
	
	abstract public void endObject(MOPMultiMapSignature[] signatures);


}
