package javamop.output.combinedaspect;

import java.util.List;

import javamop.MOPException;
import javamop.output.MOPVariable;
import javamop.parser.ast.mopspec.JavaMOPSpec;

public class LockManager {
    
    //HashMap<JavaMOPSpec, GlobalLock> locks = new HashMap<JavaMOPSpec, GlobalLock>();
    
    private final GlobalLock lock;
    
    public LockManager(String name, List<JavaMOPSpec> specs) throws MOPException {
        //      for (JavaMOPSpec spec : specs) {
        //          if (spec.isSync())
        //              locks.put(spec, new GlobalLock(new MOPVariable(spec.getName() + "_MOPLock")));
        //      }
        
        lock = new GlobalLock(new MOPVariable(name + "_MOPLock"));
    }
    
    /*  public GlobalLock getLock(JavaMOPSpec spec){
     *        return locks.get(spec);
}
*/
    public GlobalLock getLock(){
        return lock;
    }
    
    public String decl() {
        String ret = "";
        
        /*      if (locks.size() <= 0)
         *            return ret;
         */
        /*      ret += "// Declarations for Locks \n";
         *        for (GlobalLock lock : locks.values()) {
         *            ret += lock;
    }
    ret += "\n";
    */
        ret += "// Declarations for the Lock \n";
        ret += lock;
        ret += "\n";
        
        return ret;
    }
    
}
