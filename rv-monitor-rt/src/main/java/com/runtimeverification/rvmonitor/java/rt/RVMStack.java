package com.runtimeverification.rvmonitor.java.rt;

public class RVMStack<Elt> implements RVMObject {
	static int CAPACITY = 100;
	int curr_index = 0;
	Elt[] elements;
	int capacity;

	public RVMStack() {
		elements = (Elt[]) new Object[CAPACITY];
		capacity = CAPACITY;
	}

	public RVMStack(int initial_capacity) {
		elements = (Elt[]) new Object[initial_capacity];
		capacity = initial_capacity;
	}

	public Elt peek() {
		return elements[curr_index - 1];
	}

	public void pop(int num) {
		curr_index -= num;
	}

	public void push(Elt elt) {
		if (curr_index < elements.length) {
			elements[curr_index++] = elt;
		} else {
			int len = elements.length;
			Elt[] old = elements;
			elements = (Elt[]) new Object[len << 1];
			for (int i = 0; i < len; i++) {
				elements[i] = old[i];
			}
			elements[curr_index++] = elt;
		}
	}

	public void clear() {
		for (int i = 0; i < curr_index; i++)
			elements[i] = null;
		curr_index = 0;
	}

	public RVMStack<Elt> fclone() {
		RVMStack<Elt> ret = new RVMStack<Elt>(capacity);
		ret.curr_index = curr_index;
		for (int i = 0; i < curr_index; i++)
			ret.elements[i] = elements[i];
		return ret;
	}
}