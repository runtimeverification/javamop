package javamoprt;

import java.util.ArrayList;
import java.util.HashSet;

public class MOPStat implements MOPObject{
	static public int created_monitors = 0;
	static public int terminated_montors = 0;
	
	static public int success_cleanup = 0;
	static public int fail_cleanup = 0;

	static public int success_cleanup_oneiter = 0;
	static public int fail_cleanup_oneiter = 0;

//	static public MOPTimer Timer_total = new MOPTimer();
//	static public MOPTimer Timer_cleaning = new MOPTimer();
	
	static public MOPTimer timer = new MOPTimer();
	static public MOPTimer timer1 = new MOPTimer();
	static public MOPTimer timer2 = new MOPTimer();
	static public MOPTimer timer3 = new MOPTimer();
	static public MOPTimer timer4 = new MOPTimer();
	static public MOPTimer timer5 = new MOPTimer();

	
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
