package javamoprt;

import java.util.HashSet;

/***
 * 
 * Thread scheduler used to implement functionalities of enforce and avoid modifiers.
 * 
 *
 */
public class MOPThreadScheduler {

	/***
	 * 
	 * The lock object used to coordinate execution
	 * 
	 */
	static final Object lock = new Object();
	
	/***
	 * 
	 * Schedule thread to execute based on current state, next transition and the whole fsm.
	 * 
	 * @param currentState current state in the fsm.
	 * @param nextTransition next transition to be executed.
	 * @param fsm the whole fsm used to schedule thread. 
	 */
	public static void enforceFSMExecution(int currentState, int nextTransition, HashSet<int []> fsm) {
		
		// TODO insert semantics to decide whether to pause current thread or not.
		boolean result = false;
		synchronized (lock) {
			while (result) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/***
	 * 
	 * Wake up other threads to check their conditions again.
	 * 
	 */
	public static void notifyExecution() {
		synchronized (lock) {
			lock.notifyAll();
		}
	}
}
