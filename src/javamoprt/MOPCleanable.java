package javamoprt;

public abstract class MOPCleanable {
	protected boolean isDeleted = false;
	protected boolean repeat = false;
	public MOPCleanable nextInQueue = null;
	public boolean isCleaning = false;

}
