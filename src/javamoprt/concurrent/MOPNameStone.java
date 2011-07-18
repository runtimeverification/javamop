package javamoprt.concurrent;

public class MOPNameStone {
	Thread t;
	public boolean tag;
	MOPNameStone(){
		t = Thread.currentThread();
		tag = false;
	}
}