package logicrepository.plugins.srs;

public class Variable extends Symbol {
  private Variable(String name){
    this.name = name;
  }

  public static Symbol get(String name){
    Symbol ret = symbolTable.get(name);
    if(ret == null){
      ret = new Variable(name);
      symbolTable.put(name, ret);
    } 
    return ret;
  }
}
