package javamop.helper;

import javamop.parser.ast.MOPSpecFile;

import java.io.*;

/**
 * Created by hx312 on 4/14/2016.
 * The implementation is modified from the tutorial:
 * http://www.tutorialspoint.com/java/java_serialization.htm
 */
public class MOP_Serialization {
    /**
     * Method for serializing an mop spec object to a file.
     * @param mopSpecFile The mop spec object that is going to be serialized.
     * @param outputPath The path in which the serialization of mop spec object will be stored.
     */
    public static void writeMOPSpecObject2File(MOPSpecFile mopSpecFile, String outputPath) {
        try
        {
            FileOutputStream fileOut =
                    new FileOutputStream(outputPath);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(mopSpecFile);
            out.close();
            fileOut.close();
            System.out.println("Serialized data of spec is stored into " + outputPath);
        }catch(IOException i)
        {
            i.printStackTrace();
        }
    }

    /**
     * Deserialize an MOP spec object from a file.
     * @param inputPath The path to the serialization file.
     * @return The MOP spec object.
     */
    public static MOPSpecFile readMOPSpecObjectFromFile(String inputPath) {
        MOPSpecFile mopSpecFile = null;

        try
        {
            FileInputStream fileIn = new FileInputStream(inputPath);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            mopSpecFile = (MOPSpecFile) in.readObject();
            in.close();
            fileIn.close();
        }catch(IOException i)
        {
            i.printStackTrace();
        }catch(ClassNotFoundException c)
        {
            System.out.println("MOPSpecFile class not found.");
            c.printStackTrace();
        }

        return mopSpecFile;
    }
}
