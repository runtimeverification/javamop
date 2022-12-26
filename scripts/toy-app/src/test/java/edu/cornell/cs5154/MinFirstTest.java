package edu.cornell.cs5154;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MinFirstTest
{
   @Test
   public void testForNullList()
   {
      List<String> list = null;
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
       List<String> list = new ArrayList<String>();
       list.add (null);
       list.add ("cat");
       Min.min (list);
   }

   @Test (expected = NullPointerException.class)
   public void testForSoloNullElement()
   {
       List<String> list = new ArrayList<String>();
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
      List<String> list = new ArrayList<String>();
      Min.min (list);
   }

   @Test
   public void testSingleElement()
   {
       List<String> list = new ArrayList<String>();
       list.add ("cat");
       Object obj = Min.min (list);
       assertTrue ("Single Element List", obj.equals ("cat"));
   }

   @Test
   public void testDoubleElement()
   {
       List<String> list = new ArrayList<String>();
       list.add ("dog");
       list.add ("cat");
       Object obj = Min.min (list);
       assertTrue ("Double Element List", obj.equals ("cat"));
   }
}
