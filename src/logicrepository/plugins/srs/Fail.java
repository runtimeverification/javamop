package logicrepository.plugins.srs;

public class Fail implements AbstractSequence {

  private Fail(){}

  public String toString(){
    return "#fail";
  }

  public String toDotString(){
    return "\\#fail";
  }

  public static Fail theFail = new Fail();

  public static Fail get(){
    return theFail;
  }

  @Override
  public int dotLength(){
    return 5;
  }

}
