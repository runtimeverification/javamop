package com.runtimeverification.rvmonitor.java.rt;

import java.util.ArrayList;
import java.util.HashSet;

public class RVMStat implements RVMObject {
	static public int created_monitors = 0;
	static public int terminated_montors = 0;
	
	static public int success_cleanup = 0;
	static public int fail_cleanup = 0;

	static public int success_cleanup_oneiter = 0;
	static public int fail_cleanup_oneiter = 0;

//	static public RVMTimer Timer_total = new RVMTimer();
//	static public RVMTimer Timer_cleaning = new RVMTimer();
	
	static public RVMTimer timer = new RVMTimer();
	static public RVMTimer timer1 = new RVMTimer();
	static public RVMTimer timer2 = new RVMTimer();
	static public RVMTimer timer3 = new RVMTimer();
	static public RVMTimer timer4 = new RVMTimer();
	static public RVMTimer timer5 = new RVMTimer();

	
	static public long point1 = 0;
	static public long point2 = 0;
	static public long point3 = 0;
	static public long point4 = 0;
	static public long point5 = 0;
	static public long point6 = 0;
	static public long point7 = 0;
	static public long point8 = 0;
	static public long point9 = 0;
	static public long point10 = 0;
	static public long point11 = 0;
	static public long point12 = 0;
	static public long point13 = 0;
	static public long point14 = 0;
	static public long point15 = 0;

	
	static public HashSet<String> locations = new HashSet<String>();
	static public ArrayList<Boolean> idSet = new ArrayList<Boolean>();
	
	
	
	
}
