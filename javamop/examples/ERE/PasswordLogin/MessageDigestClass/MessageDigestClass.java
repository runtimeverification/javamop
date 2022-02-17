// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
import java.security.*;
import java.io.*;


public class MessageDigestClass {
    
    public static void main(String[] args) throws Exception {
        // Create a Message Digest from a Factory method
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        
        //This part is commented so that the property of message digest is violated.
        /*
         *          String Password = "Get In";
         *          byte[] msg = Password.getBytes();
         *          md.update(msg); */
        
        byte[] aMessageDigest = md.digest();
        
        // Printout
        //      System.out.println("Original: " + new String(msg));
        //      System.out.println("Message Digest: " + new String(aMessageDigest));
    }
    
}
