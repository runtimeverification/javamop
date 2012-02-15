package logicrepository.plugins.srs;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class SRS extends LinkedHashSet<Rule> {

  public SRS(){
    super();
  }

  @Override
  public String toString(){
    StringBuilder sb = new StringBuilder();
    for(Rule r : this){
      sb.append(r.toString());
      sb.append(",\n");
    }
    return sb.toString();
  }

  public String toPaddedString(String padding){
    StringBuilder sb = new StringBuilder();
    for(Rule r : this){
      sb.append(padding);
      sb.append(r.toString());
      sb.append(".\n");
    }
    return sb.toString();
  }
}

