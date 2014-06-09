
import java.util.*;

public class SafeEnum_1 {
    public static void main(String[] args){
        Vector<Integer> v = new Vector<Integer>();
        
        v.add(1);
        v.add(2);
        v.add(4);
        v.add(8);
        
        Enumeration e = v.elements();
        
        int sum = 0;
        
        if(e.hasMoreElements()){
            sum += (Integer)e.nextElement();
            v.add(11);
        }
        
        while(e.hasMoreElements()){
            sum += (Integer)e.nextElement();
        }
        
        v.clear();
        
        System.out.println("sum: " + sum);
    }
}
