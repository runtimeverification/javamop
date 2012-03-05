package javamoprt;

import java.lang.ref.*;
import java.util.*;
//import org.apache.commons.collections.map.*;
import java.lang.reflect.*;

class StopWatch {
	protected long runTime;
	protected long startTime, stopTime;

	StopWatch() {
		runTime = 0;
		startTime = 0;
		stopTime = 0;
	}

	public void start() {
		startTime = System.currentTimeMillis();
	}

	public void stop() {
		stopTime = System.currentTimeMillis();
		runTime += (stopTime - startTime);
	}

	public long getRunTime() {
		return runTime;
	}

}

public class Test {
	static final int NUM_DUMMY = 1000000;

	static public void memoryClean() {
		Integer[] integers = new Integer[NUM_DUMMY];

		for (int i = 0; i < NUM_DUMMY; i++) {
			integers[i] = new Integer(i);
		}

		System.gc();
	}

	private static final int SZ_REF = 4;

	private static int size_prim(Class t) {
		if (t == Boolean.TYPE)
			return 1;
		else if (t == Byte.TYPE)
			return 1;
		else if (t == Character.TYPE)
			return 2;
		else if (t == Short.TYPE)
			return 2;
		else if (t == Integer.TYPE)
			return 4;
		else if (t == Long.TYPE)
			return 8;
		else if (t == Float.TYPE)
			return 4;
		else if (t == Double.TYPE)
			return 8;
		else if (t == Void.TYPE)
			return 0;
		else
			return SZ_REF;
	}

	private static int size_inst(Class c) {
		Field flds[] = c.getDeclaredFields();
		int sz = 0;

		for (int i = 0; i < flds.length; i++) {
			Field f = flds[i];
			if (!c.isInterface() && (f.getModifiers() & Modifier.STATIC) != 0)
				continue;
			sz += size_prim(f.getType());
		}

		if (c.getSuperclass() != null)
			sz += size_inst(c.getSuperclass());

		Class cv[] = c.getInterfaces();
		for (int i = 0; i < cv.length; i++)
			sz += size_inst(cv[i]);

		return sz;
	}

	public static int sizeof(Object obj) {
		if (obj == null)
			return 0;

		Class c = obj.getClass();

		return size_inst(c);
	}

/*	static public void testMOPMap() {
		StopWatch timer = new StopWatch();
		StopWatch timer2 = new StopWatch();

		MOPAbstractMap map = new MOPAbstractMap();

		Integer[] keys1 = new Integer[100];
		Integer[][] keys2 = new Integer[100][100];
		Integer[][][] keys3 = new Integer[100][100][100];
		Integer[] integers = new Integer[1000000];

		for (int i = 0; i < 100; i++) {
			keys1[i] = new Integer(i);
			for (int j = 0; j < 100; j++) {
				keys2[i][j] = new Integer(100 + i * 100 + j);
				for (int k = 0; k < 100; k++) {
					keys3[i][j][k] = new Integer(10100 + i * 10000 + j * 100 + k);
				}
			}
		}

		System.out.println("== total execution time of MOPMap puts ==");

		timer.start();
		int index = 0;
		for (int j = 0; j < 100; j++) {
			for (int k = 0; k < 100; k++) {
				for (int i = 0; i < 100; i++) {
					Integer newint = new Integer(1010100 + i * 10000 + j * 100 + k);
					integers[index++] = newint;

					MOPAbstractMap map2 = (MOPAbstractMap) map.get(keys1[i]);
					if (map2 == null) {
						map2 = new MOPAbstractMap();
						map.put(new MOPWeakReference(keys1[i]), map2);
					}

					MOPAbstractMap map3 = (MOPAbstractMap) map2.get(keys2[i][j]);
					if (map3 == null) {
						map3 = new MOPAbstractMap();
						map2.put(new MOPWeakReference(keys2[i][j]), map3);
					}

				}
			}
		}
		timer.stop();

		System.out.println(timer.getRunTime() + "ms");

		System.out.println("== total execution time of MOPMap gets ==");

		timer2.start();
		int max = 0;
		for (int j = 0; j < 100; j++) {
			for (int k = 0; k < 100; k++) {
				for (int i = 0; i < 100; i++) {
					MOPAbstractMap map2 = (MOPAbstractMap) map.get(keys1[i]);
					MOPAbstractMap map3 = (MOPAbstractMap) map2.get(keys2[i][j]);
					Integer num = (Integer) map3.get(keys3[i][j][k]);

					map2 = (MOPAbstractMap) map.get(keys1[i]);
					map3 = (MOPAbstractMap) map2.get(keys2[i][j]);
					num = (Integer) map3.get(keys3[i][j][k]);

					if (num != null) {
						if (num > max)
							max = num.intValue();
					}
				}
			}
		}
		timer2.stop();

		System.out.println(timer2.getRunTime() + "ms");

		int max2 = 0;
		for (int i = 0; i < index; i++) {
			if (integers[i] > max2)
				max2 = integers[i];
		}

	}*/

