package javamoprt;

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
	

}
