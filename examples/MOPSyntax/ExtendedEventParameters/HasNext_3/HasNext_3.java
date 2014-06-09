
import java.util.*;

public class HasNext_3 {
    public static void main(String[] args) {
        Vector<Integer> v = new Vector<Integer>();
        v.add(1); v.add(2);
        Iterator it = v.iterator();
        while(it.hasNext()) {
            int sum = (Integer)it.next() + (Integer)it.next();
            System.out.println("sum = " + sum);
        }
    }
}
