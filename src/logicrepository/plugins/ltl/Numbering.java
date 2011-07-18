package logicrepository.plugins.ltl;

import java.util.HashMap;
import java.util.Set;
import java.util.LinkedHashSet;

public class Numbering<Key> extends HashMap<Key, Integer>{
  private int count;

  public Integer get(Object k){
    if(containsKey(k)) return super.get(k);
    put((Key)k, count);
    return count++;
  }

  public LinkedHashSet<Integer> map(LinkedHashSet<Key> set){
    LinkedHashSet<Integer> ret = new LinkedHashSet();
    for(Key item : set){
      ret.add(get(item)); 
    } 
    return ret;
  }
} 
