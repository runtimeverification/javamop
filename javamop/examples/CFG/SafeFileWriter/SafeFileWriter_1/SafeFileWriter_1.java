// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.

import java.util.*;
import java.io.*;

public class SafeFileWriter_1 {
    public static void main(String[] args){
        FileWriter fw_1=null;
        FileWriter fw_2=null;
        FileWriter fw_3=null;
        FileWriter fw_4=null;
        FileWriter fw_5=null;
        try{
            fw_1 = new FileWriter(File.createTempFile("javamoptest1", ".tmp"));
            fw_2 = new FileWriter(File.createTempFile("javamoptest2", ".tmp"));
            fw_3 = new FileWriter(File.createTempFile("javamoptest3", ".tmp"));
            fw_4 = new FileWriter(File.createTempFile("javamoptest4", ".tmp"));
            fw_5 = new FileWriter(File.createTempFile("javamoptest5", ".tmp"));
            
            fw_1.write("testing\n");
            fw_2.write("testing\n");
            fw_3.write("testing\n");
            fw_4.write("testing\n");
            fw_5.write("testing\n");
            
            fw_1.write("testing\n");
            fw_2.write("testing\n");
            fw_4.write("testing\n");
            fw_5.write("testing\n");
            
            fw_1.write("testing\n");
            fw_3.write("testing\n");
            fw_5.write("testing\n");
            
            fw_1.close();
            fw_2.close();
            fw_3.close();
            fw_4.close();
            fw_5.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        try{
            fw_1.write("testing\n");
        } catch (Exception e) {
        }
        try{
            fw_2.write("testing\n");
        } catch (Exception e) {
        }
        try{
            fw_4.write("testing\n");
        } catch (Exception e) {
        }
    }
}
