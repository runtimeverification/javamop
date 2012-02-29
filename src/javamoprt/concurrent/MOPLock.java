package javamoprt.concurrent;

import java.util.concurrent.atomic.AtomicReference;

/*
 * There is a possible deadlock in this lock. Do not use.
 */
public class MOPLock {
	static ThreadLocal<MOPNameStone> myStone = new ThreadLocal<MOPNameStone>(){
		protected MOPNameStone initialValue(){
			return new MOPNameStone();
		}
	};
	
	MOPNameStone NULL = new MOPNameStone();
	AtomicReference<MOPNameStone> lock = new AtomicReference<MOPNameStone>(NULL);
	
	public MOPNameStone lock(){
		MOPNameStone stone = myStone.get();
		stone.tag = true;
		
		MOPNameStone origStone = lock.get();
		if(origStone != stone){
			origStone = lock.getAndSet(stone);
			
			while(origStone.tag)
				Thread.yield();
		}
		
		return stone;
	}
	
	public void unlock(){
		MOPNameStone stone = myStone.get();
		stone.tag = false;
	}
}

