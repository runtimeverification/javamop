package com.runtimeverification.rvmonitor.util;

public class IntStack {
    private int[] data;
    private int curr_index = 0;

    public IntStack() {
        data = new int[32];
    }

    public IntStack(int size) {
        data = new int[size];
    }

    public int peek() {
        return data[curr_index - 1];
    }

    public int pop() {
        return data[--curr_index];
    }

    public void pop(int num) {
        curr_index -= num;
    }

    public void push(int datum) {
        if (curr_index < data.length)
            data[curr_index++] = datum;
        else {
            int len = data.length;
            int[] old = data;
            data = new int[len << 1];
            for (int i = 0; i < len; ++i) {
                data[i] = old[i];
            }
            data[curr_index++] = datum;
        }
    }

    public IntStack fclone() {
        IntStack ret = new IntStack(data.length);
        ret.curr_index = curr_index;
        for (int i = 0; i < curr_index; ++i) {
            ret.data[i] = data[i];
        }
        return ret;
    }

    public void clear() {
        curr_index = 0;
    }
}
