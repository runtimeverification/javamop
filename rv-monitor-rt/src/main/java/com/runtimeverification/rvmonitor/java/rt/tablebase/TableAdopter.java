package com.runtimeverification.rvmonitor.java.rt.tablebase;

/**
 * This interface defines methods for manipulating a tuple.
 * Currently, there is only one method, set(), for updating
 * a field in the tuple.
 * 
 * For each tuple, there should be a dedicated trait, an implementation
 * of this interface.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 * @see TableAdopter.Tuple2
 * @see TableAdopter.Tuple3
 *
 * @param <TTuple> type of the tuple
 */
interface TupleTrait<TTuple> {
	public void set(TTuple targettuple, TTuple newtuple, int flag);
}

class Tuple2Trait<T extends IIndexingTreeValue, U extends IIndexingTreeValue> implements TupleTrait<TableAdopter.Tuple2<T, U>> {
	@Override
	public void set(TableAdopter.Tuple2<T, U> targettuple, TableAdopter.Tuple2<T, U> newtuple, int flag) {
		switch (flag) {
		case 1:
			targettuple.setValue1(newtuple.getValue1());
			break;
		case 2:
			targettuple.setValue2(newtuple.getValue2());
			break;
		default:
			assert false;
		}
	}
}

class Tuple3Trait<T extends IIndexingTreeValue, U extends IIndexingTreeValue, V extends IIndexingTreeValue> implements TupleTrait<TableAdopter.Tuple3<T, U, V>> {
	@Override
	public void set(TableAdopter.Tuple3<T, U, V> targettuple, TableAdopter.Tuple3<T, U, V> newtuple, int flag) {
		switch (flag) {
		case 1:
			targettuple.setValue1(newtuple.getValue1());
			break;
		case 2:
			targettuple.setValue2(newtuple.getValue2());
			break;
		case 3:
			targettuple.setValue3(newtuple.getValue3());
			break;
		default:
			assert false;
		}
	}
}

public class TableAdopter {
	/**
	 * This class is used to hold nothing. This is used in a pure
	 * weak reference table, AbstractPureWeakRefTable.
	 * @author Choonghwan Lee <clee83@illinois.edu>
	 */
	public static class Tuple0 implements IIndexingTreeValue {
		@Override
		public final void terminate(int treeid) {
		}
	}
	
	/**
	 * This class is used to hold two values. This is used in
	 * IndexingTree2.
	 * @author Choonghwan Lee <clee83@illinois.edu>
	 *
	 * @param <T> type of the first value
	 * @param <U> type of the second value
	 */
	public static class Tuple2<T extends IIndexingTreeValue, U extends IIndexingTreeValue> implements IIndexingTreeValue {
		private T value1;
		private U value2;
		
		public final T getValue1() {
			return this.value1;
		}
		
		public final U getValue2() {
			return this.value2;
		}
		
		public final void setValue1(T val1) {
			this.value1 = val1;
		}
		
		public final void setValue2(U val2) {
			this.value2 = val2;
		}
		
		public Tuple2() {
		}
		
		public Tuple2(T v1, U v2) {
			this.value1 = v1;
			this.value2 = v2;
		}
	
		@Override
		public final void terminate(int treeid) {
			if (this.value1 != null)
				this.value1.terminate(treeid);
			if (this.value2 != null)
				this.value2.terminate(treeid);
		}
	}
	
	/**
	 * This class is used to hold three values. This is used in
	 * IndexingTree3.
	 * @author Choonghwan Lee <clee83@illinois.edu>
	 *
	 * @param <T> type of the first value
	 * @param <U> type of the second value
	 * @param <V> type of the third value
	 */
	public static class Tuple3<T extends IIndexingTreeValue, U extends IIndexingTreeValue, V extends IIndexingTreeValue> implements IIndexingTreeValue {
		private T value1;
		private U value2;
		private V value3;
		
		public final T getValue1() {
			return this.value1;
		}
		
		public final U getValue2() {
			return this.value2;
		}
		
		public final V getValue3() {
			return this.value3;
		}
		
		public final void setValue1(T val1) {
			this.value1 = val1;
		}
		
		public final void setValue2(U val2) {
			this.value2 = val2;
		}
		
		public final void setValue3(V val3) {
			this.value3 = val3;
		}
		
		public Tuple3() {
		}
		
		public Tuple3(T v1, U v2, V v3) {
			this.value1 = v1;
			this.value2 = v2;
			this.value3 = v3;
		}
	
		@Override
		public final void terminate(int treeid) {
			if (this.value1 != null)
				this.value1.terminate(treeid);
			if (this.value2 != null)
				this.value2.terminate(treeid);
			if (this.value3 != null)
				this.value3.terminate(treeid);
		}
	}
}
