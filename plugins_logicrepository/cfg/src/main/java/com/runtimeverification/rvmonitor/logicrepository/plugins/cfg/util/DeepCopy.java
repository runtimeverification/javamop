package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

/**
 * Utility class just to perform element deep copies.
 */
public class DeepCopy {
    
    /**
     * Construct a deep copy of an element.
     * @param orig The element to deep copy.
     * @return A deep copy of {@code orig}, or {@code null} if there is an error in copying.
     */
    public static <T> T copy(T orig) {
        T obj = null;
        try {
            ByteArrayOutputStream fbos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(fbos);
            out.writeObject(orig);
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(fbos.toByteArray()));
            obj = (T)in.readObject();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        catch(ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        return obj;
    }
}
