package logicrepository.plugins.srs;

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
    IntSpliceList l = new IntSpliceList();
    for(String s : args){
      if(s.equals("hasnext"))
        l.add(0);
      if(s.equals("next"))
        l.add(1);
      else throw new RuntimeException("unknown input " + s);
    }
    rewrite(l);
    System.out.println("substituted form = " + l.toString());
  }

  static private void rewrite(IntSpliceList l){
   System.out.println("rewriting:");
    System.out.println("   " + l + "\n=========================================");
    if(l.isEmpty()) return;
    SLIntIterator first;
    SLIntIterator second;
    SLIntIterator lastRepl = null;
    int currentState;
    TransitionImpl trans;
    int symbol; 
    boolean changed;
    boolean atOrPastLastChange;
    do {
    currentState = 0;
    atOrPastLastChange = false;
    changed = false;
    first = l.head();
    second = l.head();
    symbol = second.get();
    //System.out.println("******************outer*****");
    while(true){
      trans = pma[currentState][symbol];
      //System.out.println("*" + symbol + " -- " + as);
      //adjust the first pointer
      if(currentState == 0 && trans.state.number == 0){
        //System.out.println("false 0 transition");
        if(!first.next()) break;
      }
      else {
        first.next(trans.action);
      }
      if(trans.state.category == 0){
        System.out.println("Fail!");
        return;
      }
      if(trans.state.category == 1){
        System.out.println("Succeed!");
        return;
      }
      if(trans.state.replacement != null){
        first.nonDestructiveSplice(second, trans.state.replacement);
        if(l.isEmpty()) return;
        changed = true;
        atOrPastLastChange = false; 
        lastRepl = second;
        second = first.copy();
        currentState = 0;
        symbol = second.get();
        if(symbol == -1) break;
        continue;
      }
      currentState = trans.state.number;
      //normal transition
      if(trans.action == 0){
        if(!second.next()) break;
        if(!changed){
          if(second.equals(lastRepl)){
            atOrPastLastChange = true; 
          }
          if(atOrPastLastChange && currentState == 0){
            return;
          }
        }
        symbol = second.get();
      }
      //fail transition, need to reconsider he same symbol in next state
    }
    } while(changed);
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
