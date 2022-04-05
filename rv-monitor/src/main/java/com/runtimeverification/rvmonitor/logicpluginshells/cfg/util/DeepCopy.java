package com.runtimeverification.rvmonitor.logicpluginshells.cfg.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DeepCopy {

    public static <T> T copy(T orig) {
        T obj = null;
        try {
            ByteArrayOutputStream fbos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(fbos);
            out.writeObject(orig);
            ObjectInputStream in = new ObjectInputStream(
                    new ByteArrayInputStream(fbos.toByteArray()));
            obj = (T) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        return obj;
    }
}
