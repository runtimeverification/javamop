package logicrepository.plugins.srs;

public class Succeed implements AbstractSequence {

  private Succeed(){}

  public String toString(){
    return "#succeed";
  }

  public static Succeed theSucceed = new Succeed();

  public static Succeed get(){
    return theSucceed;
  }
}
