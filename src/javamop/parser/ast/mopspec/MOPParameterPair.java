package javamop.parser.ast.mopspec;

public class MOPParameterPair {
	MOPParameters param1;
	MOPParameters param2;
	
	public MOPParameterPair(MOPParameters param1, MOPParameters param2){
		this.param1 = param1;
		this.param2 = param2;
	}
	
	public MOPParameters getParam1(){
		return param1;
	}
	
	public MOPParameters getParam2(){
		return param2;
	}

	public boolean equals(MOPParameterPair mopPair) {
		return this.getParam1().equals(mopPair.getParam1()) && this.getParam2().equals(mopPair.getParam2());
	}

	public String toString() {
		return "(" + param1.toString() + ", " + param2.toString() + ")";
	}
	
	public int hashCode() {
		return param1.hashCode() ^ param2.hashCode();
	}
}
