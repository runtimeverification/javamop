// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.

import java.util.*;

public class HasNext_3 {
    public static void main(String[] args) {
        Vector<Integer> v = new Vector();
        v.add(1); v.add(2);
        Iterator it = v.iterator();
        while(it.hasNext()) {
            int sum = (Integer)it.next() + (Integer)it.next();
            System.out.println("sum = " + sum);
        }
    }
}
