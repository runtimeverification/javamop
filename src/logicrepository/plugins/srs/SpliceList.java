package logicrepository.plugins.srs;

import java.util.Collection;

public class SpliceList<E> {

  protected class Node {
    protected E element;
    protected Node prev = null;
    protected Node next = null;

    protected Node(){ 
      element = null; 
    }

    protected Node(E element){
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
      StringBuilder sb = new StringBuilder(element.toString());
      sb.append(" ");
      sb.append((next == null)?"<>":next.toStringNext());
      return sb;
    }
  }

  private Node head;
  private Node tail;

  public SpliceList(){
    head = tail = null;
  }

  public SpliceList(Collection<E> c){
    for(E e : c){
      add(e);
    }
  }

  public SpliceList(SpliceList<E> c){
    SLIterator<E> I = c.head();
    do {
      add(I.get());
    } while(I.next());  
  }

  public SpliceList(E[] c){
    for(E e : c){
      add(e);
    }
  }

  public boolean isEmpty(){
    return head == null;
  }

  public void add(E element){
    if(head == null){
      head = tail = new Node(element);
      return;
    } 
    tail.next = new Node(element);
    tail.next.prev = tail;
    tail = tail.next;
  }

  public void add(Collection<E> c){
    for(E e : c){
      add(e);
    }
  }

