package javamop.output.combinedaspect.event;

import javamop.JavaMOPMain;
import javamop.JavaMOPOptions;
import javamop.output.combinedaspect.event.EventManager;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hx312 on 3/25/2016.
 */
public class EventMethodHelperTest {
    static {
        if (JavaMOPMain.options == null)
            JavaMOPMain.options = new JavaMOPOptions();
    }
    /**
     * Set JavaMOPMain.options for testing predicate 1.
     */
    private void setOptions4P1(boolean merge, String aspectName) {
        JavaMOPMain.options.merge = merge;
        JavaMOPMain.options.aspectname = aspectName;
    }

    @Test
    public void methodName_pred1Test() throws Exception {
        //predicate 1:
        // JavaMOPMain.options.merge && JavaMOPMain.options.aspectname != null &&
        // JavaMOPMain.options.aspectname.length() > 0

        // Correlated Active Clause Coverage is impossible for the three clauses,
        // CACC: TTT, FTT, TFT (impossible), TTF
        // Use clause coverage and predicate coverage instead (two tests are enough: TTT, FFF)

        setOptions4P1(true, "foo"); //TTT
        String tttMethName = EventManager.EventMethodHelper.methodName("HasNext", "next", "HasNext");
        assertEquals("HasNextRuntimeMonitor.HasNext_nextEvent", tttMethName);

        setOptions4P1(false, null); //FFF
        String fffMethName = EventManager.EventMethodHelper.methodName("HasNext", "next", "HasNext");
        assertEquals("HasNextRuntimeMonitor.nextEvent", fffMethName);
    }
}