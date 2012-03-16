package javamoprt;

public class MOPPMATransitionImpl {
    public int action;
    public MOPPMAStateImpl state;

    public MOPPMATransitionImpl(int action, MOPPMAStateImpl state){
    this.action = action;
    this.state = state;
  }
}