  public void add(E[] c){
    for(E e : c){
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
  public class SLIteratorImpl implements SLIterator<E> {
    protected Node node; 

    protected SLIteratorImpl(Node node){
      this.node = node;
    }

    @Override
    public SLIterator<E> copy(){
      return new SLIteratorImpl(node);
    }

    @Override
    public boolean next(){
      if(node.next == null) return false;
      node = node.next;
      return true;
    }

    @Override
    public boolean next(int amount){
      for(int i = 0; i < amount; ++i){
        if(!next()) return false;
      }
      return true;
    }

    @Override
    public boolean previous(){
      if(node.prev == null) return false;
      node = node.prev;
      return true;
    }

    @Override
    public boolean previous(int amount){
      for(int i = 0; i < amount; ++i){
        if(!previous()) return false;
      }
      return true;
    }

    @Override
    public E get(){
      return node.element;
    }

    //WARNING:  This assumes iterators point to the same list!
    @Override
    public void splice(SLIterator<E> end, SpliceList<E> replacement){
      SLIteratorImpl endImpl;
      try {
        endImpl = (SLIteratorImpl) end;
      }
      catch(ClassCastException e){
        throw new IllegalArgumentException("Must provide an SLIteratorImpl to splice");
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

    private void spliceEmptyRepl(SLIteratorImpl endImpl){
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

    private void spliceNonEmptyRepl(SLIteratorImpl endImpl, SpliceList<E> replacement){
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

    @Override
    public void nonDestructiveSplice(SLIterator<E> end, SpliceList<E> replacement){
      splice(end, new SpliceList<E>(replacement));
    }

    @Override
    public void nonDestructiveSplice(SLIterator<E> end, Collection<E> replacement){
      splice(end, new SpliceList<E>(replacement));
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
      SLIteratorImpl other;
      try{
         other = (SLIteratorImpl) o;
      }
      catch(ClassCastException e){
        return false;
      }
      return   (node.element == other.node.element) 
            && (node.next    == other.node.next   )
            && (node.prev    == other.node.prev   );
    }
  }

  public SLIterator<E> head(){
    return new SLIteratorImpl(head);
  }

  public SLIterator<E> tail(){
    return new SLIteratorImpl(tail);
  }

  public static void main(String[] args){
    SpliceList<String> empty = new SpliceList<String>();

    SpliceList<String> sl = new SpliceList<String>(args);
    SpliceList<String> sl2 = new SpliceList<String>(sl);
    SpliceList<String> sl3 = new SpliceList<String>(sl);
    SpliceList<String> sl4 = new SpliceList<String>(sl);
    SpliceList<String> sl5 = new SpliceList<String>(sl);
    SpliceList<String> sl6 = new SpliceList<String>(sl);
    SpliceList<String> sl7 = new SpliceList<String>(sl);
    SpliceList<String> sl8 = new SpliceList<String>(sl);
    SpliceList<String> sl9 = new SpliceList<String>(sl);
    SpliceList<String> sl10 = new SpliceList<String>(sl);
    SpliceList<String> sl11 = new SpliceList<String>(sl);
    SpliceList<String> sl12 = new SpliceList<String>(sl);

    SLIterator<String> H;
    SLIterator<String> T;

    System.out.println(sl.head);
    System.out.println(sl.tail);
    System.out.println(sl);
    H = sl.head();
    do {
      System.out.println(H);
    } while(H.next());

    H = empty.head();
    T = empty.tail();
    System.out.println("========splicing empty to empty========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(empty);
    H.splice(T, new SpliceList<String>());    
    System.out.println("========done========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(empty);
    System.out.println(empty.head());
    System.out.println(empty.tail());


    H = empty.head();
    T = empty.tail();
    System.out.println("========splicing [1 2 3] to empty========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(empty);
    H.splice(T, new SpliceList<String>(new String[] {"1", "2", "3"}));    
    System.out.println("========done========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(empty);
    System.out.println(empty.head());
    System.out.println(empty.tail());


    H = sl.head();
    T = sl.tail();
    T.previous(5);
    System.out.println("========splicing empty to front========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl);
    H.splice(T, new SpliceList<String>());
    System.out.println("========done========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl);
    System.out.println(sl.head());
    System.out.println(sl.tail());


    H = sl2.head();
    T = sl2.head();
    System.out.println("========splicing empty to single front element========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl2);
    H.splice(T, new SpliceList<String>());
    System.out.println("========done========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl2);
    System.out.println(sl2.head());
    System.out.println(sl2.tail());


    H = sl3.head();
    T = sl3.tail();
    H.next(2);
    T.previous(5);
    System.out.println("========splicing empty to middle========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl3);
    H.splice(T, new SpliceList<String>());
    System.out.println("========done========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl3);
    System.out.println(sl3.head());
    System.out.println(sl3.tail());

    H = sl4.head();
    H.next(5);
    T = sl4.tail();
    System.out.println("========splicing empty to back========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl4);
    H.splice(T, new SpliceList<String>());
    System.out.println("========done========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl4);
    System.out.println(sl4.head());
    System.out.println(sl4.tail());

    H = sl5.tail();
    T = sl5.tail();
    System.out.println("========splicing empty to single back element========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl5);
    H.splice(T, new SpliceList<String>());
    System.out.println("========done========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl5);
    System.out.println(sl5.head());
    System.out.println(sl5.tail());

    
    H = sl6.head();
    T = sl6.tail();
    System.out.println("========splicing empty to whole list========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl6);
    H.splice(T, new SpliceList<String>());
    System.out.println("========done========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl6);
    System.out.println(sl6.head());
    System.out.println(sl6.tail());

    H = sl7.head();
    T = sl7.tail();
    T.previous(5);
    System.out.println("========splicing [0 0 0] to front========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl7);
    H.splice(T, new SpliceList<String>(new String[] {"0", "0", "0"}));
    System.out.println("========done========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl7);
    System.out.println(sl7.head());
    System.out.println(sl7.tail());

    H = sl8.head();
    H.next(5);
    T = sl8.tail();
    System.out.println("========splicing [0 0 0] to back========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl8);
    H.splice(T, new SpliceList<String>(new String[] {"0", "0", "0"}));
    System.out.println("========done========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl8);
    System.out.println(sl8.head());
    System.out.println(sl8.tail());

    H = sl9.tail();
    T = sl9.tail();
    System.out.println("========splicing [0 0 0] to single back element========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl9);
    H.splice(T, new SpliceList<String>(new String[] {"0", "0", "0"}));
    System.out.println("========done========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl9);
    System.out.println(sl9.head());
    System.out.println(sl9.tail());


    H = sl10.head();
    T = sl10.tail();
    H.next(2);
    T.previous(2);
    System.out.println("========splicing [0 0 0] to middile========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl10);
    H.splice(T, new SpliceList<String>(new String[] {"0", "0", "0"}));
    System.out.println("========done========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl10);
    System.out.println(sl10.head());
    System.out.println(sl10.tail());


    H = sl11.head();
    T = sl11.tail();
    System.out.println("========splicing [0 0 0] to entirety========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl11);
    H.splice(T, new SpliceList<String>(new String[] {"0", "0", "0"}));
    System.out.println("========done========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl11);
    System.out.println(sl11.head());
    System.out.println(sl11.tail());

    H = sl12.head();
    T = sl12.tail();
    H.next(5);
    T.previous(5);
    System.out.println(
        "========splicing [0 0 0] to middle then [9 9 9] with the same Iterators========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl12);
    H.splice(T, new SpliceList<String>(new String[] {"0", "0", "0"}));
    System.out.println("========done one========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl12);
    System.out.println(sl12.head());
    System.out.println(sl12.tail());
    H.splice(T, new SpliceList<String>(new String[] {"9", "9", "9"}));
    System.out.println("========done two========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl12);
    System.out.println(sl12.head());
    System.out.println(sl12.tail());
  }
}

