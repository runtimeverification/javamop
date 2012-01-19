package logicrepository.plugins.srs;

public final class Epsilon extends Symbol {

  public Epsilon() {}
  
  private static Epsilon theEpsilon = new Epsilon();

  public static Symbol get(){
    return theEpsilon;
  }

  public String toString(){
    return "\\epsilon";
  }
} 
