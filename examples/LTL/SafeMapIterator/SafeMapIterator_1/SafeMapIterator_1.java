import java.util.*;

public class SafeMapIterator_1 {
    public static void main(String[] args){
        try{
            Map<String, String> testMap = new HashMap<String,String>();
            testMap.put("Foo", "Bar");
            testMap.put("Bar", "Foo");
            Set<String> keys = testMap.keySet();
            Iterator i = keys.iterator();
            testMap.put("breaker", "borked");
            System.out.println(i.next());
        }
        catch(Exception e){
            System.out.println("java found the problem too");
        }
    }
}
