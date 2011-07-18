package javamop.logicpluginshells.javacfg.cfgutil;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

public class DeepCopy {

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
