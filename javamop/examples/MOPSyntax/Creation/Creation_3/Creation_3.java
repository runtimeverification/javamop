// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.

import java.util.*;

public class Creation_3 {
    public int temp = 0;
    public void fun1(){
        temp = 1;
    }
    public void fun2(){
        temp = 2;
    }
    
    public static void main(String[] args){
        Creation_3 o = new Creation_3();
        
        System.out.println("fun2");
        o.fun2();
        System.out.println("fun1");
        o.fun1();
        System.out.println("fun2");
        o.fun2();
        System.out.println("fun1");
        o.fun1();
        
        System.out.println("main end");
    }
}
