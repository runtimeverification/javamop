package com.runtimeverification.rvmonitor.java.rt;

import java.util.Collection;

public class RVMIntSpliceList {

  protected class Node {
    protected int element;
    protected Node prev = null;
    protected Node next = null;

    protected Node(){ 
      element = -1; 
    }

    protected Node(int element){
      this.element = element;
    }

    @Override
    public String toString(){
      StringBuilder sb = new StringBuilder("<");
      sb.append((prev == null)?"<>":prev.toStringPrev());
      sb.append(" [");
      sb.append(element);
      sb.append("] "); 
      sb.append((next == null)?"<>":next.toStringNext());
      sb.append(">");
      return sb.toString();
    }

    private StringBuilder toStringPrev(){
      StringBuilder sb = new StringBuilder((prev == null)?"<>":prev.toStringPrev());
      sb.append(" ");
      sb.append(element);
      return sb;
    }

    private StringBuilder toStringNext(){
      StringBuilder sb = new StringBuilder(element);
      sb.append(element);
      sb.append(" ");
      sb.append((next == null)?"<>":next.toStringNext());
      return sb;
    }
  }

  private Node head;
  private Node tail;

  public RVMIntSpliceList(){
    head = tail = null;
  }

  public RVMIntSpliceList(Collection<Integer> c){
    for(Integer e : c){
      add(e);
    }
  }

  public RVMIntSpliceList(RVMIntSpliceList c){
    RVMSLIntIterator I = c.head();
    do {
      add(I.get());
    } while(I.next());  
  }

  public RVMIntSpliceList(int[] c){
    for(int e : c){
      add(e);
    }
  }

  public boolean isEmpty(){
    return head == null;
  }

  public int headElem(){
    if(head == null) return -1;
    return head.element;
  }

  public int tailElem(){
    if(tail == null) return -1;
    return tail.element;
  }

  public void add(int element){
    if(head == null){
      head = tail = new Node(element);
      return;
    } 
    tail.next = new Node(element);
    tail.next.prev = tail;
    tail = tail.next;
  }

  public void addFront(int element){
    if(head == null){
      head = tail = new Node(element);
      return;
    } 
    Node node = new Node(element);
    head.prev = node;
    node.next = head;
    head = node;
  }

  public void insertBeforeTail(int element){
    if(head == null){
      head = tail = new Node(element);
      return;
    } 
    Node node = new Node(element);
    node.prev = tail.prev;
    tail.prev.next = node;
    tail.prev = node;
    node.next = tail;
  }

  public void add(Collection<Integer> c){
    for(Integer e : c){
      add(e);
    }
  }

  public void add(int[] c){
    for(int e : c){
      add(e);
    }
  }

  @Override
  public String toString(){
    if(isEmpty()) return "#epsilon";
    Node current = head;
    StringBuilder sb = new StringBuilder("[");
    do {
     sb.append(current.element);
     sb.append(" ");
     current = current.next; 
    } while(current != null); 
    sb.setCharAt(sb.length() - 1, ']'); 
    return sb.toString();
  } 

  //C++ style iterator, because it's far superior
  public class RVMSLIntIteratorImpl implements RVMSLIntIterator {
    protected Node node; 

    protected RVMSLIntIteratorImpl(Node node){
      this.node = node;
    }

    public RVMSLIntIteratorImpl copy(){
      return new RVMSLIntIteratorImpl(node);
    }

    public boolean next(){
      if(node == null || node.next == null) return false;
      node = node.next;
      return true;
    }

    public boolean next(int amount){
      for(int i = 0; i < amount; ++i){
        if(!next()) return false;
      }
      return true;
    }

    public boolean previous(){
      if(node.prev == null) return false;
      node = node.prev;
      return true;
    }

    public boolean previous(int amount){
      for(int i = 0; i < amount; ++i){
        if(!previous()) return false;
      }
      return true;
    }

    public int get(){
      if(node == null) return -1;
      return node.element;
    }

    //WARNING:  This assumes iterators point to the same list!
    public void splice(RVMSLIntIterator end, RVMIntSpliceList replacement){
      RVMSLIntIteratorImpl endImpl;
      try {
        endImpl = (RVMSLIntIteratorImpl) end;
      }
      catch(ClassCastException e){
        throw new IllegalArgumentException("Must provide an RVMSLIntIteratorImpl to splice");
      }
      if(isEmpty()) {
        if(replacement.isEmpty()) return;
        head = replacement.head;
        tail = replacement.tail;
        node = head;
        endImpl.node = head;
        return;
      }  
      //we are splicing empty into something not empty
      if(replacement.isEmpty()){
        spliceEmptyRepl(endImpl);
        return;
      }
      //we are splicing something not empty into something not empty
      spliceNonEmptyRepl(endImpl, replacement);
    }

