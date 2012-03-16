package javamoprt;

public class MOPPMAStateImpl {
  public int number;
  public int[] replacement;
  public int category;

  public MOPPMAStateImpl(int number){
    this.number = number;
    this.replacement = null;
    this.category = -1;
  }

  public MOPPMAStateImpl(int number, int[] replacement){
    this.number = number;
    this.replacement = replacement;
    this.category = -1;
  }

  public MOPPMAStateImpl(int number, int category){
    this.number = number;
    this.replacement = null;
    this.category = category;
  }
}