	/*
	static public void testApacheMap3() {
		StopWatch timer = new StopWatch();
		StopWatch timer2 = new StopWatch();

		ReferenceIdentityMap map = new ReferenceIdentityMap(AbstractReferenceMap.WEAK, AbstractReferenceMap.HARD, true);

		Integer[] keys1 = new Integer[100];
		Integer[][] keys2 = new Integer[100][100];
		Integer[][][] keys3 = new Integer[100][100][100];
		Integer[] integers = new Integer[1000000];

		for (int i = 0; i < 100; i++) {
			keys1[i] = new Integer(i);
			for (int j = 0; j < 100; j++) {
				keys2[i][j] = new Integer(100 + i * 100 + j);
				for (int k = 0; k < 100; k++) {
					keys3[i][j][k] = new Integer(10100 + i * 10000 + j * 100 + k);
				}
			}
		}

		System.out.println("== total execution time of ReferenceIdentityMap puts ==");

		timer.start();
		int index = 0;
		for (int k = 0; k < 100; k++) {
			for (int j = 0; j < 100; j++) {
				for (int i = 0; i < 100; i++) {
					Integer newint = new Integer(1010100 + i * 10000 + j * 100 + k);
					integers[index++] = newint;

					ReferenceIdentityMap map2 = (ReferenceIdentityMap) map.get(keys1[i]);
					if (map2 == null) {
						map2 = new ReferenceIdentityMap(AbstractReferenceMap.WEAK, AbstractReferenceMap.HARD, true);
						map.put(keys1[i], map2);
					}

					ReferenceIdentityMap map3 = (ReferenceIdentityMap) map2.get(keys2[i][j]);
					if (map3 == null) {
						map3 = new ReferenceIdentityMap(AbstractReferenceMap.WEAK, AbstractReferenceMap.HARD, true);
						map2.put(keys2[i][j], map3);
					}

					map3.put(keys3[i][j][k], newint);
				}
			}
		}
		timer.stop();

		System.out.println(timer.getRunTime() + "ms");

		System.out.println("== total execution time of ReferenceIdentityMap gets ==");

		timer2.start();
		int max = 0;
		for (int k = 0; k < 100; k++) {
			for (int j = 0; j < 100; j++) {
				for (int i = 0; i < 100; i++) {
					ReferenceIdentityMap map2 = (ReferenceIdentityMap) map.get(keys1[i]);
					ReferenceIdentityMap map3 = (ReferenceIdentityMap) map2.get(keys2[i][j]);
					Integer num = (Integer) map3.get(keys3[i][j][k]);

					map2 = (ReferenceIdentityMap) map.get(keys1[i]);
					map3 = (ReferenceIdentityMap) map2.get(keys2[i][j]);
					num = (Integer) map3.get(keys3[i][j][k]);

					if (num != null) {
						if (num > max)
							max = num.intValue();
					}
				}
			}
		}
		timer2.stop();

		System.out.println(timer2.getRunTime() + "ms");

		int max2 = 0;
		for (int i = 0; i < index; i++) {
			if (integers[i] > max2)
				max2 = integers[i];
		}

	}
	 */


	/*
	static public void testApacheMap2() {
		StopWatch timer = new StopWatch();
		StopWatch timer2 = new StopWatch();

		ReferenceIdentityMap map = new ReferenceIdentityMap(AbstractReferenceMap.WEAK, AbstractReferenceMap.HARD, true);

		Integer[] keys1 = new Integer[1000000];
		Integer[][] keys2 = new Integer[1000000][1];
		Integer[] integers = new Integer[1000000];

		for (int i = 0; i < 1000000; i++) {
			keys1[i] = new Integer(i);
			for (int j = 0; j < 1; j++) {
				keys2[i][j] = new Integer(1000000 + i * 1 + j);
			}
		}

		System.out.println("== total execution time of ReferenceIdentityMap puts ==");

		timer.start();
		int index = 0;
		for (int j = 0; j < 1; j++) {
			for (int i = 0; i < 1000000; i++) {
				ReferenceIdentityMap map2 = (ReferenceIdentityMap) map.get(keys1[i]);

				if (map2 == null) {
					map2 = new ReferenceIdentityMap(AbstractReferenceMap.WEAK, AbstractReferenceMap.WEAK, true);
					map.put(keys1[i], map2);
				}

				Integer newint = new Integer(1000000 + i * 1 + j);
				integers[index++] = newint;

				map2.put(keys2[i][j], newint);
			}
		}
		timer.stop();

		System.out.println(timer.getRunTime() + "ms");

		System.out.println("== total execution time of ReferenceIdentityMap gets ==");

		timer2.start();
		int max = 0;
		for (int j = 0; j < 1; j++) {
			for (int i = 0; i < 1000000; i++) {
				ReferenceIdentityMap map2 = (ReferenceIdentityMap) map.get(keys1[i]);

				Integer num = (Integer) map2.get(keys2[i][j]);

				if (num != null) {
					if (num > max)
						max = num.intValue();
				}
			}
		}
		timer2.stop();

		System.out.println(timer2.getRunTime() + "ms");

		int max2 = 0;
		for (int i = 0; i < index; i++) {
			if (integers[i] > max2)
				max2 = integers[i];
		}

	}
	*/

