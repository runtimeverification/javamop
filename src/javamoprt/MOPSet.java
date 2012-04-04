package javamoprt;

public abstract class MOPSet implements MOPObject{

	public int size = 0;
	
	abstract public int size();

	abstract public boolean add(MOPMonitor e);
	
	abstract public void endObject(int idnum);
	
	abstract public boolean alive();
	
	abstract public void endObjectAndClean(int idnum);
	
	abstract public void ensureCapacity();
	
}