package logicrepository.plugins.srs;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class SRS extends LinkedHashSet<Rule> {
  private Set<Symbol> terminals = new HashSet<Symbol>();

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

  @Override
  public boolean add(Rule r){
    terminals.addAll(r.getTerminals());
    return super.add(r);
  }

  public Set<Symbol> getTerminals(){
    return terminals;
  }
}

