package javamoptestsuite;

import java.io.*;

public class TestClassLoader extends ClassLoader{
    private String path;
    
    public TestClassLoader(String path){
        this.path = path;
    }
    
    public Class findClass(String name) throws ClassNotFoundException{
        byte[] b = loadClassData(name);
        
        if (b == null){
            return super.findClass(name);
        }
        
        return defineClass(name, b, 0, b.length);
    }
    
    public byte[] loadClassData(String name){
        try{
            File classFile = new File(this.path + "/" + name + ".class");
            
            FileInputStream fileinputstream = new FileInputStream(classFile);
            int numberBytes = fileinputstream.available();
            byte bytearray[] = new byte[numberBytes];
            
            fileinputstream.read(bytearray);
            
            fileinputstream.close();
            
            return bytearray;
        } catch (Exception e){
            return null;
        }
        
    }
    
}
