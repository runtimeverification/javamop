package logicrepository.plugins.srs;

import java.util.Collection;

public class IntSpliceList {

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
      sb.append(" ");
      sb.append((next == null)?"<>":next.toStringNext());
      return sb;
    }
  }

  private Node head;
  private Node tail;

  public IntSpliceList(){
    head = tail = null;
  }

  public IntSpliceList(Collection<Integer> c){
    for(Integer e : c){
      add(e);
    }
  }

  public IntSpliceList(IntSpliceList c){
    SLIIntIterator I = c.head();
    do {
      add(I.get());
    } while(I.next());  
  }

  public IntSpliceList(int[] c){
    for(int e : c){
      add(e);
    }
  }

  public boolean isEmpty(){
    return head == null;
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
  public class SLIIntIterator {
    protected Node node; 

    protected SLIIntIterator(Node node){
      this.node = node;
    }

    public SLIIntIterator copy(){
      return new SLIIntIterator(node);
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
    public void splice(SLIIntIterator end, IntSpliceList replacement){
      SLIIntIterator endImpl;
      try {
        endImpl = (SLIIntIterator) end;
      }
      catch(ClassCastException e){
        throw new IllegalArgumentException("Must provide an SLIIntIterator to splice");
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

    private void spliceEmptyRepl(SLIIntIterator endImpl){
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

    private void spliceNonEmptyRepl(SLIIntIterator endImpl, IntSpliceList replacement){
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

    
    public void nonDestructiveSplice(SLIIntIterator end, IntSpliceList replacement){
      splice(end, new IntSpliceList(replacement));
    }


    public void nonDestructiveSplice(SLIIntIterator end, Collection<Integer> replacement){
      splice(end, new IntSpliceList(replacement));
    }

    public void nonDestructiveSplice(SLIIntIterator end, int[] replacement){
      splice(end, new IntSpliceList(replacement));
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
      SLIIntIterator other;
      try{
         other = (SLIIntIterator) o;
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

  public SLIIntIterator head(){
    return new SLIIntIterator(head);
  }

  public SLIIntIterator tail(){
    return new SLIIntIterator(tail);
  }

  public static void main(String[] args){
    //these tests are incomplete.  See tests in SpliceList<E> for
    //complete tests
    IntSpliceList l = new IntSpliceList(new int [] {1,2,3,4,5});
    System.out.println(l);
    SLIIntIterator first, second;
    first = l.head();
    second = l.tail();
    first.next(1);
    second.previous(1);
    first.nonDestructiveSplice(second,new int[]{});
    System.out.println(l);
    
    l = new IntSpliceList(new int [] {1,2,3,4,5});
    System.out.println(l);
    first = l.head();
    second = l.tail();
    first.next(2);
    second.previous(2);
    first.nonDestructiveSplice(second,new int[]{9,9,9,9,9,9,9,9});
    System.out.println(l);
  }
}

