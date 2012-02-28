package javamoprt;

public class MOPMultiMapSignature {
	static public final int MAP_OF_MAP = 1;
	static public final int MAP_OF_SET = 2;
	static public final int MAP_OF_MONITOR = 3;

	protected int type;
	protected int idnum;

	public MOPMultiMapSignature(int type, int idnum) {
		this.type = type;
		this.idnum = idnum;
	}

}
