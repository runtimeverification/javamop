package com.runtimeverification.rvmonitor.java.rt;

import java.util.Arrays;

public class RVMMonitorInfo implements Cloneable, RVMObject {
	public boolean isFullParam = false;
	public int[] connected = null;
	
	public RVMMonitorInfo clone(){
		try{
			RVMMonitorInfo ret = (RVMMonitorInfo)super.clone();
			if(this.connected != null)
				ret.connected = Arrays.copyOf(this.connected, this.connected.length);
			return ret;
		} catch(CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}
	
}