	/*
	static public void testApacheMap() {
		StopWatch timer = new StopWatch();
		StopWatch timer2 = new StopWatch();

		ReferenceIdentityMap map = new ReferenceIdentityMap(AbstractReferenceMap.WEAK, AbstractReferenceMap.HARD, true);

		Integer[] keys1 = new Integer[10000];
		Integer[][] keys2 = new Integer[10000][100];
		Integer[] integers = new Integer[1000000];

		for (int i = 0; i < 10000; i++) {
			keys1[i] = new Integer(i);
			for (int j = 0; j < 100; j++) {
				keys2[i][j] = new Integer(10000 + i * 100 + j);
			}
		}

		System.out.println("== total execution time of ReferenceIdentityMap puts ==");

		timer.start();
		int index = 0;
		for (int j = 0; j < 100; j++) {
			for (int i = 0; i < 10000; i++) {
				ReferenceIdentityMap map2 = (ReferenceIdentityMap) map.get(keys1[i]);

				if (map2 == null) {
					map2 = new ReferenceIdentityMap(AbstractReferenceMap.WEAK, AbstractReferenceMap.WEAK, true);
					map.put(keys1[i], map2);
				}

				Integer newint = new Integer(1000000 + i * 100 + j);
				integers[index++] = newint;

				map2.put(keys2[i][j], newint);
			}
		}
		timer.stop();

		System.out.println(timer.getRunTime() + "ms");

		System.out.println("== total execution time of ReferenceIdentityMap gets ==");

		timer2.start();
		int max = 0;
		for (int j = 0; j < 100; j++) {
			for (int i = 0; i < 10000; i++) {
				ReferenceIdentityMap map2 = (ReferenceIdentityMap) map.get(keys1[i]);

				Integer num = (Integer) map2.get(keys2[i][j]);

				if (num != null) {
					if (num > max)
						max = num.intValue();
				}
			}
		}
		timer2.stop();

		System.out.println(timer2.getRunTime() + "ms");

		int max2 = 0;
		for (int i = 0; i < index; i++) {
			if (integers[i] > max2)
				max2 = integers[i];
		}

	}
	*/


	/*
	static public void testApacheSet() {
		// setting up
		ReferenceIdentityMap set = new ReferenceIdentityMap(AbstractReferenceMap.WEAK, AbstractReferenceMap.WEAK, 4096, 0.75f, true);

		Integer[] integers = new Integer[1000000];

		for (int i = 0; i < 1000000; i++) {
			integers[i] = new Integer(i);
			set.put(integers[i], integers[i]);
		}

		StopWatch timer = new StopWatch();

		timer.start();
		// testing
		int sum = 0;
		for (Integer e : (Set<Integer>) set.keySet()) {
			if (e != null)
				sum++;
		}
		timer.stop();

		System.out.println("== total execution time of ReferenceIdentityMap iteration ==");
		System.out.println(timer.getRunTime() + "ms");
	}
	*/

	static public void testHashSet() {
		// setting up
		HashSet set = new HashSet();

		Integer[] integers = new Integer[1000000];

		for (int i = 0; i < 1000000; i++) {
			integers[i] = new Integer(i);
			set.add(integers[i]);
		}

		StopWatch timer = new StopWatch();

		timer.start();
		// testing
		long sum = 0;
		for (Integer e : (HashSet<Integer>) set) {
			if (e != null && e >= 0)
				sum++;
		}
		timer.stop();

		System.out.println("== total execution time of HashSet iteration==");
		System.out.println(timer.getRunTime() + "ms");
	}

	static public void main(String[] args) {
		// testHashSet();
		// testMOPRefSet();
		// testApacheSet();

		memoryClean();
		// testMOPRefMap();
		// testApacheMap();
		// testApacheMap2();
		// testMOPRefMap2();
		// testMOPRefMap3();
		// testApacheMap3();
		// testMOPMap();

		// testApacheMap2();
		// memoryClean();
		// testApacheMap2();
		// memoryClean();

//		System.out.println("== size of MOPWeakReference ==");
//		System.out.println(sizeof(new MOPWeakReference(0, new Integer(1), new ReferenceQueue())) + "bytes");
//
//		System.out.println("== size of MOPRefSet ==");
//		System.out.println(sizeof(new MOPRefSet()) + "bytes");
//
//		System.out.println("== size of MOPMap ==");
//		System.out.println(sizeof(new MOPMap()) + "bytes");
//
//		System.out.println("== size of MOPMapOfSet ==");
//		System.out.println(sizeof(new MOPMapOfSet(1)) + "bytes");
//
//		System.out.println("== size of ReferenceIdentityMap ==");
//		System.out.println(sizeof(new ReferenceIdentityMap(AbstractReferenceMap.WEAK, AbstractReferenceMap.WEAK, 4096, 0.75f, true)) + "bytes");

		System.exit(0);
	}
}
