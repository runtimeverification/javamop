// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
import java.util.*;

public class HasNext2_2 {
    public static void main(String[] args) {
        Vector<Integer> v = new Vector<Integer>();
        v.add(1);
        v.add(2);
        
        Iterator i = v.iterator();
        int sum = 0;
        
        // JavaMOP should not match "next next"
        if (i.hasNext()) {
            sum += (Integer)i.next();
        }
        
        System.out.println("sum: " + sum);
    }
}
