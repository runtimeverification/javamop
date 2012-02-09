package logicrepository.plugins.srs;

import java.util.ArrayList;

public class ActionSRS {
  public ArrayList<Variable> actions;
  public SRS srs;

  @Override
  public String toString(){
    return actions.toString() + "\n" + srs.toString();
  }

  public String toPaddedString(String padding){
    return padding + actions.toString() + "\n" + srs.toPaddedString(padding);
  }

  public ActionSRS(ArrayList<Variable> actions, SRS srs){
    this.actions = actions;
    this.srs     = srs;
  }

  public ActionSRS makeFinal(){
    return new ActionSRS(actions, srs.makeFinal());
  }
}
