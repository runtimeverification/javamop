package javamoprt;

public interface MOPSet extends MOPObject{
	
	public int size();
	
	public boolean add(MOPMonitor e);
	
	public void endObject(int idnum);
	
	public boolean alive();
	
	public void endObjectAndClean(int idnum);
	
	public void ensureCapacity();
	
}