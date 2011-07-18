package javamoprt.test;

import javamoprt.MOPTimer;
import javamoprt.concurrent.MOPLock;
import javamoprt.concurrent.MOPNameStone;

public class MOPLockTest {

	static Thread daemon = new Thread() {
		public void run() {
			while (true) {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {

				}
			}
		}
	};

	public static void main(String args[]) {
		MOPTimer timer_1 = new MOPTimer();
		MOPTimer timer_2 = new MOPTimer();

		daemon.setDaemon(true);
		daemon.start();

		MOPLock moplock = new MOPLock();

		timer_1.start();
		for (int i = 0; i < 100; i++) {
			TestThread1 thread = new TestThread1(moplock);
			thread.start();
			try {
				thread.join();
			} catch (Exception e) {

			}
		}
		timer_1.end();

		timer_2.start();
		for (int i = 0; i < 100; i++) {
			TestThread2 thread = new TestThread2();
			thread.start();
			try {
				thread.join();
			} catch (Exception e) {

			}
		}
		timer_2.end();

		System.out.println("mop: " + timer_1.getElapsedMicroTime());
		System.out.println("java: " + timer_2.getElapsedMicroTime());
	}
}

class TestThread1 extends Thread {
	MOPLock moplock;

	TestThread1(MOPLock moplock) {
		this.moplock = moplock;
	}

	static void doSomething() {
		int s = 0;
		s = 1;

		int k = s;
		k = k + s;
	}

	public void run() {
		for (int i = 0; i < 100000; i++) {
			MOPNameStone stone = moplock.lock();

			doSomething();

			stone.tag = false;
		}
	}
}

class TestThread2 extends Thread {
	Object lock = new Object();

	TestThread2() {
	}

	static void doSomething() {
		int s = 0;
		s = 1;

		int k = s;
		k = k + s;
	}

	public void run() {
		for (int i = 0; i < 100000; i++) {
			synchronized (lock) {
				doSomething();
			}
		}
	}
}
