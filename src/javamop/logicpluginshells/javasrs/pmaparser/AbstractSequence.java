package javamop.logicpluginshells.javasrs.pmaparser;

import java.util.Map;

public interface AbstractSequence { 
  public String toDotString(); 
  public int dotLength();
  public void getImpl(StringBuilder sb, Map<Symbol, Integer> symToNum);
}

