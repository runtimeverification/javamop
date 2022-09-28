package edu.cornell.cs5154;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Collections;
import java.util.Arrays;

import org.junit.Test;

public class AppTest
{
    @Test
    public void countSynchronizedCollection() {
        Collection c = Collections.synchronizedCollection(Arrays.asList("Foo", "Bar"));
        App a = new App();
        assertEquals(2, a.count(c));
    }
}
