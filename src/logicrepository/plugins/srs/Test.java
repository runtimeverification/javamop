public class Test {

  static TransitionImpl [][] arr = 
   {
     {new TransitionImpl(0, new StateImpl(1, new int[] {1, 2, 3})),
    new TransitionImpl(0, new StateImpl(15))},
   
     {new TransitionImpl(0, new StateImpl(1, new int[] {1, 2, 3})),
    new TransitionImpl(0, new StateImpl(0))},
   };

  public static void main(String[] args){
    TransitionImpl trans = arr[0][1];
    System.out.println(trans.state.number);
  }
}

class TransitionImpl {
  int action;
  StateImpl state;

  public TransitionImpl(int action, StateImpl state){
    this.action = action;
    this.state = state;
  }
}

class StateImpl {
  int number;
  int[] replacement;
  int category;

  public StateImpl(int number){
    this.number = number;
    this.replacement = null;
    this.category = -1;
  }

  public StateImpl(int number, int[] replacement){
    this.number = number;
    this.replacement = replacement;
    this.category = -1;
  }

  public StateImpl(int number, int category){
    this.number = number;
    this.replacement = null;
    this.category = category;
  }
}
