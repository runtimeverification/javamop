package logicrepository.plugins.srs;

public final class End extends Symbol {

  public End() {}
  
  private static End theEnd = new End();

  public static Symbol get(){
    return theEnd;
  }

  public String toString(){
    return "$";
  }
} 
