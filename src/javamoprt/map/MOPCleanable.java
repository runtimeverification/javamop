package javamoprt.map;

import javamoprt.MOPObject;

public abstract class MOPCleanable  implements MOPObject{
	public boolean isDeleted = false;
	public boolean repeat = false;
	public MOPCleanable nextInQueue = null;
	public boolean isCleaning = false;

	/*
	 *  concurrent cleaner is disabled since it requires a serious revising.
	 */
	// protected static final boolean multicore = Runtime.getRuntime().availableProcessors() > 1;
	protected static final boolean multicore = false;

}
