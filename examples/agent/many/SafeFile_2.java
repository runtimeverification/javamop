
import java.io.*;
import java.util.*;

public class SafeFile_2{
    
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
        }
        try{
            System.out.println("close");
            fr.close();
        } catch (Exception e){
        }
        System.out.println("end");
    }
    
    public static void sub2(){
        sub1();
        sub1();
    }
    
    public static void main(String[] args){
        for(int i = 0; i < 5; i++){
            sub1();
        }
        
        for(int i = 0; i < 2; i++){
            sub2();
        }
        
    }
    
}
