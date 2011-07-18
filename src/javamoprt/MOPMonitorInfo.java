package javamoprt;

import java.util.Arrays;

public class MOPMonitorInfo implements Cloneable, MOPObject{
	public boolean isFullParam = false;
	public int[] connected = null;
	
	public Object clone(){
		try{
			MOPMonitorInfo ret = (MOPMonitorInfo)super.clone();
			if(this.connected != null)
				ret.connected = Arrays.copyOf(this.connected, this.connected.length);
			return ret;
		} catch(CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}
	
}
