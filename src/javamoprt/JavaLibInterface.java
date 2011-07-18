package javamoprt;

public interface JavaLibInterface {
	enum Category { Match, Fail, Unknown };
	boolean isCoreachable();
	void process(String s);
	Category getCategory();
}
