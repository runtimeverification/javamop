package logicrepository.plugins.srs;

import java.util.ArrayList;

public class SRS extends ArrayList<Rule> {
  public SRS(){
    super();
  }

  public String toString(){
    StringBuilder sb = new StringBuilder();
    for(Rule r : this){
      sb.append(r.toString());
      sb.append(",\n");
    }
    return sb.toString();
  }
}

