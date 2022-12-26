// Introduction to Software Testing
// Authors: Paul Ammann & Jeff Offutt
// Chapter 3; page ??
// JUnit for Calc.java

package edu.cornell.cs5154;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CalcTest
{
   @Test public void testAddSame()
   {
       assertTrue ("Calc sum incorrect", 2 == Calc.add (1, 1));
   }

   @Test public void testAddDifferent()
   {
       assertTrue ("Calc sum incorrect", 5 == Calc.add (2, 3));
   }

   @Test public void testAddBig()
   {
       assertTrue ("Calc sum incorrect", 40000 == Calc.add (10000, 30000));
   }

   @Test public void testAddPositiveAndNegative()
   {
       assertTrue ("Calc sum incorrect", 2 == Calc.add (5, -3));
   }
}
