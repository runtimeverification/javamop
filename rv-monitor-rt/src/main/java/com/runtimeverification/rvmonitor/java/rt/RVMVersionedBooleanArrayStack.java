package com.runtimeverification.rvmonitor.java.rt;

public class RVMVersionedBooleanArrayStack implements RVMObject {
	public boolean[][] data;
	public int[] version;
	public int curr_index = 0;

	public RVMVersionedBooleanArrayStack() {
		data = new boolean[32][];
		version = new int[32];
	}

	public RVMVersionedBooleanArrayStack(int size) {
		data = new boolean[size][];
		version = new int[size];
	}

	public int peek_version() {
		if (curr_index > 0)
			return version[curr_index - 1];
		else
			return -1;
	}

	public boolean[] peek() {
		if (curr_index > 0)
			return data[curr_index - 1];
		else
			return null;
	}

	public boolean[] pop() {
		boolean[] ret = null;
		if (curr_index > 0) {
			ret = data[curr_index - 1];
			curr_index--;
			data[curr_index] = null;
			version[curr_index] = 0;
		}
		return ret;
	}

	public boolean[] popAndNext() {
		if (curr_index > 0) {
			curr_index--;
			data[curr_index] = null;
			version[curr_index] = 0;
		}
		if (curr_index > 0)
			return data[curr_index - 1];
		else
			return null;
	}

	public void pop(int num) {
		curr_index -= num;
	}

	public void push(boolean[] datum, int ver) {
		if (curr_index < data.length) {
			data[curr_index] = datum;
			version[curr_index] = ver;
			curr_index++;
		} else {
			int len = data.length;
			boolean[][] old = data;
			int[] oldversion = version;

			data = new boolean[len << 1][];
			version = new int[len << 1];
			System.arraycopy(old, 0, data, 0, len);
			System.arraycopy(oldversion, 0, version, 0, len);

			data[curr_index] = datum;
			version[curr_index] = ver;
			curr_index++;
		}
	}

	public RVMVersionedBooleanArrayStack fclone() {
		RVMVersionedBooleanArrayStack ret = new RVMVersionedBooleanArrayStack(data.length);
		ret.curr_index = curr_index;

		for (int i = 0; i < ret.curr_index; i++) {
			ret.data[i] = this.data[i].clone();
		}
		System.arraycopy(version, 0, ret.version, 0, curr_index);

		return ret;
	}

	public void clear() {
		curr_index = 0;
	}

	public boolean empty() {
		if (curr_index == 0)
			return true;
		else
			return false;
	}
}