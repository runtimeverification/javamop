// Introduction to Software Testing
// Authors: Paul Ammann & Jeff Offutt
// Chapter 1; page ??
// JUnit for Calc.java

package edu.cornell.cs5154;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

@RunWith (Parameterized.class)
public class DataDrivenCalcTest
{
   public int a, b, sum;

   public DataDrivenCalcTest (int a, int b, int sum)
   {
      this.a = a;
      this.b = b;
      this.sum = sum;
   }

  @Parameters
  public static Collection<Object[]> calcValues()
  {
     return Arrays.asList (new Object [][] {{1, 1, 2},
                                            {2, 3, 5},
                                            {10000, 30000, 40000},
                                            {5, -3, 2},
                                            {Integer.MAX_VALUE, Integer.MIN_VALUE, -1},
                                            {Integer.MAX_VALUE, 1, Integer.MIN_VALUE}});
  }

  @Test
  public void additionTest()
  {
     assertTrue ("Addition Test", sum == Calc.add (a,b));
  }
}
