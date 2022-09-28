package edu.cornell.cs5154;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MinTest
{
   private List<String> list;   // Test fixture

   @Before      // Set up - Called before every test method.
   public void setUp()
   {
      list = new ArrayList<String>();
   }

   @After      // Tear down - Called after every test method.
   public void tearDown()
   {
      list = null;  // redundant in this example!
   }

   @Test
   public void testForNullList()
   {
      list = null;
      try {
         Min.min (list);
      } catch (NullPointerException e) {
         return;
      }
      fail ("NullPointerException expected");
   }

   @Test (expected = NullPointerException.class)
   public void testForNullElement()
   {
      list.add (null);
      list.add ("cat");
      Min.min (list);
   }

   @Test (expected = NullPointerException.class)
   public void testForSoloNullElement()
   {
      list.add (null);
      Min.min (list);
   }

   @Test (expected = ClassCastException.class)
   @SuppressWarnings ("unchecked")
   public void testMutuallyIncomparable()
   {
      List list = new ArrayList();
      list.add ("cat");
      list.add ("dog");
      list.add (1);
      Min.min (list);
   }

   @Test (expected = IllegalArgumentException.class)
   public void testEmptyList()
   {
      Min.min (list);
   }

   @Test
   public void testSingleElement()
   {
      list.add ("cat");
      Object obj = Min.min (list);
      assertTrue ("Single Element List", obj.equals ("cat"));
   }

   @Test
   public void testDoubleElement()
   {
      list.add ("dog");
      list.add ("cat");
      Object obj = Min.min (list);
      assertTrue ("Double Element List", obj.equals ("cat"));
   }
}
