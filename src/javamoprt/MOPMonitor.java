package javamoprt;

public abstract class MOPMonitor implements javamoprt.MOPObject {
	public abstract void endObject(int idnum);
	public boolean MOP_terminated = false;
	public int MOP_lastevent = -1;
}
