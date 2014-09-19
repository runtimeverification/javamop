import org.aspectj.lang.Signature;
/**
 * Created by hx312 on 13/09/2014.
 */
public aspect FindMethods_RVM_ALL {
    pointcut inJavamop(): within(javamop..*);
    pointcut methodCall(): call(* com.runtimeverification.rvmonitor..*(..)) && !cflow(within(FindMethods_RVM_ALL));

    pointcut ourMethods():  inJavamop() && methodCall();
    before():ourMethods(){
        Signature sig = thisJoinPoint.getSignature();
        String line =""+ thisJoinPoint.getSourceLocation().getLine();
        String sourceName = thisJoinPoint.getSourceLocation().getWithinType().getCanonicalName();

        System.out.println(sig+" "+line+" "+sourceName);
    }

}
