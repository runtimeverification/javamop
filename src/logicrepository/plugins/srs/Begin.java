package logicrepository.plugins.srs;

public final class Begin extends Symbol {

  public Begin() {}
  
  private static Begin theBegin = new Begin();

  public static Symbol get(){
    return theBegin;
  }

  public String toString(){
    return "^";
  }
} 
