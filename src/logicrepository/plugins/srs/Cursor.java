package logicrepository.plugins.srs;

public final class Cursor extends Symbol {

  private Cursor(){}

  private static Cursor theCursor = new Cursor();

  public static Symbol get(){
    return theCursor;
  }

  public String toString(){
    return "_";
  }
} 