    private void spliceEmptyRepl(RVMSLIntIteratorImpl endImpl){
      if(node == head){
        if(endImpl.node == tail){
          head = tail = node = endImpl.node = null;
          return;
        }
        head = endImpl.node.next; 
        head.prev = null;
        node = head;
        endImpl.node = head;
        return;
      }
      if(node == tail){
        tail = tail.prev;
        tail.next = null;
        node = null;
        endImpl.node = null;
        return;
      } 
      Node prev = node.prev;
      Node next = endImpl.node.next;
      prev.next = next;
      if(next != null) next.prev = prev;
      node = next;
      if (endImpl.node == tail) {
        tail = prev; 
      }
      endImpl.node = next;
    }

    private void spliceNonEmptyRepl(RVMSLIntIteratorImpl endImpl, RVMIntSpliceList replacement){
      if(node == head){
        if(endImpl.node == tail){
          head = replacement.head;
          tail = replacement.tail;
          node = replacement.head;
          replacement.head = replacement.tail = endImpl.node = null;
          return;
        }
        head = replacement.head;
        node = head;
        replacement.head = null;
        endImpl.node = endImpl.node.next;
        replacement.tail.next = endImpl.node; 
        endImpl.node.prev = replacement.tail;
        replacement.tail = null;
        return;
      }    
      if(node == tail){
        endImpl.node = null;
        tail = replacement.tail;
        node.prev.next = replacement.head;
        replacement.head.prev = node.prev;
        node = replacement.head;
        replacement.tail = replacement.head = null;
        return;
      }
      node.prev.next = replacement.head;
      replacement.head.prev = node.prev;

      replacement.tail.next = endImpl.node.next;
      if(endImpl.node.next != null){
        endImpl.node.next.prev = replacement.tail;
      }
      if(endImpl.node == tail) {
        tail = replacement.tail; 
      }

      node = replacement.head;
      endImpl.node = replacement.tail.next;
      replacement.head = replacement.tail = null;
    }

    
    public void nonDestructiveSplice(RVMSLIntIterator end,
									 RVMIntSpliceList replacement){
      splice(end, new RVMIntSpliceList(replacement));
    }


    public void nonDestructiveSplice(RVMSLIntIterator end,
									 Collection<Integer> replacement){
      splice(end, new RVMIntSpliceList(replacement));
    }

    public void nonDestructiveSplice(RVMSLIntIterator end, int[] replacement){
      splice(end, new RVMIntSpliceList(replacement));
    }


    @Override 
    public String toString(){
      if(node == null) return "<>";
      return node.toString();
    }

    @Override
    public boolean equals(Object o){
      if(o == null) return false;
      if(this == o) return true;
      RVMSLIntIteratorImpl other;
      try{
         other = (RVMSLIntIteratorImpl) o;
      }
      catch(ClassCastException e){
        return false;
      }
      if(node == null){
        if(other.node == null) return true;
        else return false;
      }
      if(other.node == null) return false;
      return   (node.element == other.node.element) 
            && (node.next    == other.node.next   )
            && (node.prev    == other.node.prev   );
    }
  }

  public RVMSLIntIterator head(){
    return new RVMSLIntIteratorImpl(head);
  }

  public RVMSLIntIterator tail(){
    return new RVMSLIntIteratorImpl(tail);
  }

  public static void main(String[] args){
    //these tests are incomplete.  See tests in SpliceList<E> for
    //complete tests
    RVMIntSpliceList l = new RVMIntSpliceList(new int [] {1,2,3,4,5});
    System.out.println(l);
    RVMSLIntIterator first, second;
    first = l.head();
    second = l.tail();
    first.next(1);
    second.previous(1);
    first.nonDestructiveSplice(second,new int[]{});
    System.out.println(l);
    
    l = new RVMIntSpliceList(new int [] {1,2,3,4,5});
    System.out.println(l);
    first = l.head();
    second = l.tail();
    first.next(2);
    second.previous(2);
    first.nonDestructiveSplice(second,new int[]{9,9,9,9,9,9,9,9});
    System.out.println(l);
    System.out.println(l.head());
    System.out.println(l.tail());
    l.addFront(91);
    l.addFront(92);
    System.out.println(l);
    System.out.println(l.head());
    System.out.println(l.tail());
    l.insertBeforeTail(99);
    l.insertBeforeTail(100);
    System.out.println(l);
    System.out.println(l.tail());
  }
}

