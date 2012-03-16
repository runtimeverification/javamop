public class Test {

static TransitionImpl [][] pma = {
  {new TransitionImpl(0, new StateImpl(4, 0)),
  new TransitionImpl(0, new StateImpl(1)),
  },

{new TransitionImpl(0, new StateImpl(2, new int[] {})),
new TransitionImpl(0, new StateImpl(3, new int[] {1,})),
},

{new TransitionImpl(1, new StateImpl(4, 0)),
new TransitionImpl(1, new StateImpl(4, 0)),
},

{new TransitionImpl(1, new StateImpl(1)),
new TransitionImpl(1, new StateImpl(1)),
},

{new TransitionImpl(1, new StateImpl(0)),
new TransitionImpl(1, new StateImpl(0)),
},

};


  public static void main(String[] args){
    System.out.println("ran");
    TransitionImpl t = pma[0][0];
    System.out.println("s0, next");
    System.out.println("action = " + t.action);
    System.out.println("next state = " + t.state.number);
    System.out.println("next state replacement = " + t.state.replacement);
    System.out.println("next state category = " + t.state.category);

    System.out.println("s1, hasnext");
    t = pma[1][1];
    System.out.println("action = " + t.action);
    System.out.println("next state = " + t.state.number);
    System.out.println("next state replacement = " + t.state.replacement);
    System.out.println("next state category = " + t.state.category);
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
