package com.runtimeverification.rvmonitor.java.rt.concurrent;

import java.util.concurrent.atomic.AtomicReference;

/*
 * There is a possible deadlock in this lock. Do not use.
 */
public class RVMLock {
	static ThreadLocal<RVMNameStone> myStone = new ThreadLocal<RVMNameStone>(){
		protected RVMNameStone initialValue(){
			return new RVMNameStone();
		}
	};
	
	RVMNameStone NULL = new RVMNameStone();
	AtomicReference<RVMNameStone> lock = new AtomicReference<RVMNameStone>(NULL);
	
	public RVMNameStone lock(){
		RVMNameStone stone = myStone.get();
		stone.tag = true;
		
		RVMNameStone origStone = lock.get();
		if(origStone != stone){
			origStone = lock.getAndSet(stone);
			
			while(origStone.tag)
				Thread.yield();
		}
		
		return stone;
	}
	
	public void unlock(){
		RVMNameStone stone = myStone.get();
		stone.tag = false;
	}
}

