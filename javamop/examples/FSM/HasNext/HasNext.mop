// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package mop;

import java.io.*;
import java.util.*;

// This property specifies that a program does
// not call the hasnext method  before the next
// method of an iterator. 
// This property is borrowed from tracematches 
// (see ECOOP'07 http://abc.comlab.ox.ac.uk/papers)

full-binding HasNext(Iterator i) {
   event hasnext after(Iterator i) : 
      call(* Iterator.hasNext()) && target(i) {} 
   event next before(Iterator i) : 
      call(* Iterator.next()) && target(i) {}

   fsm :
     start [
        next -> unsafe
        hasnext -> safe
     ]
     safe [
        next -> start
        hasnext -> safe 
     ]
     unsafe [
        next -> unsafe
        hasnext -> safe
     ]

     alias match = unsafe
	
   @match {
      System.out.println("next called without hasNext!");
   }
}









