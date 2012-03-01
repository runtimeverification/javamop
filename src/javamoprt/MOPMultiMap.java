package javamoprt;

public interface MOPMultiMap {

	/*
	 * To avoid a race condition, it keeps two numbers separately.
	 * Thus, the result might be incorrect sometimes.
	 * This method is only for statistics.
	 */
	public long size();

	public Object get(Object key);
	
	public Object get(Object key, int pos);
	public Object[] getAll(Object key);
	
	public boolean put(MOPWeakReference keyref, Object value);

	public boolean put(MOPWeakReference keyref, Object value, int pos);
	
	public void endObject(MOPMultiMapSignature[] signatures);


}
