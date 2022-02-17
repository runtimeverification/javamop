// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.

import java.io.*;
import java.util.*;

public class SafeFile_1{
    
    static FileReader fr = null;
    
    public static void sub1(){
        System.out.println("begin");
        
        File file = null;
        try{
            file = File.createTempFile("javamoptest1", ".tmp");
            FileWriter fw_1 = new FileWriter(file);
            fw_1.write("testing\n");
            fw_1.close();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        
        try{
            System.out.println("open");
            fr = new FileReader(file);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        try{
            System.out.println("close");
            fr.close();
        } catch (Exception e){
        }
        System.out.println("end");
    }
    
    public static void sub2(){
        System.out.println("begin");
        
        File file = null;
        try{
            file = File.createTempFile("javamoptest1", ".tmp");
            FileWriter fw_1 = new FileWriter(file);
            fw_1.write("testing\n");
            fw_1.close();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        
        try{
            System.out.println("open");
            fr = new FileReader(file);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println("end");
    }
    
    public static void sub3(){
        System.out.println("begin");
        try{
            System.out.println("close");
            fr.close();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println("end");
    }
    
    public static void main(String[] args){
        sub1();
        sub2();
        sub3();
        
    }
    
}
