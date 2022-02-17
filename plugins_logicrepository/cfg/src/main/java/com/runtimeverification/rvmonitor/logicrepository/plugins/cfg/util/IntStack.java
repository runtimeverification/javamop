package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util;

/**
 * Convenience class for a stack of primitive ints.
 */
public class IntStack implements java.io.Serializable {
    private int[] data;
    private int curr_index = 0;
    
    /**
     * Construct an IntStack with a default size.
     */
    public IntStack() {
        data = new int[32];
    }
    
    @Override
    public String toString() {
        String ret = "[";
        for (int i = curr_index; i>=0; i--) {
            ret += Integer.toString(data[i])+",";
        }
        return ret+"]";
    }
    
    @Override
    public int hashCode() {
        return curr_index^peek();
    }
    
    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(!(o instanceof IntStack)) {
            return false;
        }
        IntStack s = (IntStack)o;
        if(curr_index != s.curr_index) {
            return false;
        }
        for(int i = 0; i < curr_index; i++) {
            if(data[i] != s.data[i]) { 
                return false;
            }
        }
        return true;
    }
    
    /**
     * Construct an IntStack with a given size.
     * @param size The size of the int stack.
     */
    public IntStack(int size) {
        data = new int[size];
    }
    
    /**
     * Retrieve the top member of the stack. The stack must contain at least one element.
     * @return The top member of the stack.
     */
    public int peek() {
        return data[curr_index - 1];
    }
    
    /**
     * Pop one member off the stack. The stack must contain at least one member.
     * @return The top element of the stack, now popped.
     */
    public int pop() {
        return data[--curr_index];
    }
    
    /**
     * Pop a number of elements off the stack.
     * @param num The number of elements to pop off. Must not be greater than the stack size.
     */
    public void pop(int num) {
        curr_index -= num;
    }
    
    /**
     * Add a member to the stack.
     * @param datum The integer to put on top of the stack.
     */
    public void push(int datum) {
        if(curr_index < data.length) {
            data[curr_index++] = datum;
        } else {
            int len = data.length;
            int[] old = data;
            data = new int[len * 2];
            for(int i = 0; i < len; ++i) {
                data[i] = old[i];
            }
            data[curr_index++] = datum; 
        }
    }
    
    /**
     * Clones the stack to a new object.
     * @return A clone of the IntStack.
     */
    public IntStack fclone() {
        IntStack ret = new IntStack(data.length);
        ret.curr_index = curr_index;
        for(int i = 0; i < curr_index; ++i) {
            ret.data[i] = data[i];
        }
        return ret;
    }
    
    /**
     * Removes all elements from the stack.
     */
    public void clear() {
        curr_index = 0;
    }
}
