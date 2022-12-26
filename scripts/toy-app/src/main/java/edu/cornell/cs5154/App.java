package edu.cornell.cs5154;

import java.util.Collection;

public class App
{
    public int count (Collection<String> collection) {
        int count = 0;
        for (String element : collection) {
            count++;
        }
        return count;
    }
}
